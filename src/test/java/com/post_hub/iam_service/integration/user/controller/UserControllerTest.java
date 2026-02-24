package com.post_hub.iam_service.integration.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.post_hub.iam_service.IamServiceApplication;
import com.post_hub.iam_service.model.entity.User;
import com.post_hub.iam_service.model.exception.InvalidDataException;
import com.post_hub.iam_service.model.request.user.NewUserRequest;
import com.post_hub.iam_service.model.request.user.UpdateUserRequest;
import com.post_hub.iam_service.repository.UserRepository;
import com.post_hub.iam_service.security.JwtTokenProvider;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
/*
    Настраивает работу с базой данных и говорит Spring что не нужно заменять базу тестовой, а использовать ту которая в
    проекте
 */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = IamServiceApplication.class) // поднимает всё приложение spring а не только отдельные компоненты
@AutoConfigureMockMvc // позволяет имитировать HTTP запросы прямо в тесте
@ExtendWith({MockitoExtension.class, SpringExtension.class})
public class UserControllerTest {

    @Autowired
    @Setter
    private MockMvc mockMvc;

    @Autowired
    @Setter
    private JwtTokenProvider tokenProvider;

    @Autowired
    @Setter
    private UserRepository userRepository;

    // конвертирует объекты в JSON и обратно(нужен для отправки данных в запросах и разбирать ответы)
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String adminJwt;
    private String userJwt;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeAll
    @Transactional
    void authorize() {
        // Admin authorization (ID: 1)
        User admin = userRepository.findById(1L)
                .orElseThrow(() -> new InvalidDataException("Admin with ID: 1 not found"));
        Hibernate.initialize(admin.getRoles()); // загрузит данные в память до закрытия транзакции
        this.adminJwt = "Bearer " + jwtTokenProvider.generateToken(admin);

        // Regular user authorization (ID: 3)
        User user = userRepository.findById(3L)
                .orElseThrow(() -> new InvalidDataException("User with ID: 3 not found"));
        Hibernate.initialize(user.getRoles());// загрузит данные в память до закрытия транзакции
        this.userJwt = "Bearer " + jwtTokenProvider.generateToken(user);
    }

    @Test
    void getAllUsers_200_OK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/all")
                .header(HttpHeaders.AUTHORIZATION, adminJwt)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllUsers_Unauthorized_401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/users/all"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @Transactional // откатит изменения в БД
    void create_User_AsAdmin_OK_200() throws Exception {
        NewUserRequest request = new NewUserRequest(
                "newUser",
                "password123",
                "newuser@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                .post("/users/create")
                .header(HttpHeaders.AUTHORIZATION, adminJwt)
                .contentType(MediaType.APPLICATION_JSON) // Этот заголовок говорит серверу, в каком формате отправлены данные в теле запроса
                .content(objectMapper.writeValueAsBytes(request)) // Добавляет тело (body) запроса
                .accept(MediaType.APPLICATION_JSON)) // Как и в первом тесте, ожидаем ответ от сервера в формате JSON
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void createUser_asUser_Forbidden_403() throws Exception {
        NewUserRequest request = new NewUserRequest(
                "newUser",
                "password123",
                "newuser@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/create")
                        .header(HttpHeaders.AUTHORIZATION, userJwt)
                        .contentType(MediaType.APPLICATION_JSON) // Этот заголовок говорит серверу, в каком формате отправлены данные в теле запроса
                        .content(objectMapper.writeValueAsBytes(request)) // Добавляет тело (body) запроса
                        .accept(MediaType.APPLICATION_JSON)) // Как и в первом тесте, ожидаем ответ от сервера в формате JSON
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @Transactional
    void updateUser_asAdmin_OK_200() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest("updates_username", "updatedUser@gmail.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/update/1")
                .header(HttpHeaders.AUTHORIZATION, adminJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    @Transactional
    void deleteUser_asAdmin_OK_200() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/delete/1")
                .header(HttpHeaders.AUTHORIZATION, adminJwt)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Transactional
    void deleteUser_asUser_Forbidden_403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/delete/1")
                        .header(HttpHeaders.AUTHORIZATION, userJwt)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
