-- SCRIPT_TYPE(MAP_LINK)
-- AUTO-GENERATED - DO NOT MODIFY DIRECTLY
local script = { source = nil, target = nil }

-- rpg
local rpg = require("libraries/rpgwizard/rpg")

function script.update(dt)

    rpg.switch_map("outside.map", 6.5, 8.5, 1)

    return true
end

return script
