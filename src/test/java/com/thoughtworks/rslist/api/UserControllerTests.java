package com.thoughtworks.rslist.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
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

    @Autowired
    RsEventRepository rsEventRepository;

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    private void setup() {
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
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

    @Test
    public void shouldGetUser() throws Exception {
        User user = new User("dangz", 22, "male", "a@b.com", "11111111111");
        String userJson = objectMapper.writeValueAsString(user);
        userRepository.save(user.toUserEntity());

        mockMvc.perform(get("/users/dangz").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string(userJson));
    }

    @Test void shouldNotGetUser() throws Exception {
        User user = new User("dangz", 22, "male", "a@b.com", "11111111111");
        userRepository.save(user.toUserEntity());

        mockMvc.perform(get("/users/random").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("User Not Found."));
    }

    @Test void shouldDeleteUser() throws Exception {
        UserEntity userA = new User("userA", 18, "male", "a@b.com", "15972577777").toUserEntity();
        UserEntity userB = new User("userB", 18, "male", "a@b.com", "15972588888").toUserEntity();
        UserEntity userC = new User("userC", 18, "male", "a@b.com", "15972599999").toUserEntity();
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        int userAId = userRepository.findByUsername("userA").get().getID();
        int userBId = userRepository.findByUsername("userB").get().getID();

        RsEvent rsEventA = new RsEvent("EventA", "null", userAId);
        RsEvent rsEventB = new RsEvent("EventB", "null", userBId);
        RsEvent rsEventC = new RsEvent("EventC", "null", userAId);
        rsEventRepository.save(rsEventA.toRsEventEntity());
        rsEventRepository.save(rsEventB.toRsEventEntity());
        rsEventRepository.save(rsEventC.toRsEventEntity());

        mockMvc.perform(delete("/users/userA").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
        assertEquals(2, userRepository.count());
        assertEquals(1, rsEventRepository.count());
    }
}
