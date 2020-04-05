package de.nulide.shiftcal.logic.io;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import de.nulide.shiftcal.logic.io.object.SerialShiftCalendar;
import de.nulide.shiftcal.logic.object.ShiftCalendar;
import de.nulide.shiftcal.tools.Alarm;

public class CalendarIO {

    public static final String FILE_NAME = "sc.o";

    public static void exportShiftCal(File dir, FileOutputStream fos){
        ObjectOutput out = null;
        try {
            ShiftCalendar sc = readShiftCal(dir);
            out = new ObjectOutputStream(fos);
            out.writeObject(SerialFactory.convertShiftCalendarToSerial(sc));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            writeShiftVal(dir, c, sc);
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
        ObjectInputStream input;
        try {
            input = new ObjectInputStream(new FileInputStream(new File(dir, FILE_NAME)));
            SerialShiftCalendar readSC = (SerialShiftCalendar) input.readObject();
            input.close();
            return SerialFactory.convertSerialToShiftCalendar(readSC);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ShiftCalendar();
    }

    public static void writeShiftVal(File dir, Context c, ShiftCalendar sc) {
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
            out.writeObject(SerialFactory.convertShiftCalendarToSerial(sc));
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        alarm.setAlarm(c);
    }
}
