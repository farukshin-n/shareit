package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(long id);

    List<ItemRequest> findByRequesterIdNot(long id, Pageable pageable);
}
