local sprite = {}

-- modules
local asset = require("libraries/rpgwizard/asset")
local animation = require("libraries/rpgwizard/animation")

function sprite.load(name)
    local asset_name = "sprites/" .. name

    local sprite_asset = asset.load_json(asset_name)

    -- Runtime data, setup anim8
    sprite_asset.anim8 = {}
    for k, v in pairs(sprite_asset.animations) do
        if v ~= nil and v ~= '' then
            sprite_asset.anim8[k] = animation.load(v)
        end
    end

    return sprite_asset
end

function sprite.update(dt)

end

function sprite.draw(sprite_asset)
    if sprite_asset.active_animation == nil then
        return
    end

    -- TODO: Optimize, many function calls
    local active_animation = sprite_asset.active_animation
    local image = asset.load_texture(active_animation.spriteSheet.image)
    local x = sprite_asset.collider:getX() - math.floor(active_animation.width / 2)
    local y = sprite_asset.collider:getY() - math.floor(active_animation.height / 2)
    active_animation.animator:draw(image, x, y)
end

function sprite.set_location(sprite_asset, x, y, layer)
    sprite_asset.collider:setPosition(x, y)
    sprite_asset.layer = layer
end

return sprite
