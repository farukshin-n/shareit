package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoForRequestsTest {
    @Autowired
    private JacksonTester<ItemDtoForRequests> jacksonTester;

    @Test
    void serialization() throws IOException {
        ItemDtoForRequests item = new ItemDtoForRequests(1L, "Apple", "Great fruit", true, 2L);
        JsonContent<ItemDtoForRequests> result = jacksonTester.write(item);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(item.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.name").isEqualTo(item.getName());
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(item.getDescription());
        assertThat(result).extractingJsonPathValue("$.available").isEqualTo(item.isAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(
                item.getRequestId().intValue());


    }
}
