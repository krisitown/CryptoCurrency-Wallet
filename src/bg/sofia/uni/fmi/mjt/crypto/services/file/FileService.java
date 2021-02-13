package bg.sofia.uni.fmi.mjt.crypto.services.file;

import java.io.File;

public interface FileService {
    StringBuilder readFile(File file);
    void updateFile(Object objectToSave, String filePath);
}
