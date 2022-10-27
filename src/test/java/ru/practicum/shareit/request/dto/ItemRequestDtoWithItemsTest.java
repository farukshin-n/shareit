package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoWithItemsTest {
    @Autowired
    private JacksonTester<ItemRequestDtoWithItems> jacksonTester;

    @Test
    void serialization() throws IOException {
        List<ItemDtoForRequests> itemDtos = Collections.emptyList();
        ItemRequestDtoWithItems itemDto = new ItemRequestDtoWithItems(
                1L,
                "list of items",
                LocalDateTime.now().plusHours(1).withNano(0),
                itemDtos);
        JsonContent<ItemRequestDtoWithItems> result = jacksonTester.write(itemDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathValue("$.created")
                .isEqualTo(itemDto.getCreated().toString());
        assertThat(result).extractingJsonPathValue("$.items")
                .isEqualTo(itemDto.getItems());
    }
}
