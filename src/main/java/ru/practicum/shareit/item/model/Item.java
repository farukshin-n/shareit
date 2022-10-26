package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "items")
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private boolean available;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return available == item.available && Objects.equals(id, item.id) &&
                Objects.equals(name, item.name) && Objects.equals(description, item.description) &&
                Objects.equals(owner, item.owner) && Objects.equals(request, item.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, available, owner, request);
    }
}
