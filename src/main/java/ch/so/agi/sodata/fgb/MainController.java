package ch.so.agi.sodata.fgb;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${app.convertKey}")
    private String CONVERT_KEY;   

    @Autowired
    ConverterService converterService;
    
    @GetMapping("/ping")
    public ResponseEntity<String> ping()  {
        return new ResponseEntity<String>("sodata-flatgeobuf", HttpStatus.OK);
    }

    @GetMapping("/convert")
    public ResponseEntity<String> startConversion(@RequestParam(required = true) String key) {
        if (key.equals(CONVERT_KEY)) {
            try {
                converterService.convert();
            } catch (XMLStreamException | IOException e) {
                e.printStackTrace();
                throw new IllegalStateException(e);
            }
            return new ResponseEntity<String>("Conversion started.", HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<String>("Wrong convert key.", HttpStatus.FORBIDDEN);            
        }
    }

}
