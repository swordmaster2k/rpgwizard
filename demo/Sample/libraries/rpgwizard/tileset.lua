local tileset = {
    cache = {}
}

-- modules
local asset = require("libraries/rpgwizard/asset")

function tileset.load(name)
    local asset_name = "tilesets/" .. name

    if tileset.cache[name] then
        return tileset.cache[name]
    end

    local tileset_asset = asset.load_json(asset_name)
    local tileset_image = asset.load_texture(tileset_asset.image)

    -- Runtime data
    local tile_width = tileset_asset.tileWidth
    local tile_height = tileset_asset.tileHeight
    local image_width = tileset_image:getWidth()
    local image_height = tileset_image:getHeight()

    tileset_asset.rows = math.floor(image_height / tile_height)
    tileset_asset.columns = math.floor(image_width / tile_width)
    tileset_asset.quads = {}

    -- Store the tiles in a 1D linear array
    for row = 0, tileset_asset.rows - 1, 1 do
        for column = 0, tileset_asset.columns - 1, 1 do
            local x = column * tile_width
            local y = row * tile_height
            local quad = love.graphics.newQuad(x, y, tile_width, tile_height, image_width, image_height)
            table.insert(tileset_asset.quads, quad)
        end
    end

    -- Extra cache, commonly used
    tileset.cache[name] = tileset_asset

    return tileset_asset
end

function tileset.update(dt)

end

local function drawTile(current_map, tile, row, column)
    local tileset_index = tile.tilesetIndex
    local tile_index = tile.tileIndex

    if tileset_index > -1 and tile_index > -1 then
        local tileset_name = current_map.tilesets[tileset_index + 1]
        local tileset_asset = tileset.load(tileset_name)
        local tileset_image = asset.load_texture(tileset_asset.image)
        local quad = tileset_asset.quads[tile_index + 1]

        if quad ~= nil then
            local xx = row * current_map.tileWidth
            local yy = column * current_map.tileHeight
            love.graphics.draw(tileset_image, quad, xx, yy)
        end
    end
end

function tileset.draw(current_map, layer)
    -- Draw the tiles
    for column = 0, current_map.height - 1, 1 do
        for row = 0, current_map.width - 1, 1 do
            local current_tile = column * current_map.width + row + 1
            local tile = layer.tiles[current_tile]
            drawTile(current_map, tile, row, column)
        end
    end
end

return tileset
