package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.entity.RsEventEntity;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class RsControllerTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    RsEventRepository rsEventRepository;
    @Autowired
    UserRepository userRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    private void setup() {
        userRepository.deleteAll();
        rsEventRepository.deleteAll();
        UserEntity userA = new User("userA", 18, "male", "a@b.com", "15972577777").toUserEntity();
        UserEntity userB = new User("userB", 18, "male", "a@b.com", "15972588888").toUserEntity();
        UserEntity userC = new User("userC", 18, "male", "a@b.com", "15972599999").toUserEntity();
        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        RsEvent rsEventA = new RsEvent("EventA", "null", userA.getID());
        RsEvent rsEventB = new RsEvent("EventB", "null", userB.getID());
        RsEvent rsEventC = new RsEvent("EventC", "null", userC.getID());
        rsEventRepository.save(rsEventA.toRsEventEntity());
        rsEventRepository.save(rsEventB.toRsEventEntity());
        rsEventRepository.save(rsEventC.toRsEventEntity());
    }

    @Test
    public void shouldGetAllRsEvents() throws Exception {
        mockMvc.perform(get("/rs/list").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].eventName", is("EventA")))
            .andExpect(jsonPath("$[1].eventName", is("EventB")))
            .andExpect(jsonPath("$[2].eventName", is("EventC")))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllRsEventsOutOfBound() throws Exception {
        int start = 1;
        int end = (int) rsEventRepository.count();
        String url;

        url = String.format("/rs/list?start=%d&end=%d", start-1, end);
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", is("invalid request param")))
            .andExpect(status().isBadRequest());

        url = String.format("/rs/list?start=%d&end=%d", start, end+1);
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", is("invalid request param")))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGetRsEventsBetween() throws Exception {
        int start = 1;
        int end = (int) rsEventRepository.count();
        String url;

        url = String.format("/rs/list?start=%d&end=%d", start, end-1);
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetOneRsEvent() throws Exception {
        mockMvc.perform(get("/rs/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.eventName", is("EventA")))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetOneRsEventOutOfBound() throws Exception {
        int start = 1;
        int end = (int) rsEventRepository.count();
        String url;

        url = String.format("/rs/%d", start-1);
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", is("invalid index")))
            .andExpect(status().isBadRequest());

        url = String.format("/rs/%d", end+1);
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.error", is("invalid index")))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAddRsEvent() throws Exception {
        User user = new User("xiaowang", 19, "female", "a@b.com", "18888888888");
        userRepository.save(user.toUserEntity());
        int userId = userRepository.findByUsername("xiaowang").get().getID();
        RsEvent rsEvent = new RsEvent("第四条事件", "未分类", userId);


        mockMvc.perform(post("/rs/list").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rsEvent)))
            .andExpect(status().isCreated())
            .andExpect(header().string("index", "4"));

        RsEventEntity target = rsEventRepository.findByEventNameAndKeyword("第四条事件", "未分类");
        assertNotNull(target);
        assertEquals(4, rsEventRepository.count());
    }

    @Test
    public void shouldAddValidRsEvent() throws Exception {
        String invalidJson = "{\"eventName\": \"fourth Event\",\"keyword\": \"None\"}";
        mockMvc.perform(post("/rs/list").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
            .andExpect(jsonPath("$.error", is("invalid param")))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateRsEvent() throws Exception {
        RsEventEntity target = rsEventRepository.findByEventNameAndKeyword("EventA", "null");
        RsEvent rsEvent = new RsEvent("new EventA", "new null", target.getUserId());

        String url = String.format("/rs/%d", target.getId());
        mockMvc.perform(patch(url).contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rsEvent)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/rs/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.eventName", is("new EventA")))
            .andExpect(jsonPath("$.keyword", is("new null")))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldNotUpdateRsEventProperty() throws Exception {
        String request = "{\"keyword\": \"关键词\"}";
        mockMvc.perform(post("/rs/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isMethodNotAllowed());
    }

    @Test
    public void shouldRemoveRsEvent() throws Exception {
        mockMvc.perform(delete("/rs/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(2, rsEventRepository.count());
    }
}
