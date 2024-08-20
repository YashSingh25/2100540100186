package com.yash.project_test.project.controllers;

import com.yash.project_test.project.models.AverageResponse;
import com.yash.project_test.project.services.AverageCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/numbers")
public class AverageController {
    private final AverageCalculatorService averageService;

    @Autowired
    public AverageController(AverageCalculatorService averageService) {
        this.averageService = averageService;
    }

    @GetMapping
    public String hello(){
        return "hello yash";
    }

    @GetMapping("/{numberId}")
    public ResponseEntity<AverageResponse> getAverage(@PathVariable String numberId) {
        try {
            AverageResponse response = averageService.calculateAverage(numberId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}