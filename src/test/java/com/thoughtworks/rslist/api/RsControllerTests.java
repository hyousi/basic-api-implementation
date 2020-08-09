package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class RsControllerTests {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper;

    @BeforeEach
    private void setup() {
        RsController.rsEventList = RsController.init();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void shouldGetAllRsEvents() throws Exception {
        mockMvc.perform(get("/rs/list").contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(objectMapper.writeValueAsString(RsController.rsEventList)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllRsEventsOutOfBound() throws Exception {
        int start = 1;
        int end = RsController.rsEventList.size();
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
    public void shouldGetOneRsEvent() throws Exception {
        mockMvc.perform(get("/rs/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().string(objectMapper.writeValueAsString(RsController.rsEventList.get(0))))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetOneRsEventOutOfBound() throws Exception {
        int start = 1;
        int end = RsController.rsEventList.size();
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
    public void shouldGetRsEventBetween() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2").contentType(MediaType.APPLICATION_JSON))
            .andExpect(content()
                .string(objectMapper.writeValueAsString(RsController.rsEventList.subList(0, 2))))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldAddRsEvent() throws Exception {
        User user = new User("xiaowang", 19, "female", "a@b.com", "18888888888");
        RsEvent rsEvent = new RsEvent("第四条事件", "未分类", user);
        mockMvc.perform(post("/rs/list").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rsEvent)))
            .andExpect(status().isCreated())
            .andExpect(header().string("index", "4"));

        assertEquals(4, RsController.rsEventList.size());
        mockMvc.perform(get("/rs/4").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.eventName", is("第四条事件")))
            .andExpect(jsonPath("$.user").doesNotExist())
            .andExpect(status().isOk());

        mockMvc.perform(get("/rs/list").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].user").doesNotExist())
            .andExpect(jsonPath("$[1].user").doesNotExist())
            .andExpect(jsonPath("$[2].user").doesNotExist())
            .andExpect(jsonPath("$[3].user").doesNotExist())
            .andExpect(status().isOk());
    }

    @Test
    public void shouldAddValidRsEvent() throws Exception {
        String invalidJson = "{\"eventNames\": \"fourth Event\",\"keywords\": \"None\"}";
        mockMvc.perform(post("/rs/list").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
            .andExpect(jsonPath("$.error", is("invalid param")))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldUpdateRsEvent() throws Exception {
        RsEvent rsEvent = new RsEvent("第一条时间", "未分类");
        mockMvc.perform(post("/rs/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rsEvent)))
            .andExpect(status().isCreated())
            .andExpect(header().string("index", "1"));

        mockMvc.perform(get("/rs/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.eventName", is("第一条时间")))
            .andExpect(jsonPath("$.keyword", is("未分类")))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateRsEventProperty() throws Exception {
        String request = "{\"keyword\": \"关键词\"}";
        mockMvc.perform(post("/rs/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(request))
            .andExpect(status().isCreated())
            .andExpect(header().string("index", "1"));

        mockMvc.perform(get("/rs/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.eventName", is("第一条事件")))
            .andExpect(jsonPath("$.keyword", is("关键词")))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldRemoveRsEvent() throws Exception {
        mockMvc.perform(delete("/rs/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertEquals(2, RsController.rsEventList.size());
    }
}
