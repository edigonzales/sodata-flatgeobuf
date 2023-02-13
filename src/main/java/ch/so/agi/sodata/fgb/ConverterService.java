package ch.so.agi.sodata.fgb;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConverterService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String PYTHON = "python";
    private static final String SOURCE_FILE_NAME = "FlatGeobufConverter.py";

    private String venvExePath;
    
    @PostConstruct
    public void init() throws IOException {        
        // Wenn man nur von Docker als Runtime ausgehen würde, könnte
        // auf das if/else und Zippen verzichten und beim Herstellungsprozess
        // des Images die Python-Abhängigkeiten ans richtige Ort kopieren.
        // Dann würde aber die Anwendung nicht mehr als normale Fatjar
        // auf einer GraalVM funktionieren. Wobei im vorliegenden Fall
        // gibt es auch noch GDAL/OGR als Abhängigkeit. 
        File venvExeFile = Paths.get("venv","bin","graalpy").toFile();
        if (!venvExeFile.exists()) {
            // Unzipperei...
        }
        venvExePath = venvExeFile.getAbsolutePath();
        
        log.debug("<venvExePath> {}", venvExePath);        
    }
    
    public void convert() throws IOException {
        InputStreamReader code = new InputStreamReader(ConverterService.class.getClassLoader().getResourceAsStream(SOURCE_FILE_NAME));

        try (Context context = Context.newBuilder("python")
                .allowAllAccess(true)
                .option("python.Executable", venvExePath)
                .option("python.ForceImportSite", "true")
                .build()) {
            
            context.eval(Source.newBuilder(PYTHON, code, SOURCE_FILE_NAME).build());
            
            org.graalvm.polyglot.Value pyFlatGeobufConverterClass = context.getPolyglotBindings().getMember("FlatGeobufConverter");
            org.graalvm.polyglot.Value pyFlatGeobufConverter = pyFlatGeobufConverterClass.newInstance();

            FlatGeobufConverter flatGeobufConverter = pyFlatGeobufConverter.as(FlatGeobufConverter.class);
            
            flatGeobufConverter.convert("my.gpkg");


        }
    }
    
}
