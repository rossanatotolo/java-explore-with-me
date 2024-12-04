package ru.practicum.comment.dto;

import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;

@Data
public class CommentOutputDto {
    Long id;

    String text;

    String authorName;

    EventShortDto event;

    LocalDateTime created;
}
