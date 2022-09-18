local rpg = { _version = "0.0.1" }
local json = require("json")

local cache = {}

local current_map = nil

--- ###############################################################################################
--- Asset Loading
--- ###############################################################################################

local function load_json(name)
    local contents, size = love.filesystem.read(name)

    -- TODO: error handling

    local json_data = json.decode(contents)

    -- TODO: error handling

    return json_data
end

function rpg.load_texture(name)
    local asset_name = "textures/" .. name

    -- Check the cache
    if cache[asset_name] ~= nil then
        return cache[asset_name]
    end

    local new_image = love.graphics.newImage(asset_name)

    -- Store in cache
    cache[asset_name] = new_image

    return new_image
end

function rpg.load_animation(name)
    local asset_name = "animations/" .. name

    -- Check the cache
    if cache[asset_name] ~= nil then
        return cache[asset_name]
    end

    local animation = load_json(asset_name)
    rpg.load_texture(animation.spriteSheet.image)

    -- Store in cache
    cache[asset_name] = animation

    return animation

end

function rpg.load_sprite(name)
    local asset_name = "sprites/" .. name

    local sprite = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        sprite = cache[asset_name]
    else
        sprite = load_json(asset_name)
        cache[asset_name] = sprite
    end

    for k, v in pairs(sprite.animations) do
        if v ~= nil and v ~= '' then
            rpg.load_animation(v)
        end
    end

    return sprite

end

function rpg.load_tileset(name)
    local asset_name = "tilesets/" .. name

    -- Check the cache
    if cache[asset_name] ~= nil then
        return cache[asset_name]
    end

    local tileset = load_json(asset_name)
    local tileset_image = rpg.load_texture(tileset.image)

    -- Runtime data
    local tile_width = tileset.tileWidth
    local tile_height = tileset.tileHeight
    local image_width = tileset_image.getWidth(tileset_image)
    local image_height = tileset_image.getHeight(tileset_image)

    tileset.rows = math.floor(image_height / tile_height)
    tileset.columns = math.floor(image_width / tile_width)
    tileset.count = tileset.rows * tileset.columns
    tileset.quads = {}

    -- Store the tiles in a 1D linear array
    for row = 0, tileset.rows - 1, 1 do
        for column = 0, tileset.columns - 1, 1 do
            local x = column * tile_width
            local y = row * tile_height
            local quad = love.graphics.newQuad(x, y, tile_width, tile_height, image_width, image_height)
            table.insert(tileset.quads, quad)
        end
    end

    -- Store in cache
    cache[asset_name] = tileset

    return tileset
end

function rpg.load_map(name)
    local asset_name = "maps/" .. name

    local map = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        map = cache[asset_name]
    else
        map = load_json(asset_name)
        cache[asset_name] = map
    end

    -- Load tilesets
    for i, tileset_name in pairs(map.tilesets) do
        rpg.load_tileset(tileset_name)
    end

    -- TODO: Load music

    -- Iterate layers
    for i, layer in pairs(map.layers) do
        for j, instance in pairs(layer.sprites) do

            -- Runtime data
            local sprite = rpg.load_sprite(instance.asset)
            instance.active_animation = sprite.animations["SOUTH"]
            instance.x = instance.startLocation.x
            instance.y = instance.startLocation.y
            instance.layer = instance.startLocation.layer

        end
    end

    current_map = map

    return map
end

--- ###############################################################################################
---
--- Asset Drawing
---
--- ###############################################################################################

local function drawImage(image)
    -- TODO
end

local function drawSprite(sprite)
    if sprite.active_animation == nil then
        return
    end

    -- TODO: Optimize, many function calls
    local animation = rpg.load_animation(sprite.active_animation)
    local spriteSheet = animation.spriteSheet
    local image = rpg.load_texture(animation.spriteSheet.image)
    local image_width = image.getWidth(image)
    local image_height = image.getHeight(image)
    local quad = love.graphics.newQuad(spriteSheet.x, spriteSheet.y, animation.width, animation.height, image_width,
        image_height)
    local x = sprite.x - math.floor(animation.width / 2)
    local y = sprite.y - math.floor(animation.height / 2)

    love.graphics.draw(image, quad, x, y)
end

local function drawTile(tile, row, column)
    -- TODO: Optimize, slow string split
    local tileset_index, tile_index = string.match(tile, "(%-?%d+):(%-?%d+)")
    tileset_index = tonumber(tileset_index)
    tile_index = tonumber(tile_index)
    --- TODO: end Optimize

    if tileset_index > -1 and tile_index > -1 then
        local tileset_name = current_map.tilesets[tileset_index + 1]
        local tileset = rpg.load_tileset(tileset_name)
        local tileset_image = rpg.load_texture(tileset.image)
        local quad = tileset.quads[tile_index + 1]

        if quad ~= nil then
            local xx = row * current_map.tileWidth
            local yy = column * current_map.tileHeight
            love.graphics.draw(tileset_image, quad, xx, yy)
        end
    end
end

local function drawTiles(layer)
    -- Draw the tiles
    for column = 0, current_map.height - 1, 1 do
        for row = 0, current_map.width - 1, 1 do
            local current_tile = column * current_map.width + row + 1
            local tile = layer.tiles[current_tile]
            drawTile(tile, row, column)
        end
    end
end

function rpg.drawMap()
    if current_map == nil then
        return
    end

    for i, layer in pairs(current_map.layers) do

        drawTiles(layer)

        for j, sprite in pairs(layer.sprites) do
            drawSprite(sprite)
        end

        for j, image in pairs(layer.images) do
            drawImage(image)
        end

    end

end

--- ###############################################################################################
---
--- Client API
---
--- ###############################################################################################

function rpg.getSprite(id)
    if current_map == nil then
        error("invalid state: no map is loaded")
    end

    -- TODO: Optimize
    for i, layer in pairs(current_map.layers) do
        local sprite = layer.sprites[id]
        if sprite ~= nil then
            return sprite
        end
    end

    return nil

end

return rpg
