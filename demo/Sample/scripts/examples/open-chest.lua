local script = { source = nil, target = nil }

-- rpg
local rpg = require("libraries/rpgwizard/rpg")

function script.update(dt)

    local map_sprite = {
        startLocation = {
            x = math.random(100, 350),
            y = math.random(100, 130),
            layer = 2
        },
        thread = nil,
        asset = "chest.sprite",
        event = {
            type = "keypress",
            script = "examples/open-chest.lua",
            key = "e"
        }
    }

    rpg.add_sprite("my-chest-" .. math.random(1, 100), map_sprite)

end

return script
