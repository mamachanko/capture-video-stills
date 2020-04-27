package io.github.mamachanko.capturevideostills;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Types;

@SpringBootApplication
@Log4j2
public class CaptureVideoStillsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptureVideoStillsApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return args -> {
            log.info("loading image into db ...");
            ClassPathResource image = new ClassPathResource("defaultImage.png");
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("imageBlob", new SqlLobValue(image.getInputStream(), ((int) image.contentLength()), new DefaultLobHandler()), Types.BLOB);
            long startTime = System.nanoTime();
            namedParameterJdbcTemplate.update("insert into image(data) values (:imageBlob)", parameters);
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            log.info("done in {}ms", timeElapsed / 1000000);
        };
    }
}

@RestController
@Log4j2
class ApiController {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    ApiController(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @GetMapping(value = "/api/image.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity getImage() {
        InputStream imageStream = namedParameterJdbcTemplate.query("select id, data from image order by id desc;", resultSet -> {
            resultSet.next();
            return resultSet.getBinaryStream(2);
        });

        return ResponseEntity.ok(new InputStreamResource(imageStream));
    }

    @PostMapping("/api/images")
    public void saveImage(MultipartFile file) throws IOException {
        log.info("saving image into db ...");
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("imageBlob", new SqlLobValue(file.getInputStream(), ((int) file.getSize()), new DefaultLobHandler()), Types.BLOB);
        long startTime = System.nanoTime();
        namedParameterJdbcTemplate.update("insert into image(data) values (:imageBlob)", parameters);
        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        log.info("done in {}ms", timeElapsed / 1000000);
    }
}
