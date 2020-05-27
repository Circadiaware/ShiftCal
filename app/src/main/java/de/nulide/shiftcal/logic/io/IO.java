package de.nulide.shiftcal.logic.io;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.util.HashMap;

import de.nulide.shiftcal.logic.io.object.JSONSettings;
import de.nulide.shiftcal.logic.io.object.JSONShiftCalendar;
import de.nulide.shiftcal.logic.io.object.SerialShiftCalendar;
import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.tools.Alarm;

public class IO {

    public static final String SERIAL_SC_FILE_NAME = "sc.o";
    public static final String JSON_SC_FILE_NAME = "sc.json";
    private static final String JSON_SETTINGS_FILE_NAME = "settings.json";


    public static void exportShiftCal(File dir, FileOutputStream fos){
        PrintWriter out = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            ShiftCalendar sc = readShiftCal(dir);
            out = new PrintWriter(fos);
            out.write(mapper.writeValueAsString(
                    SerialFactory.convertShiftCalendarToSerial(sc)));
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void importShiftCal(File dir, Context c, InputStream is) {
        ObjectInputStream input;
        try {
            input = new ObjectInputStream(is);
            SerialShiftCalendar readSC = (SerialShiftCalendar) input.readObject();
            input.close();
            ShiftCalendar sc = SerialFactory.convertSerialToShiftCalendar(readSC);
            writeShiftCal(dir, c, sc);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static ShiftCalendar readShiftCal(File dir) {
        File oldFile = new File(dir, SERIAL_SC_FILE_NAME);
        File newFile = new File(dir, JSON_SC_FILE_NAME);
        if(oldFile.exists()){
            ObjectInputStream input;
            try {
                input = new ObjectInputStream(new FileInputStream(oldFile));
                SerialShiftCalendar readSC = (SerialShiftCalendar) input.readObject();
                input.close();
                ShiftCalendar sc = SerialFactory.convertSerialToShiftCalendar(readSC);
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String json = mapper.writeValueAsString(JSONFactory.convertShiftCalendarToJSON(sc));
                    writeJSON(newFile, json);
                    oldFile.delete();
                    return sc;
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else if(newFile.exists()){
            ObjectMapper mapper = new ObjectMapper();
            ShiftCalendar sc = new ShiftCalendar();
            try {
                sc = JSONFactory.convertJSONToShiftCalendar(
                        mapper.readValue(readJSON(newFile), JSONShiftCalendar.class));
                return sc;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }


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
    }

    public static Settings readSettings(File dir) {
        ObjectInputStream input;
        File file = new File(dir, JSON_SETTINGS_FILE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        String json = readJSON(file);
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

    public static String readJSON(File file){
        StringBuilder json = new StringBuilder();
        try {
            if(file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    json.append(line);
                    json.append('\n');
                }
                br.close();
                return json.toString();
            }
        } catch (JsonProcessingException | FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String();
    }
}
