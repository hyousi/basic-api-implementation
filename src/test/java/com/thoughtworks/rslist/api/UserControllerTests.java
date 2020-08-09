package com.thoughtworks.rslist.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.JsonPath;
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
        UserController.userList = UserController.init();
        objectMapper = new ObjectMapper();

    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        UserController.userList.clear();
        User user = new User("dangz", 22, "male", "a@b.com", "11111111111");
        UserController.userList.add(user);

        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].user_name", is("dangz")))
            .andExpect(jsonPath("$[0].user_age", is(22)))
            .andExpect(jsonPath("$[0].user_gender", is("male")))
            .andExpect(jsonPath("$[0].user_email", is("a@b.com")))
            .andExpect(jsonPath("$[0].user_phone", is("11111111111")))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldAddValidUser() throws Exception {
        User user = new User("dangz", 22, "male", "a@b.com", "11111111111");
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
            .andExpect(status().isCreated())
            .andExpect(header().string("index", "2"));
    }

    @Test
    public void shouldNotAddContainedUser() throws Exception {
        User existUser = UserController.userList.get(0);
        int length = UserController.userList.size();
        String userJson = objectMapper.writeValueAsString(existUser);

        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
            .andExpect(status().isCreated())
            .andExpect(header().string("index", "1"));
        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(length)));
    }
}
