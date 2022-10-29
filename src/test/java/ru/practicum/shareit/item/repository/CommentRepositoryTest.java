package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(value = {"comment-repository-test-before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"comment-repository-test-after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
public class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    User firstUser = new User(1L, "Adam", "adam@paradise.com");
    User secondUser = new User(2L, "Eva", "eva@paradise.com");
    Item paradise = new Item(3L, "Paradise", "great garden without people", true, firstUser, null);
    private final Comment comment = new Comment(
            4L,
            "great garden!",
            paradise,
            secondUser,
            LocalDateTime.of(2022, 12, 12, 12, 12, 0)
    );
    private final Set<Long> itemIds = new HashSet<>(Arrays.asList(1L, 3L));

    @Test
    void handleFindCommentsByItem_Id() {
        List<Comment> result = commentRepository.findCommentsByItem_Id(paradise.getId());

        assertThat(result).isNotEmpty();
        assertEquals(result.get(0).getId(), comment.getId());
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(0).getAuthor().getId(), comment.getAuthor().getId());
        assertEquals(result.get(0).getAuthor().getName(), comment.getAuthor().getName());
        assertEquals(result.get(0).getCreated(), comment.getCreated());
    }

    @Test
    void handleFindCommentsByItemIdIn() {
        List<Comment> result = commentRepository.findCommentsByItem_IdIn(itemIds);

        assertThat(result).isNotEmpty();
        assertEquals(result.get(0).getId(), comment.getId());
        assertEquals(result.get(0).getText(), comment.getText());
        assertEquals(result.get(0).getAuthor().getId(), comment.getAuthor().getId());
        assertEquals(result.get(0).getAuthor().getName(), comment.getAuthor().getName());
        assertEquals(result.get(0).getCreated(), comment.getCreated());
    }
}
