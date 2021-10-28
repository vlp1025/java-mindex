package com.mindex.challenge.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Compensation {
    private String id;
    private Employee employee;
    private Double salary;
    private Date effectiveDate;
}
