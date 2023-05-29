package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest2 {
    private final EntityManager em;
    private final UserService userService;
    private final ItemRequestService service;

    @Test
    void getAllRequestTest() {
        User user = new User();
        user.setEmail("1@1.ru");
        user.setName("1");
        userService.saveUser(user);
        User userFromBase = userService.getAllUsers().get(0);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("покой");
        service.addNewItemRequest(userFromBase.getId(), itemRequestDto);

        TypedQuery<ItemRequest> query = em.createQuery("Select ir from ItemRequest ir where ir.userId = :userId", ItemRequest.class);
        ItemRequest itemRequestFromBase = query.setParameter("userId", userFromBase.getId()).getSingleResult();

        assertThat(itemRequestFromBase.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}