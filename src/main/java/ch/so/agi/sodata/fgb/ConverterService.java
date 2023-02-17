package ch.so.agi.sodata.fgb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import ch.so.agi.meta2file.model.ThemePublication;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ConverterService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    HttpClient httpClient;

    @Value("${app.configFile}")
    private String CONFIG_FILE;   

    @Value("${app.filesServerUrl}")
    private String FILES_SERVER_URL;   

    @Value("${app.workDirectory}")
    private String WORK_DIRECTORY;   
    
    @Autowired
    private AmazonS3StorageService amazonS3StorageService;
    
    public void convert() throws XMLStreamException, IOException {        
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        log.debug("config file: " + new File(CONFIG_FILE).getAbsolutePath());

        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(new File(CONFIG_FILE)));

        while (xr.hasNext()) {
            xr.next();
            if (xr.getEventType() == XMLStreamConstants.START_ELEMENT) {
                if ("themePublication".equals(xr.getLocalName())) {
                    var themePublication = xmlMapper.readValue(xr, ThemePublication.class);
                    var identifier = themePublication.getIdentifier();
                    var items = themePublication.getItems();
                    
                    log.debug("Identifier: "+ identifier);
                    
                    // TODO raster vs. vector
                    
                    try {
                        if (items.size() > 1) {
                            // TODO
                            // convert subunits
                        } else {
                            convertDataset(identifier);
                        }
                    } catch (URISyntaxException | IOException | InterruptedException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                    
                    
                    
                    
                }
            }
        }        
    }
    
    private void convertDataset(String identifier) throws URISyntaxException, IOException, InterruptedException {
        Path tmpWorkDir = Files.createTempDirectory(Paths.get(WORK_DIRECTORY), identifier);

        // Herunterladen
        String requestUrl = this.FILES_SERVER_URL + "/" + identifier + "/aktuell/" + identifier + ".gpkg.zip";
        File zipFile = Paths.get(WORK_DIRECTORY, identifier + ".gpkg.zip").toFile();
        
        HttpRequest httpRequest = HttpRequest.newBuilder().GET().uri(new URI(requestUrl))
                .timeout(Duration.ofSeconds(120L)).build();
        HttpResponse<InputStream> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        saveFile(response.body(), zipFile.getAbsolutePath());

        // Entzippen
        try {
            new ZipFile(zipFile).extractAll(tmpWorkDir.toFile().getAbsolutePath());            
        } catch (ZipException e) {
            log.error(e.getMessage());
            throw new IOException(e);
        }
        File gpkgFile = Paths.get(tmpWorkDir.toFile().getAbsolutePath(), identifier + ".gpkg").toFile();
        
        amazonS3StorageService.store(new File("aa"), "foo", "bar");

        
        // Alle Tabellen eruieren, die konvertiert werden sollen.
        List<String> tableNames = new ArrayList<String>();
        String url = "jdbc:sqlite:" + gpkgFile;
        try (Connection conn = DriverManager.getConnection(url)) {
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT tablename FROM T_ILI2DB_TABLE_PROP WHERE setting = 'CLASS'"); 
                    while (rs.next()) {
                        tableNames.add(rs.getString("tablename"));
                        log.debug("tablename: " + rs.getString("tablename"));
                    }
            }  catch (SQLException e) {
                log.error(e.getMessage());
                return;
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            return;
        }
        


        // Konvertieren
        String osTmpDir = System.getProperty("java.io.tmpdir");
        for (String tableName : tableNames) {
            String outputFile = Paths.get(tmpWorkDir.toFile().getAbsolutePath(), tableName + ".fgb").toFile().getAbsolutePath();
            log.debug(outputFile);
            
            try {
                ProcessBuilder pb = new ProcessBuilder("ogr2ogr", "-lco", "SPATIAL_INDEX=YES", "-lco", "TEMPORARY_DIR="+osTmpDir, "-f", "FlatGeobuf", outputFile, gpkgFile.getAbsolutePath(), tableName);
                log.debug(pb.command().toString());

                Process p = pb.start();
                BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = null;
                while ((line = is.readLine()) != null)
                    log.info(line);
                p.waitFor();
                
                if (p.exitValue() != 0) {
                    log.error("ogr2ogr did not run successfully.");
                    return;
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                log.error(e.getMessage());
                return;
            }
        }
                
        // Hochladen
        //https://github.com/edigonzales/ilivalidator-jobrunr/blob/eccdff794b/src/main/java/ch/so/agi/ilivalidator/StorageService.java
        
        
    }
    
    private static void saveFile(InputStream body, String destinationFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFile);
        fos.write(body.readAllBytes());
        fos.close();
    }

    // Scheduler für Converting
    
    // Scheduler für Cleaning
}
