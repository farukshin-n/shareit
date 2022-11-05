package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        UserDto user = new UserDto(1L, "Adam", "adam@paradise.com");
        ItemRequestDto item = new ItemRequestDto(
                1L,
                "great garden without people",
                user,
                LocalDateTime.now().plusHours(1).withNano(0));
        JsonContent<ItemRequestDto> result = jacksonTester.write(item);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.requester");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathNumberValue("$.requester.id")
                .isEqualTo(item.getRequester().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.requester.name")
                .isEqualTo(item.getRequester().getName());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(item.getCreated().toString());
    }
}
