package com.example.carbt.files;

import android.content.Context;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileHandler extends Fragment {
    public void save(String filename, String fileContents) {
        try (FileOutputStream fos = this.requireContext().openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(fileContents.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read(Context context, String filename) {
        FileInputStream fis;
        String contents;
        try {
            fis = context.openFileInput(filename
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            // Error occurred when opening raw file for reading.
        } finally {
            contents = stringBuilder.toString();
        }
        return contents;
    }
}
