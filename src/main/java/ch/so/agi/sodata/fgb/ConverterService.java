package ch.so.agi.sodata.fgb;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConverterService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void convert() {
        
        try {
            ProcessBuilder pb = new ProcessBuilder("ogr2ogr", "--version");
            //pb.directory(new File("myDir"));
            Process p = pb.start();
            BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = is.readLine()) != null)
                log.info(line);
            p.waitFor();
            
            
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Scheduler für Converting
    
    // Scheduler für Cleaning
}
