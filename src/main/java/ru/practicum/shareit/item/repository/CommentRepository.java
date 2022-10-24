package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Set;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByItem_Id(Long id);

    @Query(value = "SELECT * FROM comments " +
            "WHERE item_id IN ?",
            nativeQuery = true)
    List<Comment> findCommentsByItemIdList(List<Long> itemIds);

    List<Comment> findCommentsByItem_IdIn(Set<Long> itemIds);
}
