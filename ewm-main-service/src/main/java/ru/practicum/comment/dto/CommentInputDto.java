package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentInputDto {
    @NotBlank
    private String text;
}
