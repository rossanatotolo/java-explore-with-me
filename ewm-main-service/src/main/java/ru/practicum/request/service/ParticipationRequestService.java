package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getAllParticipationRequests(final Long userId);

    ParticipationRequestDto createParticipationRequest(final Long userId, final Long eventId);

    ParticipationRequestDto cancelParticipationRequest(final Long userId, final Long requestId);
}
