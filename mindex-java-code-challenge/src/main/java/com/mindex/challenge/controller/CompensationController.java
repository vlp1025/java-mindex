package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationRequest;
import com.mindex.challenge.service.CompensationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class CompensationController {

    @Autowired
    CompensationService compensationService;

    @PostMapping("/compensation/{employeeId}")
    public ResponseEntity<Object> saveCompensation(
            @RequestBody CompensationRequest compensation,
            @PathVariable String employeeId) {
        log.debug("compensation creation body: "+compensation);
        return compensationService.saveCompensation(compensation, employeeId);
    }

    @GetMapping("/compensation/{employeeId}")
    public ResponseEntity<Object> getCompensation(@PathVariable String employeeId) {
        log.debug("query compensation by ID: "+employeeId);
        return compensationService.findCompensationByEmployeeId(employeeId);
    }
}
