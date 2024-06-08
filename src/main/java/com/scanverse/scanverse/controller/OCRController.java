package com.scanverse.scanverse.controller;

import com.scanverse.scanverse.service.OCRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/ocr/api/v1")
@RequiredArgsConstructor
@Slf4j
public class OCRController {

    private final OCRService ocrService;

    @PostMapping("/extractText")
    public ResponseEntity<String> extractText(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            // saving the uploaded file to a temporary location
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);

            // Perform OCR
            String text = ocrService.extractTextFromFile(tempFile);

            // Deleting the temporary file
            boolean deleteResult = tempFile.delete();
            if (!deleteResult) {
                log.error("Unable to delete temp file: {}", tempFile.getAbsolutePath());
            }

            return ResponseEntity.ok(text);
        } catch (IOException | TesseractException e) {
            return ResponseEntity.status(500).body("Failed to extract text " + e.getMessage());
        }
    }
}
