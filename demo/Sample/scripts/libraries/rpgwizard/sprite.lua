local sprite = {}

-- modules
local asset = require("scripts/libraries/rpgwizard/asset")
local animation = require("scripts/libraries/rpgwizard/animation")

function sprite.load(cache, name)
    local asset_name = "sprites/" .. name

    local sprite_asset = nil

    -- Check the cache
    -- Slightly different here as we don't want to store Runtime data but
    -- we do want to load the original asset from the cache for speed
    if cache[asset_name] ~= nil then
        sprite_asset = cache[asset_name]
    else
        sprite_asset = asset.load_json(cache, asset_name)
        cache[asset_name] = sprite_asset
    end

    -- Runtime data, setup anim8
    sprite_asset.anim8 = {}
    for k, v in pairs(sprite_asset.animations) do
        if v ~= nil and v ~= '' then
            sprite_asset.anim8[k] = animation.load(cache, v)
        end
    end

    return sprite_asset
end

function sprite.update(dt)

end

function sprite.draw(cache, sprite)
    if sprite.active_animation == nil then
        return
    end

    -- TODO: Optimize, many function calls
    local active_animation = sprite.active_animation
    local image = asset.load_texture(cache, active_animation.spriteSheet.image)
    local x = sprite.collider:getX() - math.floor(active_animation.width / 2)
    local y = sprite.collider:getY() - math.floor(active_animation.height / 2)
    active_animation.animator:draw(image, x, y)
end

return sprite
