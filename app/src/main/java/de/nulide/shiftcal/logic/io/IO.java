package de.nulide.shiftcal.logic.io;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import de.nulide.shiftcal.logic.io.object.JSONSettings;
import de.nulide.shiftcal.logic.io.object.JSONShiftCalendar;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.tools.Alarm;

public class IO {

    public static final String JSON_SC_FILE_NAME = "sc.json";
    private static final String JSON_SETTINGS_FILE_NAME = "settings.json";


    public static void exportShiftCal(File dir, FileOutputStream fos){
        PrintWriter out = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            ShiftCalendar sc = readShiftCal(dir);
            out = new PrintWriter(fos);
            out.write(mapper.writeValueAsString(
                    JSONFactory.convertShiftCalendarToJSON(sc)));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void importShiftCal(File dir, Context c, InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String json = readJSON(br);
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
        if(!json.isEmpty()) {
            try {
                ShiftCalendar sc =  JSONFactory.convertJSONToShiftCalendar(
                        mapper.readValue(json, JSONShiftCalendar.class));
                writeShiftCal(dir, c, sc);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public static ShiftCalendar readShiftCal(File dir) {
        File newFile = new File(dir, JSON_SC_FILE_NAME);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
            ShiftCalendar sc;
            try {
                sc = JSONFactory.convertJSONToShiftCalendar(
                        mapper.readValue(readJSONFromFile(newFile), JSONShiftCalendar.class));
                return sc;
            } catch (JsonProcessingException e) {
                e.printStackTrace();


        }

        return new ShiftCalendar();
    }

    public static void writeShiftCal(File dir, Context c, ShiftCalendar sc) {
        Alarm alarm = new Alarm(dir);
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(dir, JSON_SC_FILE_NAME);
        try {
            String json = mapper.writeValueAsString(JSONFactory.convertShiftCalendarToJSON(sc));
            writeJSON(file, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        alarm.setAlarm(c);
        alarm.setDNDAlarm(c);
    }

    public static Settings readSettings(File dir) {
        ObjectInputStream input;
        File file = new File(dir, JSON_SETTINGS_FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        String json = readJSONFromFile(file);
        if(!json.isEmpty()) {
            try {
                return JSONFactory.convertJSONToSettings(
                        mapper.readValue(json, JSONSettings.class));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return new Settings();
    }

    public static void writeSettings(File dir, Context c, Settings s) {
        Alarm alarm = new Alarm(dir);
        File file = new File(dir, JSON_SETTINGS_FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(JSONFactory.convertSettingsToJSON(s));
            writeJSON(file, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        alarm.setAlarm(c);
    }

    public static void writeJSON(File file, String json){
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file);
            out.write(json);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readJSONFromFile(File file){
        StringBuilder json = new StringBuilder();
        try {
            if(file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                return readJSON(br);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new String();
    }

    public static String readJSON(BufferedReader br){
        StringBuilder json = new StringBuilder();
        try {
                String line;

                while ((line = br.readLine()) != null) {
                    json.append(line);
                    json.append('\n');
                }
                br.close();
                return json.toString();

        } catch (JsonProcessingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String();
    }


}
