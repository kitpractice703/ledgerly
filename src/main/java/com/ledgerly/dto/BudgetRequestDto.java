package com.ledgerly.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetRequestDto {

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotNull(message = "한도 금액을 입력해주세요.")
    @Min(value = 1, message = "한도 금액은 1원 이상이어야 합니다.")
    private Integer limitAmount;

    @NotNull(message = "연도를 입력해주세요.")
    private Integer year;

    @NotNull(message = "월을 입력해주세요.")
    @Min(value = 1, message = "월은 1 이상이어야합니다.")
    @Max(value = 12, message = "월은 12 이하이어야합니다.")
    private Integer month;
}
