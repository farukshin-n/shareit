package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoWithBookerIdTest {
    @Autowired
    private JacksonTester<BookingDtoWithBookerId> jacksonTester;

    @Test
    void serialization() throws IOException {
        UserDto user = new UserDto(1L, "Adam", "adam@paradise.com");
        ItemDto item = new ItemDto(2L, "Paradise", "great garden", true, user, 2L);
        BookingDtoWithBookerId bookingDto = new BookingDtoWithBookerId(1L, LocalDateTime.now().plusHours(1).withNano(0),
                LocalDateTime.now().plusHours(2).withNano(0), item, user.getId(), BookingStatus.WAITING);
        JsonContent<BookingDtoWithBookerId> result = jacksonTester.write(bookingDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item.id");
        assertThat(result).hasJsonPath("$.item.name");
        assertThat(result).hasJsonPath("$.item.description");
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(bookingDto.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.start").isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id")
                .isEqualTo(bookingDto.getItem().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.item.name")
                .isEqualTo(bookingDto.getItem().getName());
        assertThat(result).extractingJsonPathValue("$.item.description")
                .isEqualTo(bookingDto.getItem().getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId")
                .isEqualTo(bookingDto.getBookerId().intValue());
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(bookingDto.getStatus().toString());
    }
}
