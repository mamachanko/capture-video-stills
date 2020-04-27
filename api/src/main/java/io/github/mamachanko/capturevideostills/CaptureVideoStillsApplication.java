package io.github.mamachanko.capturevideostills;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class CaptureVideoStillsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaptureVideoStillsApplication.class, args);
    }

}

@RestController
@Log4j2
class ApiController {

    private Path IMAGE_PATH = Paths.get("/tmp/image.png");

    @GetMapping(value = "/api/image.png", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource getImage() {
        FileSystemResource previouslyCapturedImage = new FileSystemResource(IMAGE_PATH);

        if (previouslyCapturedImage.exists()) return previouslyCapturedImage;

        return new ClassPathResource("defaultImage.png");
    }

    @PostMapping("/api/images")
    public void saveImage(MultipartFile file) throws IOException {
        Files.write(IMAGE_PATH, file.getBytes());
        log.info("File name '{}' saved to '{}'.", file.getOriginalFilename(), IMAGE_PATH);
    }
}
