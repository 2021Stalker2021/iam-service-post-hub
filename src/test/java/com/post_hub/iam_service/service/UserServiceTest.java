package com.post_hub.iam_service.service;

import com.post_hub.iam_service.mapper.UserMapper;
import com.post_hub.iam_service.model.dto.user.UserDTO;
import com.post_hub.iam_service.model.entity.Role;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.NotFoundException;
import com.post_hub.iam_service.repository.RoleRepository;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.service.impl.UserServiceImpl;
import com.post_hub.iam_service.service.model.IamServiceUserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// создавать новый экземпляр тестового класса для каждого тестового метода.
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;
    private Role superAdminRole;

    @BeforeEach
    void setUp() {
        Role superAdminRole = new Role();
        superAdminRole.setName(IamServiceUserRole.SUPER_ADMIN.getRole());

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("TestUser");
        testUser.setEmail("testuser@gmail.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of(superAdminRole));

        testUserDTO = new UserDTO();
        testUserDTO.setId(1);
        testUserDTO.setUsername("TestUser");
        testUserDTO.setEmail("testuser@gmail.com");

    }

    @Test
    void getById_UserExists_ReturnsUserDTO() {
        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.getById(1L).getPayload();

        assertNotNull(result);
        assertEquals(testUserDTO.getId(), result.getId());
        assertEquals(testUserDTO.getUsername(), result.getUsername());

        verify(userRepository, times(1)).findByIdAndDeletedFalse(1L);
        verify(userMapper, times(1)).toUserDTO(testUser);
    }

    @Test
    void getById_UserNotFound_ThrowsException() {
        when(userRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("not found");

        verify(userRepository, times(1)).findByIdAndDeletedFalse(999L);
        verify(userMapper, times(0)).toUserDTO(testUser);
    }
}
