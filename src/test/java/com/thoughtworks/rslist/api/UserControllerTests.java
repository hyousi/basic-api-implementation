package com.thoughtworks.rslist.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    private void setup() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/user")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(objectMapper.writeValueAsString(UserController.userList)));
    }

    @Test
    public void shouldAddValidUser() throws Exception {
        User user = new User("dangz", 22, "male", "a@b.com", "11111111111");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldNotAddContainedUser() throws Exception {
        User existUser = UserController.userList.get(0);
        int length = UserController.userList.size();
        String userJson = objectMapper.writeValueAsString(existUser);

        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
            .andExpect(status().isOk());
        mockMvc.perform(get("/user")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(length)));
    }
}
