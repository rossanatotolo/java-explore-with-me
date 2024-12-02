//package ru.practicum.event.dto;
//
//import jakarta.validation.constraints.Future;
//import jakarta.validation.constraints.PositiveOrZero;
//import jakarta.validation.constraints.Size;
//import lombok.Data;
//import ru.practicum.event.model.Location;
//import ru.practicum.event.model.StateAction;
//
//import java.time.LocalDateTime;
//
//@Data
//public class EventUpdateUserDto {
//    @Size(min = 20, max = 2000)
//    private String annotation;
//
//    @PositiveOrZero
//    private Long category;
//
//    @Size(min = 20, max = 7000)
//    private String description;
//
//    @Future
//    private LocalDateTime eventDate;
//
//    private Location location;
//
//    private  Boolean paid;
//
//    @PositiveOrZero
//    private Integer participantLimit;
//
//    private Boolean requestModeration;
//
//    private StateAction stateAction;
//
//    @Size(min = 3, max = 120)
//    private String title;
//}
