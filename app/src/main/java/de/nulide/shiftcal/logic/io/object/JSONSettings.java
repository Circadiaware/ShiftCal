package de.nulide.shiftcal.logic.io.object;

import java.util.HashMap;

public class JSONSettings {

    private HashMap<String, String> settings;

    public JSONSettings() {
        settings = new HashMap<>();
    }

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    }
}
