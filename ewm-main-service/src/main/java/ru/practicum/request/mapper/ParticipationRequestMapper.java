package ru.practicum.request.mapper;

import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(final ParticipationRequest participationRequest) {

        final ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();

        participationRequestDto.setId(participationRequest.getId());
        participationRequestDto.setRequester(participationRequest.getRequester().getId());
        participationRequestDto.setEvent(participationRequest.getEvent().getId());
        participationRequestDto.setStatus(participationRequest.getStatus());
        participationRequestDto.setCreated(participationRequest.getCreated());

        return participationRequestDto;
    }

    public static List<ParticipationRequestDto> toParticipationRequestDtoList(Iterable<ParticipationRequest> requests) {
        final List<ParticipationRequestDto> result = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            result.add(toParticipationRequestDto(request));
        }
        return result;
    }
}
