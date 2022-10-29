package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoWithBookerId;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoWithBookingsAndCommentsTest {
    @Autowired
    private JacksonTester<ItemDtoWithBookingsAndComments> jacksonTester;

    @Test
    void serialization() throws IOException {
        User user = new User(1L, "Adam", "adam@paradise.com");
        UserDto owner = new UserDto(user.getId(), user.getName(), user.getEmail());
        Item item = new Item(2L, "Paradise", "great garden", true, user, null);
        ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(),owner, 2L);
        BookingDtoWithBookerId lastBooking = new BookingDtoWithBookerId(
                6L,
                LocalDateTime.of(2022, 10, 20, 12, 30),
                LocalDateTime.of(2022, 10, 21, 13, 35),
                itemDto,
                owner.getId(),
                BookingStatus.APPROVED
        );
        BookingDtoWithBookerId nextBooking = new BookingDtoWithBookerId(
                7L,
                LocalDateTime.of(2022, 10, 23, 12, 35),
                LocalDateTime.of(2022, 10, 24, 13, 0),
                itemDto,
                owner.getId(),
                BookingStatus.APPROVED
        );
        ItemDtoWithBookingsAndComments itemDtoWith = new ItemDtoWithBookingsAndComments(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemDto.getRequestId(),
                Collections.emptyList(),
                lastBooking,
                nextBooking);
        JsonContent<ItemDtoWithBookingsAndComments> result = jacksonTester.write(itemDtoWith);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.owner.id");
        assertThat(result).hasJsonPath("$.owner.name");
        assertThat(result).hasJsonPath("$.owner.email");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDtoWith.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(itemDtoWith.getName());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(itemDtoWith.getDescription());
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(itemDtoWith.getAvailable());
        assertThat(result).extractingJsonPathValue("$.owner.id").isEqualTo(itemDtoWith.getOwner().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.owner.name").isEqualTo(itemDtoWith.getOwner().getName());
        assertThat(result).extractingJsonPathValue("$.owner.email").isEqualTo(itemDtoWith.getOwner().getEmail());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(
                itemDtoWith.getRequestId().intValue());
        assertThat(result).extractingJsonPathValue("$.comments").isEqualTo(Collections.emptyList());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(
                itemDtoWith.getLastBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.item.id").isEqualTo(
                itemDtoWith.getLastBooking().getItem().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.lastBooking.item.name").isEqualTo(
                itemDtoWith.getLastBooking().getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(
                itemDtoWith.getLastBooking().getBookerId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(
                itemDtoWith.getNextBooking().getId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.item.id").isEqualTo(
                itemDtoWith.getLastBooking().getItem().getId().intValue());
        assertThat(result).extractingJsonPathValue("$.nextBooking.item.name").isEqualTo(
                itemDtoWith.getLastBooking().getItem().getName());
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(
                itemDtoWith.getLastBooking().getBookerId().intValue());
    }
}
