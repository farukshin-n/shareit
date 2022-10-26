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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SqlGroup({
        @Sql(value = {"comment-repository-test.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
        @Sql(value = {"comment-repository-test.after.sql"}, executionPhase = AFTER_TEST_METHOD)
})
public class CommentRepositoryTest {
    private final CommentRepository commentRepository;
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final Item paradise = new Item(3L,
            "Paradise",
            "great garden without people",
            true,
            firstUser,
            null);
    private final Comment comment = new Comment(
            4L,
            "awesome!",
            paradise,
            firstUser,
            LocalDateTime.of(2011, 10, 24, 12, 30, 0)
    );

    @Test
    void findCommentsByItem_Id() {
        List<Comment> result = commentRepository.findCommentsByItem_Id(paradise.getId());

        assertThat(result).isNotEmpty().hasSameElementsAs(List.of(comment));
    }
}
