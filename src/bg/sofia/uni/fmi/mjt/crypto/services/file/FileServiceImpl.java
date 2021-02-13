package bg.sofia.uni.fmi.mjt.crypto.services.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public class FileServiceImpl implements FileService {
    private Gson serializer;

    public FileServiceImpl() {
        this.serializer = new GsonBuilder().create();
    }

    public StringBuilder readFile(File file) {
        StringBuilder jsonString = new StringBuilder();
        synchronized (this) {
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader reader = new BufferedReader(fileReader)) {

                String line = reader.readLine();
                while(line != null) {
                    jsonString.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonString;
    }

    public synchronized void updateFile(Object objectToSave, String filePath) {
        File usersFile = new File(filePath);
        boolean success = true;

        File backupFile = null;
        if(usersFile.exists() && !usersFile.isDirectory()) {
            backupFile = new File(filePath + "_backup");
            if(!usersFile.renameTo(backupFile)){
                //todo: handle error
            }
        }

        try(FileWriter fileOut = new FileWriter(filePath);
            BufferedWriter objectOut = new BufferedWriter(fileOut)) {
            objectOut.write(serializer.toJson(objectToSave));
        } catch (IOException e) {
            success = false;
            //todo: handle error
            e.printStackTrace();
        }

        if(!success && backupFile != null) {
            backupFile.delete();
        }
    }
}
