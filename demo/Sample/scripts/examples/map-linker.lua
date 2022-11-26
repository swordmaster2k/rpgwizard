local script = { source = nil, target = nil }

-- rpg
local rpg = require("libraries/rpgwizard/rpg")

function script.update(dt)

    rpg.switch_map("start.map", 15, 10, 2)

    return true
end

return script
