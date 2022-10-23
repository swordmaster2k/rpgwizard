local animation = {}

-- modules
local asset = require("scripts/libraries/rpgwizard/asset")

-- libraries
local anim8 = require("scripts/libraries/anim8")

function animation.load(cache, name)
    local asset_name = "animations/" .. name

    local animation_asset = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        animation_asset = cache[asset_name]
    else
        animation_asset = asset.load_json(cache, asset_name)
        cache[asset_name] = animation_asset
    end

    local image = asset.load_texture(cache, animation_asset.spriteSheet.image)

    -- Runtime data, setup anim8
    local g = anim8.newGrid(animation_asset.width, animation_asset.height, image:getWidth(), image:getHeight())
    local row = (animation_asset.spriteSheet.y / animation_asset.height) + 1
    local col_start = (animation_asset.spriteSheet.x / animation_asset.width) + 1
    local col_end = col_start + (animation_asset.spriteSheet.width / animation_asset.width) - 1
    local range = tostring(col_start) .. '-' .. tostring(col_end)
    animation_asset.animator = anim8.newAnimation(g(range, row), 1 / animation_asset.frameRate)

    return animation_asset
end

return animation
