package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.APIError;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationRequest;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.helpers.Constants;
import com.mindex.challenge.service.CompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class CompensationServiceImpl implements CompensationService {
    @Autowired
    CompensationRepository repository;

    @Autowired
    EmployeeRepository employeeRepository;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public ResponseEntity<Object> saveCompensation(CompensationRequest compensationRequest, String employeeId) {

        try {
            Employee employee = employeeRepository.findByEmployeeId(employeeId);
            if (employee == null)
                return new ResponseEntity<>(APIError
                        .builder()
                        .message(Constants.ID_WAS_NOT_FOUND.replace("{}", employeeId))
                        .reason(Constants.DOES_NOT_EXIST)
                        .build(), HttpStatus.NOT_FOUND);

            Compensation compensation = new Compensation();
            compensation.setEmployee(employee);
            compensation.setSalary(compensationRequest.getSalary());
            compensation.setEffectiveDate(simpleDateFormat.parse(compensationRequest.getEffectiveDate()));
            repository.insert(compensation);
        }catch (Exception exception) {
            return new ResponseEntity<>(APIError
                    .builder()
                    .message(exception.getMessage())
                    .reason(Constants.ERROR_HAS_OCCURRED)
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(compensationRequest, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> findCompensationByEmployeeId(String employeeId) {
        Compensation compensation = null;
        try {
            Employee employee = employeeRepository.findByEmployeeId(employeeId);
            if (employee == null)
                return new ResponseEntity<>(APIError
                        .builder()
                        .message(Constants.ID_WAS_NOT_FOUND.replace("{}", employeeId))
                        .reason(Constants.DOES_NOT_EXIST)
                        .build(), HttpStatus.NOT_FOUND);
            compensation = repository.findByEmployee(employee);
            if (compensation == null)
                return new ResponseEntity<>(APIError
                        .builder()
                        .message(Constants.COMP_ID_WAS_NOT_FOUND.replace("{}", employeeId))
                        .reason(Constants.COMP_DOES_NOT_EXIST)
                        .build(), HttpStatus.NOT_FOUND);
        }catch (Exception exception) {
            return new ResponseEntity<>(APIError
                    .builder()
                    .message(exception.getMessage())
                    .reason(exception.getCause().getMessage())
                    .build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(compensation, HttpStatus.OK);
    }
}
