package com.ledgerly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyTrendDto {
    private int month;
    private long income;
    private long expense;
}
