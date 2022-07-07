package com.borovyk.traininglargestpicture.controller;

import com.borovyk.traininglargestpicture.service.PicturesService;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/pictures")
public class PicturesController {

    public static final String API_KEY = "b4CCRzB2MX3HkPj9d8MaI25RXW7LkG5pMUpV9v3q";

    private final PicturesService picturesService;

    public PicturesController(PicturesService picturesService) {
        this.picturesService = picturesService;
    }

    @SneakyThrows
    @GetMapping("/{sol}/largest")
    public ResponseEntity<?> getLargestImage(@PathVariable("sol") long sol) {
        var url = picturesService.printLargestPicture(API_KEY, sol);
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT).location(URI.create(url)).build();
    }

}
