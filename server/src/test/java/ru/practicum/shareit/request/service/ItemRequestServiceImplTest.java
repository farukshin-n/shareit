package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.SubstanceNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequests;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    private final User firstUser = new User(1L, "Adam", "adam@paradise.com");
    private final UserDto firstUserDto = new UserDto(firstUser.getId(), firstUser.getName(), firstUser.getEmail());
    private final User secondUser = new User(2L, "Eva", "eva@paradise.com");
    private final UserDto secondUserDto = new UserDto(secondUser.getId(), secondUser.getName(), secondUser.getEmail());
    private final ItemRequest paradiseRequest = new ItemRequest(
            4L,
            "nice garden without people",
            secondUser,
            LocalDateTime.of(2022, 10, 10, 12, 0, 0)
    );
    private final ItemRequestDto paradiseRequestDto = new ItemRequestDto(
            paradiseRequest.getId(),
            paradiseRequest.getDescription(),
            secondUserDto,
            paradiseRequest.getCreated()
    );
    private final Item paradise = new Item(
            3L,
            "Paradise",
            "great garden without people",
            true,
            secondUser,
            paradiseRequest);
    private final ItemDto paradiseDto = new ItemDto(
            paradise.getId(),
            paradise.getName(),
            paradise.getDescription(),
            paradise.isAvailable(),
            secondUserDto,
            paradiseRequest.getId()
    );
    private final ItemDtoForRequests paradiseForRequests = new ItemDtoForRequests(
            paradise.getId(),
            paradise.getName(),
            paradise.getDescription(),
            paradise.isAvailable(),
            paradise.getRequest().getId()
    );
    private final ItemRequestDtoWithItems paradiseRequestWithItems = new ItemRequestDtoWithItems(
            paradiseRequest.getId(),
            paradiseRequest.getDescription(),
            paradiseRequest.getCreated(),
            List.of(paradiseForRequests)
    );
    @Mock
    private ItemRequestRepository mockRequestRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void handleAddRequest_byDefault() {
        lenient()
                .when(mockUserRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        Mockito
                .when(mockRequestRepository.save(any()))
                .thenReturn(paradiseRequest);
        ItemRequestDto actual = itemRequestService.addRequest(secondUser.getId(), paradiseRequestDto);

        assertEquals(paradiseRequestDto, actual);
    }

    @Test
    void handleAddRequest_withUserDoesNotExist() {
        Mockito
                .when(mockUserRepository.findById(53L))
                .thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class,
                () -> itemRequestService.addRequest(53L, paradiseRequestDto));
    }

    @Test
    void handleGetRequests_byDefault() {
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockRequestRepository.findByRequesterIdOrderByCreatedDesc(firstUser.getId()))
                .thenReturn(List.of(paradiseRequest));
        Mockito
                .when(mockItemRepository.getByRequestIdIn(anyList()))
                .thenReturn(List.of(paradise));
        List<ItemRequestDtoWithItems> actual = itemRequestService.getRequests(firstUser.getId());

        assertEquals(List.of(paradiseRequestWithItems), actual);
    }

    @Test
    void handleGetRequests_withUserDoesNotExist() {
        Mockito
                .when(mockUserRepository.findById(54L))
                .thenReturn(Optional.empty());

        assertThrows(SubstanceNotFoundException.class,
                () -> itemRequestService.getRequests(54L));
    }

    @Test
    void handleGetAllRequests_byDefault() {
        Mockito
                .when(mockRequestRepository.findByRequesterIdNot(
                                secondUser.getId(),
                                PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"))
                        )
                )
                .thenReturn(List.of(paradiseRequest));
        Mockito
                .when(mockItemRepository.getByRequestIdIn(anyList()))
                .thenReturn(List.of(paradise));
        List<ItemRequestDtoWithItems> actual = itemRequestService.getAllRequests(secondUser.getId(), 0, 1);

        assertThat(actual).isNotEmpty();
        assertEquals(List.of(paradiseRequestWithItems), actual);
    }

    @Test
    void handleGetRequest_byDefault() {
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockRequestRepository.findById(paradiseRequest.getId()))
                .thenReturn(Optional.of(paradiseRequest));
        Mockito
                .when(mockItemRepository.findByRequestIdOrderById(paradiseRequest.getId()))
                .thenReturn(List.of(paradise));
        ItemRequestDtoWithItems actual = itemRequestService.getRequest(firstUser.getId(), paradiseRequest.getId());

        assertEquals(paradiseRequestWithItems, actual);
    }

    @Test
    void handleGetRequest_withUserDoesNotExist() {
        Mockito
                .when(mockUserRepository.findById(53L))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> itemRequestService.getRequest(53L, paradiseRequest.getId())
        );
    }

    @Test
    void handleGetRequest_withRequestDoesNotExist() {
        Mockito
                .when(mockUserRepository.findById(firstUser.getId()))
                .thenReturn(Optional.of(firstUser));
        Mockito
                .when(mockRequestRepository.findById(53L))
                .thenReturn(Optional.empty());

        assertThrows(
                SubstanceNotFoundException.class,
                () -> itemRequestService.getRequest(firstUser.getId(), 53L)
        );
    }
}
