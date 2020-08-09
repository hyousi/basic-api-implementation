package com.thoughtworks.rslist.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.repository.UserRepository;
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

    @Autowired
    UserRepository userRepository;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    private void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        User user = new User("dangz", 22, "male", "a@b.com", "11111111111");
        userRepository.save(user.toUserEntity());

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
            .andExpect(header().string("index", "1"));
        assertEquals(1, userRepository.count());
    }

    @Test
    public void shouldNotAddExistedUser() throws Exception {
        User user = new User("dangz", 22, "male", "a@b.com", "11111111111");
        userRepository.save(user.toUserEntity());
        int length = (int) userRepository.count();
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
            .andExpect(status().isOk())
            .andExpect(content().string("User Exists"));

        mockMvc.perform(get("/users")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()", is(length)));
    }
}
