package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationDtoInput;
import ru.practicum.compilation.dto.CompilationDtoOutput;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

public class CompilationMapper {

    public static Compilation toCompilation(final CompilationDtoInput compilationRequestDto,
                                            final List<Event> evens) {

        final Compilation compilation = new Compilation();

        compilation.setEvents(evens);
        compilation.setPinned(compilationRequestDto.getPinned());
        compilation.setTitle(compilationRequestDto.getTitle());

        return compilation;
    }

    public static CompilationDtoOutput toCompilationDto(final Compilation compilation,
                                                                final List<EventShortDto> eventShortDtoList) {

        final CompilationDtoOutput compilationDtoOutput = new CompilationDtoOutput();

        compilationDtoOutput.setId(compilation.getId());
        compilationDtoOutput.setEvents(eventShortDtoList);
        compilationDtoOutput.setPinned(compilation.getPinned());
        compilationDtoOutput.setTitle(compilation.getTitle());

        return compilationDtoOutput;
    }
}
