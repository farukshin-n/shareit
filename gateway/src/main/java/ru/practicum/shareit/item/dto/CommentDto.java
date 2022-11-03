package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotEmpty
    @Size(max = 256, message = "Comment cannot be longer than 256 characters.")
    private String text;
    private Long itemId;
    private Long authorId;
    private String authorName;
    private LocalDateTime created;
}
