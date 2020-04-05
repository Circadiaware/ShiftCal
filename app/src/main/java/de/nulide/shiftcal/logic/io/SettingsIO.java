package de.nulide.shiftcal.logic.io;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;

import de.nulide.shiftcal.logic.object.Settings;
import de.nulide.shiftcal.tools.Alarm;

public class SettingsIO {

    private static final String FILE_NAME = "settings.o";


    public static Settings readSettings(File dir) {
        ObjectInputStream input;
        try {
            input = new ObjectInputStream(new FileInputStream(new File(dir, FILE_NAME)));
            HashMap<String, String> readS = (HashMap<String, String>) input.readObject();
            input.close();
            return new Settings(readS);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new Settings();
    }

    public static void writeSettings(File dir, Context c, Settings s) {
        Alarm alarm = new Alarm(dir);
        File file = new File(dir, FILE_NAME);
        ObjectOutput out = null;
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(s.getMap());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        alarm.setAlarm(c);
    }
}
