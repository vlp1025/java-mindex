package com.mindex.challenge.service;

import com.mindex.challenge.data.CompensationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CompensationService {
    ResponseEntity<Object> saveCompensation(CompensationRequest compensation, String employeeId);
    ResponseEntity<Object> findCompensationByEmployeeId(String employeeId);
}
