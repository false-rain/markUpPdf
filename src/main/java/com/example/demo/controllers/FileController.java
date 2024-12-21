package com.example.demo.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.example.demo.processing.ProcessPdf.processPdf;

@Controller
public class FileController {

    @GetMapping("/")
    public String showUploadForm() {
        return "some string and text";
    }

    @GetMapping("/upload")
    public ModelAndView uploadForm() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setStatus(HttpStatus.ACCEPTED);
        modelAndView.setViewName("upload");
        return modelAndView;
    }

    @PostMapping("/convert")
    public ResponseEntity<Resource> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        File processedFile = processPdf(file);
        Resource resource = new FileSystemResource(processedFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + processedFile.getName() + "\"")
                .body(resource);
    }

}
