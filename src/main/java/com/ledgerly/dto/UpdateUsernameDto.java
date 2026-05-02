package com.ledgerly.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUsernameDto {
    @NotBlank(message = "이름을 입력해주세요.")
    private String username;
}
