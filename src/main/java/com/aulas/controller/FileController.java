package com.aulas.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.aulas.config.FileStorageProperties;


@RestController
@RequestMapping("/api/files")
public class FileController {

    private final Path fileStorageLocation;



    @Autowired
    public FileController(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }

    /**
     * Endpoint to download a file by its unique filename.
     *
     * @param fileName      The unique name of the file to download.
     * @param submissionId  Optional submission ID to verify access rights.
     * @param userDetails   The authenticated user details.
     * @return The file as a downloadable Resource.
     */
    @GetMapping("/{fileName:.+}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileName,
            @RequestParam(required = false) Long submissionId,
            @AuthenticationPrincipal String email) {

        

        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Determine file's content type
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // Set headers for file download
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (MalformedURLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
