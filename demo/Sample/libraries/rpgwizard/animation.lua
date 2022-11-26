local animation = {}

-- modules
local asset = require("libraries/rpgwizard/asset")

-- libraries
local anim8 = require("libraries/anim8")

function animation.load(name)
    local asset_name = "animations/" .. name

    local animation_asset = asset.load_json(asset_name)

    local image = asset.load_texture(animation_asset.spriteSheet.image)

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
