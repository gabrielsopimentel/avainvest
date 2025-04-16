package br.com.dio.avainvest.service;

import br.com.dio.avainvest.dto.CreateUserDto;
import br.com.dio.avainvest.entity.User;
import br.com.dio.avainvest.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Nested
    class createUser {

        @Test
        @DisplayName("Should create a user with success")
        void shouldCreateAUser() {

            var user = new User(UUID.randomUUID(), "username", "email@email", "password", Instant.now(), null);

            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());
            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "123"
            );

            var output = userService.createUser(input);

            assertNotNull(output);

            var usercapture = userArgumentCaptor.getValue();
            assertEquals(input.username(), usercapture.getUsername());
            assertEquals(input.email(), usercapture.getEmail());
            assertEquals(input.password(), usercapture.getPassword());
        }

        @Test
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccrus() {

            doThrow(new RuntimeException()).when(userRepository).save(any());
            var input = new CreateUserDto(
                    "username",
                    "email@email.com",
                    "123"
            );

            assertThrows(RuntimeException.class, () -> userService.createUser(input));

        }
    }

    @Nested
    class getUserById {

        @Test
        @DisplayName("Should get user by id with success when optional is present")
        void shouldGetUserByIdWithSuccessWhenOptionalIsPresent() {

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null
            );
            doReturn(Optional.of(user))
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());

            var output = userService.getUserById(user.getUserId().toString());

            assertTrue(output.isPresent());
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("Should get user by id with success when optional is empty")
        void shouldGetUserByIdWithSuccessWhenOptionalIsEmpty() {

            var userId = UUID.randomUUID();
            doReturn(Optional.empty())
                    .when(userRepository)
                    .findById(uuidArgumentCaptor.capture());

            var output = userService.getUserById(userId.toString());

            assertTrue(output.isEmpty());
            assertEquals(userId, uuidArgumentCaptor.getValue());
        }
    }
}
