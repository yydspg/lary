package cn.lary.module.gift.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateSecurityQuestionDTO {

    @NotNull(message = "answer is null")
    private String beforeAnswer;

    @NotNull(message = "after question is null")
    private String afterQuestion;

    private String afterAnswer;

    private String code;
}
