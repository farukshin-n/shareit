package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class InputBookingDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}
