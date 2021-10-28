package com.mindex.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.APIError;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.helpers.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChallengeApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DataBootstrapTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EmployeeRepository employeeRepository;

    TestRestTemplate testRestTemplate = new TestRestTemplate();

    @Mock
    private RestTemplate restTemplateMock;

    @Test
    public void test() {
        Employee employee = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        assertNotNull(employee);
        assertEquals("John", employee.getFirstName());
        assertEquals("Lennon", employee.getLastName());
        assertEquals("Development Manager", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());
    }

    @Test
    public void shouldReturnReportingStructureGivenValidEmployeeId() {
        // given
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";

        Employee reporterOne = new Employee();
        reporterOne.setEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        reporterOne.setDepartment(null);
        reporterOne.setPosition(null);
        reporterOne.setFirstName(null);
        reporterOne.setLastName(null);
        reporterOne.setDirectReports(null);

        Employee reporterTwo = new Employee();
        reporterTwo.setEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        reporterTwo.setDepartment(null);
        reporterTwo.setPosition(null);
        reporterTwo.setFirstName(null);
        reporterTwo.setLastName(null);
        reporterTwo.setDirectReports(null);

        Employee reporterThree = new Employee();
        reporterThree.setEmployeeId("03aa1462-fa9-4978-901b-7c001562cf6f");
        reporterThree.setDepartment(null);
        reporterThree.setPosition(null);
        reporterThree.setFirstName(null);
        reporterThree.setLastName(null);
        reporterThree.setDirectReports(null);

        Employee reporterFour = new Employee();
        reporterFour.setEmployeeId("03aa1-ffa9-4978-901b-7c001562cf6f");
        reporterFour.setDepartment(null);
        reporterFour.setPosition(null);
        reporterFour.setFirstName(null);
        reporterFour.setLastName(null);
        reporterFour.setDirectReports(null);

        List<Employee> employees = new ArrayList<>();
        employees.add(reporterOne);
        employees.add(reporterTwo);
        employees.add(reporterThree);
        employees.add(reporterFour);

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setDepartment("Engineering");
        employee.setPosition("Development Manager");
        employee.setFirstName("John");
        employee.setLastName("Lennon");
        employee.setDirectReports(employees);

        ReportingStructure reportingStructure = ReportingStructure
                .builder()
                .employee(employee)
                .numberOfReports(4)
                .build();

        ResponseEntity<Object> responseEntity = new ResponseEntity(
                reportingStructure, HttpStatus.OK);

        // when
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        Mockito.when(restTemplateMock.exchange(
                "/employees/"+employeeId+"/reports",
                HttpMethod.GET,
                entity,
                Object.class
        )).thenReturn(responseEntity);

        ResponseEntity<Object> result = testRestTemplate.exchange(
                "http://localhost:"+port+"/employees/"+employeeId+"/reports",
                HttpMethod.GET,
                entity,
                Object.class
        );


        // then
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(responseEntity.getStatusCodeValue(), result.getStatusCodeValue());
        assertEquals(mapper.convertValue(responseEntity.getBody(), ReportingStructure.class).getNumberOfReports(),
                mapper.convertValue(result.getBody(), ReportingStructure.class).getNumberOfReports());

    }

    @Test
    public void shouldReturnAPIErrorGivenInvalidEmployeeId() {
        // given
        String employeeId = "16a596ae-edd3-4847-99fe-c4518e82c86";

        APIError apiError = APIError.builder()
                .message(Constants.ID_WAS_NOT_FOUND.replace("{}", employeeId))
                .reason(Constants.DOES_NOT_EXIST)
                .build();


        ResponseEntity<Object> responseEntity = new ResponseEntity(
                apiError, HttpStatus.INTERNAL_SERVER_ERROR);

        // when
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        Mockito.when(restTemplateMock.exchange(
                "/employees/"+employeeId+"/reports",
                HttpMethod.GET,
                entity,
                Object.class
        )).thenReturn(responseEntity);

        ResponseEntity<Object> result = testRestTemplate.exchange(
                "http://localhost:"+port+"/employees/"+employeeId+"/reports",
                HttpMethod.GET,
                entity,
                Object.class
        );


        // then
        ObjectMapper mapper = new ObjectMapper();
        assertEquals(responseEntity.getStatusCodeValue(), result.getStatusCodeValue());
//        assertEquals(mapper.convertValue(responseEntity.getBody(), APIError.class).getMessage(),
//                mapper.convertValue(result.getBody(), APIError.class).getMessage());

    }
}