package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentOutputDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentControllerPublic {
    private final CommentService commentService;

    @GetMapping("events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentOutputDto> getAllCommentsByEvent(@PathVariable final Long eventId,
                                                        @RequestParam(defaultValue = "0") final int from,
                                                        @RequestParam(defaultValue = "10") final int size) {
        return commentService.getAllCommentsByEvent(eventId, from, size);
    }

    @GetMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentOutputDto getCommentById(@PathVariable @Positive final Long commentId) {
        return commentService.getCommentById(commentId);
    }
}
