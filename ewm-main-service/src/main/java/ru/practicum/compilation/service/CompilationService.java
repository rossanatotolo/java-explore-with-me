package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDtoInput;
import ru.practicum.compilation.dto.CompilationDtoOutput;
import ru.practicum.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    CompilationDtoOutput createCompilation(final CompilationDtoInput compilationRequestDto);

    void deleteCompilation(final Long compId);

    CompilationDtoOutput updateCompilation(final CompilationUpdateDto compilationUpdateDto, final Long compId);

    List<CompilationDtoOutput> getAllCompilations(final Boolean pinned, final int from, final int size);

    CompilationDtoOutput getCompilationById(final Long compId);
}
