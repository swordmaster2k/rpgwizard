local asset = {}

-- libraries
local json = require("scripts/libraries/json")

function asset.load_json(cache, name)
    local contents, size = love.filesystem.read(name)

    -- TODO: error handling

    local json_data = json.decode(contents)

    -- TODO: error handling

    return json_data
end

function asset.load_texture(cache, name)
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

function asset.draw_texture(image)
    -- TODO
end

return asset
