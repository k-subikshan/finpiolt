package com.finpiolt.demo.controller;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transactions")
public class UploadController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String n8nUrl =
            "http://localhost:5678/webhook/finpilot/transactions";

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("user_name") String userName
    ) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    MediaType.MULTIPART_FORM_DATA
            );

            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            body.add("file", new MultipartInputStreamFileResource(
                    file.getInputStream(),
                    file.getOriginalFilename()
            ));

            body.add("user_name", userName);

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            n8nUrl,
                            request,
                            String.class
                    );

            return ResponseEntity
                    .status(response.getStatusCode())
                    .body(response.getBody());

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            "Upload failed: " + e.getMessage()
                    );
        }
    }

    static class MultipartInputStreamFileResource
            extends org.springframework.core.io.InputStreamResource {

        private final String filename;

        MultipartInputStreamFileResource(
                java.io.InputStream inputStream,
                String filename
        ) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public long contentLength() {
            return -1;
        }
    }
}