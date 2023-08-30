package me.sathish.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import me.sathish.entities.User;
import me.sathish.model.response.PagedResult;
import me.sathish.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    @Test
    void findAllUsers() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<User> userPage = new PageImpl<>(List.of(getUser()));
        given(userRepository.findAll(pageable)).willReturn(userPage);

        // when
        PagedResult<User> pagedResult = userService.findAllUsers(0, 10, "id", "asc");

        // then
        assertThat(pagedResult).isNotNull();
        assertThat(pagedResult.data()).isNotEmpty().hasSize(1);
        assertThat(pagedResult.hasNext()).isFalse();
        assertThat(pagedResult.pageNumber()).isEqualTo(1);
        assertThat(pagedResult.totalPages()).isEqualTo(1);
        assertThat(pagedResult.isFirst()).isTrue();
        assertThat(pagedResult.isLast()).isTrue();
        assertThat(pagedResult.hasPrevious()).isFalse();
        assertThat(pagedResult.totalElements()).isEqualTo(1);
    }

    @Test
    void findUserById() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.of(getUser()));
        // when
        Optional<User> optionalUser = userService.findUserById(1L);
        // then
        assertThat(optionalUser).isPresent();
        User user = optionalUser.get();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getText()).isEqualTo("junitTest");
    }

    @Test
    void saveUser() {
        // given
        given(userRepository.save(getUser())).willReturn(getUser());
        // when
        User persistedUser = userService.saveUser(getUser());
        // then
        assertThat(persistedUser).isNotNull();
        assertThat(persistedUser.getId()).isEqualTo(1L);
        assertThat(persistedUser.getText()).isEqualTo("junitTest");
    }

    @Test
    void deleteUserById() {
        // given
        willDoNothing().given(userRepository).deleteById(1L);
        // when
        userService.deleteUserById(1L);
        // then
        verify(userRepository, times(1)).deleteById(1L);
    }

    private User getUser() {
        User user = new User();
        user.setId(1L);
        user.setText("junitTest");
        user.setCreatedAt(Instant.now());
        return user;
    }
}
