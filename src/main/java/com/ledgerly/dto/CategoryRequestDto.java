package com.ledgerly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequestDto {

    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String name;

    @NotBlank(message = "구분을 선택해주세요.")
    @Pattern(regexp = "INCOME|EXPENSE", message = "구분은 수입 또는 지출이어야 합니다.")
    private String type;

}
