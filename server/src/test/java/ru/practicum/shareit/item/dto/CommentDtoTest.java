package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @Test
    void serialization() throws IOException {
        CommentDto comment = new CommentDto(1L, "hello world", 1L, 1L, "God", LocalDateTime.now());
        JsonContent<CommentDto> result = jacksonTester.write(comment);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.authorId");
        assertThat(result).hasJsonPath("$.authorName");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(comment.getId().intValue());
        assertThat(result).extractingJsonPathValue("$.text").isEqualTo(comment.getText());
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(comment.getItemId().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.authorId").isEqualTo(comment.getAuthorId().intValue());
        assertThat(result).extractingJsonPathValue("$.authorName").isEqualTo(comment.getAuthorName());
        assertThat(result).extractingJsonPathValue("$.created").isEqualTo(comment.getCreated().toString());
    }
}
