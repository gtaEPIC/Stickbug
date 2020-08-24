package com.example.stickbug.savedata;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class savedata {

    private File file;

    public savedata(File file) {
        this.file = file;
    }

    public int getRepeats() {
        try {
            FileReader reader = new FileReader(file);
            Properties props = new Properties();
            props.load(reader);
            reader.close();
            return Integer.parseInt(props.getProperty("repeats", "0"));
        }catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void saveRepeats(int repeats) {
        try {
            Properties props = new Properties();
            props.setProperty("repeats", String.valueOf(repeats));
            FileWriter writer = new FileWriter(file);
            props.store(writer, "Epic Gamer Don't Cheat");
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
