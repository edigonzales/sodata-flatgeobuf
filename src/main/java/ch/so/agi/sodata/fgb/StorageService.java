package ch.so.agi.sodata.fgb;

import java.io.File;
import java.io.IOException;

//https://github.com/spring-guides/gs-uploading-files/blob/main/complete/src/main/java/com/example/uploadingfiles/storage/FileSystemStorageService.java

public interface StorageService {
    //void init();

    void store(File file, String filename, String location) throws IOException;
    
    File load(String filename);

    void delete(String filename);
}
