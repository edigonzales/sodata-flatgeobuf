package ch.so.agi.sodata.fgb;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
@EnableScheduling
@SpringBootApplication
public class SodataFgbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SodataFgbApplication.class, args);
	}

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean 
    HttpClient createHttpClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(Version.HTTP_1_1)
                //.followRedirects(Redirect.NEVER)
                .build();
        return httpClient;
    }

    // TODO
    // Nur Zwecks erstmaligen Entwickeln hier.
    // Später dauert die Konvertierung zu lange.
    // Vielleicht sowas:
    // Eventuell lohnt sich eine eigener Actuator. Dann kann man die Anwendung hochfahren und sie ist live UND ready.
    // Mit Scheduler ausführen und mit simplen Key (als env var)

    @Bean
    CommandLineRunner init(ConverterService converterService) {
        return args -> {            
            converterService.convert();
        };
    }
}
