-- SCRIPT_TYPE(MAP_LINK)
-- AUTO-GENERATED - DO NOT MODIFY DIRECTLY
local script = { source = nil, target = nil }

-- rpg
local rpg = require("libraries/rpgwizard/rpg")

function script.update(dt)

    rpg.switch_map(mapName, tileX, tileY, layer)

    return true
end

return script
