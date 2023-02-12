package ch.so.agi.sodata.fgb;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConverterService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String PYTHON = "python";
    private static final String SOURCE_FILE_NAME = "FlatGeobufConverter.py";

    @PostConstruct
    public void init() throws IOException {        
        // Wenn man nur von Docker als Runtime ausgehen würde, könnte
        // auf das if/else und Zippen verzichten und beim Herstellungsprozess
        // des Images die Python-Abhängigkeiten ans richtige Ort kopieren.
        // Dann würde aber die Anwendung nicht mehr als normale Fatjar
        // auf einer GraalVM funktionieren.
        File venvExeFile = Paths.get("venv","bin","graalpy").toFile();
        if (!venvExeFile.exists()) {
            // Unzipperei...
        }
        String venvExePath = venvExeFile.getAbsolutePath();
        
        
        log.debug("<venvExePath> {}", venvExePath);

        
        // Eventuell lohnt sich eine eigener Actuator. Dann kann man die Anwendung hochfahren und sie ist live UND ready.
        // Mit Scheduler ausführen und mit simplen Key (als env var)
        
    }
}
