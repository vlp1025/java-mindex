package com.mindex.challenge.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.APIError;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.CompensationRequest;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.helpers.Constants;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String createAndQueryCompensationUrl;

    private String testEmployeeId = "b7839309-3348-463b-a7e3-5de1c168beb3";
    private String invalidEmployeeId = "b7839309-3348-463b-a";

    @Autowired
    private CompensationService compensationService;

    ObjectMapper mapper = new ObjectMapper();

    @MockBean
    CompensationRepository compensationRepository;

    @MockBean
    EmployeeRepository employeeRepository;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");


    @Test
    public void shouldCreateCompensationGivenValidEmployeeId() throws ParseException {

        // given

        CompensationRequest compensationRequest = CompensationRequest
                .builder()
                .salary(20000.0)
                .effectiveDate("23-09-2021")
                .build();

        Employee employee = new Employee();
        employee.setFirstName("Paul");
        employee.setLastName("McCartney");
        employee.setPosition("Developer I");
        employee.setDepartment("Engineering");
        employee.setEmployeeId(testEmployeeId);

        Compensation compensation = new Compensation();
        compensation.setEffectiveDate(simpleDateFormat.parse(compensationRequest.getEffectiveDate()));
        compensation.setSalary(compensationRequest.getSalary());

        ResponseEntity<Object> responseEntity = new ResponseEntity(
                compensationRequest, HttpStatus.CREATED);

        // when

        Mockito.when(employeeRepository.findByEmployeeId(testEmployeeId))
                .thenReturn(employee);

        Mockito.when(compensationRepository.findByEmployee(employee))
                .thenReturn(compensation);

        Mockito.when(compensationRepository.save(compensation))
                .thenReturn(compensation);

        ResponseEntity<Object> resultsActual = compensationService.saveCompensation(compensationRequest, testEmployeeId);


        // then

        assertEquals(responseEntity.getStatusCodeValue(), resultsActual.getStatusCodeValue());
        assertEquals(
                mapper.convertValue(responseEntity
                        .getBody(), CompensationRequest.class).getSalary(),
                mapper.convertValue(resultsActual
                        .getBody(), CompensationRequest.class).getSalary());

    }

    @Test
    public void shouldFailToCreateOrQueryCompensationGivenInvalidEmployeeId() throws ParseException {

        // given

        CompensationRequest compensationRequest = CompensationRequest
                .builder()
                .salary(20000.0)
                .effectiveDate("23-09-2021")
                .build();

        Employee employee = new Employee();
        employee.setFirstName("Paul");
        employee.setLastName("McCartney");
        employee.setPosition("Developer I");
        employee.setDepartment("Engineering");
        employee.setEmployeeId(testEmployeeId);

        Compensation compensation = new Compensation();
        compensation.setEffectiveDate(simpleDateFormat.parse(compensationRequest.getEffectiveDate()));
        compensation.setSalary(compensationRequest.getSalary());

        APIError apiError = APIError.builder()
                .message(Constants.ID_WAS_NOT_FOUND.replace("{}", invalidEmployeeId))
                .reason(Constants.DOES_NOT_EXIST)
                .build();

        ResponseEntity<Object> responseEntity = new ResponseEntity(
                apiError, HttpStatus.NOT_FOUND);

        // when

        Mockito.when(employeeRepository.findByEmployeeId(testEmployeeId))
                .thenReturn(employee);

        Mockito.when(compensationRepository.findByEmployee(employee))
                .thenReturn(compensation);

        Mockito.when(compensationRepository.save(compensation))
                .thenReturn(compensation);

        ResponseEntity<Object> resultsActualSave = compensationService
                .saveCompensation(compensationRequest, invalidEmployeeId);
        ResponseEntity<Object> resultsActualQuery = compensationService
                .findCompensationByEmployeeId(invalidEmployeeId);

        // then

        assertEquals(responseEntity.getStatusCodeValue(), resultsActualSave.getStatusCodeValue());
        assertEquals(responseEntity.getStatusCodeValue(), resultsActualQuery.getStatusCodeValue());
        assertEquals(
                mapper.convertValue(responseEntity
                        .getBody(), APIError.class).getMessage(),
                mapper.convertValue(resultsActualSave
                        .getBody(), APIError.class).getMessage());

        assertEquals(
                mapper.convertValue(responseEntity
                        .getBody(), APIError.class).getMessage(),
                mapper.convertValue(resultsActualQuery
                        .getBody(), APIError.class).getMessage());

    }
}
