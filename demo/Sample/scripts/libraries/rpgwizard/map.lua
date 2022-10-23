local map = {}

-- modules
local asset = require("scripts/libraries/rpgwizard/asset")
local sprite = require("scripts/libraries/rpgwizard/sprite")
local tileset = require("scripts/libraries/rpgwizard/tileset")

local function init_layer_collider(id, cache, world, instance)
    -- TODO: move this to compile time
    local points = {}
    for i, point in pairs(instance.points) do
        table.insert(points, point.x)
        table.insert(points, point.y)
    end

    -- Runtime data, setup collider
    instance.collider = world:newPolygonCollider(points)
    instance.collider:setCollisionClass("Solid")
    instance.collider:setObject(instance)
    instance.collider:setType("static")
end

local function init_layer_sprite(id, cache, world, instance)
    -- Runtime data
    local sprite_asset = sprite.load(cache, instance.asset)
    instance.asset = sprite_asset

    -- Assign active animation
    instance.active_animation = {}
    instance.active_animation = sprite_asset.anim8["SOUTH"] -- TODO: Default to "idle"

    -- Runtime data, setup collider
    -- TODO: switch to circular collider in sprite format
    instance.collider = world:newCircleCollider(instance.startLocation.x, instance.startLocation.y, 8)
    instance.collider:setCollisionClass("Sprite")
    instance.collider:setFixedRotation(true)
    instance.collider:setObject(instance)
    instance.collider:setLinearDamping(2)
    instance.collider:setMass(math.huge)

    -- Runtime data, setup trigger
    -- TODO: switch to circular trigger in sprite format
    instance.trigger = world:newCircleCollider(instance.startLocation.x, instance.startLocation.y, 12)
    instance.trigger:setCollisionClass("Trigger")
    instance.trigger:setFixedRotation(true)
    instance.trigger:setObject(instance)
    instance.trigger:setLinearDamping(2)
    instance.trigger:setMass(math.huge)
end

function map.load(cache, world, name)
    local asset_name = "maps/" .. name

    local map_asset = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        map_asset = cache[asset_name]
    else
        map_asset = asset.load_json(cache, asset_name)
        cache[asset_name] = map_asset
    end

    -- Load tilesets
    for i, tileset_name in pairs(map_asset.tilesets) do
        tileset.load(cache, tileset_name)
    end

    -- TODO: Load music

    -- Iterate layers
    for i, layer in pairs(map_asset.layers) do

        -- Iterate colliders
        for id, instance in pairs(layer.colliders) do
            init_layer_collider(id, cache, world, instance)
            instance.layer = i
        end

        -- Iterate triggers
        -- TODO

        -- Iterate sprites
        for id, instance in pairs(layer.sprites) do
            init_layer_sprite(id, cache, world, instance)
            instance.layer = i
        end

    end

    return map_asset
end

function map.update(dt, current_map)
    for i, layer in pairs(current_map.layers) do
        for j, map_sprite in pairs(layer.sprites) do

            -- Threads
            -- TODO

            -- Animations
            if map_sprite.active_animation ~= nil then
                map_sprite.active_animation.animator:update(dt)
            end

        end
    end
end

function map.draw(cache, world, current_map, canvas, scale)
    love.graphics.setCanvas(canvas)

    -- TODO: Optimize
    for i, layer in pairs(current_map.layers) do

        tileset.draw(cache, current_map, layer)

        for j, map_sprite in pairs(layer.sprites) do
            sprite.draw(cache, map_sprite)
        end

        for j, image in pairs(layer.images) do
            asset.draw_texture(image)
        end

    end

    -- TODO: debug only
    world:draw()

    love.graphics.setCanvas()

    love.graphics.draw(canvas, 0, 0, 0, scale, scale)
end

return map
