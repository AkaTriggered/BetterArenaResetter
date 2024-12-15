package org.akatriggered.betterarenaresetter.properties;

import org.akatriggered.betterarenaresetter.ArenaProperty;

public class AutoRespawnProperty extends ArenaProperty.BooleanProperty {

    @Override
    public Boolean getDefault() {
        return false;
    }

    @Override
    public String getName() {
        return "autorespawn";
    }

}
