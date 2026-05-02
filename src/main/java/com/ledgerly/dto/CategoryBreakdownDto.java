package com.ledgerly.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryBreakdownDto {
    private String categoryName;
    private long amount;
}
