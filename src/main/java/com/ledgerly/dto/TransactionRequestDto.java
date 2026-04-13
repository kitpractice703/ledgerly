package com.ledgerly.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionRequestDto {

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotNull(message = "금액을 입력해주세요.")
    @Min(value = 1, message = "금액은 1원 이상이어야 합니다.")
    private Integer amount;

    private String description;

    @NotNull(message = "구분을 선택해주세요.")
    private String type;

    @NotNull(message = "날짜를 선택해주세요.")
    private LocalDate transactionDate;
}
