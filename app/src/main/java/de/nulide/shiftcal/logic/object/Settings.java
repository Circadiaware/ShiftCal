package de.nulide.shiftcal.logic.object;

import java.util.HashMap;

public class Settings {

    // FinalSettings
    public static final String SET_ALARM_ON_OFF = "ALARM";
    public static final String SET_ALARM_MINUTES = "ALARM-Minutes";
    public static final String SET_ALARM_TONE = "ALARM_TONE";
    public static final String SET_COLOR = "COLOR";
    public static final String SET_DARK_MODE = "DARK_MODE";
    public static final String SET_DND = "DND";
    public static final String SET_START_OF_WEEK = "START_OF_WEEK";
    public static final String SET_SYNC = "SYNC";
    public static final String SET_MATERIALYOUCOLOR = "MYOUCOLOR";


    private HashMap<String, String> settings;

    public Settings() {
        settings = new HashMap<>();
    }

    public Settings(HashMap<String, String> s) {
        settings = s;
    }

    public boolean isAvailable(String setting) {
        return settings.containsKey(setting);
    }

    public String getSetting(String setting) {
        return settings.get(setting);
    }

    public void setSetting(String setting, String value) {
        settings.remove(setting);
        settings.put(setting, value);
    }

    public HashMap<String, String> getMap() {
        return settings;
    }
}
