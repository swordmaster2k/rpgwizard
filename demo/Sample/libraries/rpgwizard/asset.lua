local asset = {
    cache = {}
}

-- libraries
local json = require("libraries/json")

function asset.load_json(name)
    local contents, size = love.filesystem.read(name)

    -- TODO: error handling

    if asset.cache[name] ~= nil then
        return asset.deep_copy(asset.cache[name])
    end

    asset.cache[name] = json.decode(contents)

    -- TODO: error handling

    return asset.deep_copy(asset.cache[name])
end

function asset.load_texture(name)
    local asset_name = "textures/" .. name

    -- Check the cache
    if asset.cache[asset_name] ~= nil then
        return asset.cache[asset_name]
    end

    local new_image = love.graphics.newImage(asset_name)

    -- Store in cache
    asset.cache[asset_name] = new_image

    return new_image
end

function asset.draw_texture(image)
    -- TODO
end

function asset.deep_copy(original)
    local copy = {}
    for k, v in pairs(original) do
        if type(v) == "table" then
            v = asset.deep_copy(v)
        end
        copy[k] = v
    end

    return copy
end

return asset
