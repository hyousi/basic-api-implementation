package com.thoughtworks.rslist.api;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.entity.UserEntity;
import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    VoteRepository voteRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    private void setup() {
        voteRepository.deleteAll();
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        UserEntity userA = new User("userA", 18, "male", "a@b.com", "15972577777").toUserEntity();
        UserEntity userB = new User("userB", 18, "male", "a@b.com", "15972588888").toUserEntity();
        UserEntity userC = new User("userC", 18, "male", "a@b.com", "15972599999").toUserEntity();
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        int userAId = userRepository.findByUsername("userA").get().getID();
        int userBId = userRepository.findByUsername("userB").get().getID();
        int userCId = userRepository.findByUsername("userC").get().getID();
        RsEvent rsEventA = new RsEvent("EventA", "null", userAId);
        RsEvent rsEventB = new RsEvent("EventB", "null", userBId);
        RsEvent rsEventC = new RsEvent("EventC", "null", userCId);
        rsEventRepository.save(rsEventA.toRsEventEntity());
        rsEventRepository.save(rsEventB.toRsEventEntity());
        rsEventRepository.save(rsEventC.toRsEventEntity());
    }

    @Test
    public void shouldVoteSucceed() throws Exception {
        int userId = userRepository.findByUsername("userA").get().getID();
        int eventId = rsEventRepository.findByEventNameAndKeyword("EventA", "null").getId();
        Vote vote = new Vote(5, userId, LocalDate.now().toString());
        String requestBody = objectMapper.writeValueAsString(vote);
        String url = String.format("/rs/%d/vote", eventId);

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andExpect(status().isCreated());

        assertEquals(5, rsEventRepository.findById(eventId).get().getVoteNum());
        assertEquals(1, voteRepository.count());
    }

    @Test
    public void shouldVoteFail() throws Exception{
        int userId = userRepository.findByUsername("userA").get().getID();
        int eventId = rsEventRepository.findByEventNameAndKeyword("EventA", "null").getId();
        Vote vote = new Vote(11, userId, LocalDate.now().toString());
        String requestBody = objectMapper.writeValueAsString(vote);
        String url = String.format("/rs/%d/vote", eventId);

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andExpect(status().isBadRequest());

    }

    @Test
    public void shouldGetVotes() throws Exception {
        int userId = userRepository.findByUsername("userA").get().getID();
        int eventId = rsEventRepository.findByEventNameAndKeyword("EventA", "null").getId();
        Vote voteA = new Vote(3, userId, "2020-08-01");
        Vote voteB = new Vote(5, userId, "2020-08-03");

        mockMvc.perform(post(String.format("/rs/%d/vote", eventId)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(voteA)))
            .andExpect(status().isCreated());

        mockMvc.perform(post(String.format("/rs/%d/vote", eventId)).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(voteB)))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/votes?start=2020-08-01&end=2020-08-02").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)));
    }
}
