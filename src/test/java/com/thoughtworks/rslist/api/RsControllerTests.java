package com.thoughtworks.rslist.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.RsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
    public void shouldGetOneRsEvent() throws Exception {
        mockMvc.perform(get("/rs/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(objectMapper.writeValueAsString(RsController.rsEventList.get(0))))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetRsEventBetween() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2").contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(objectMapper.writeValueAsString(RsController.rsEventList.subList(0, 2))))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldAddRsEvent() throws Exception {
        RsEvent rsEvent = new RsEvent("第四条事件", "未分类");
        mockMvc.perform(post("/rs/list").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(rsEvent)))
            .andExpect(status().isOk());

        assertEquals(4, RsController.rsEventList.size());
        mockMvc.perform(get("/rs/4").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.eventName", is("第四条事件")))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateRsEvent() throws Exception {
        RsEvent rsEvent = new RsEvent("第一条时间", "未分类");
        mockMvc.perform(post("/rs/1").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(rsEvent)))
            .andExpect(status().isOk());

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
            .andExpect(status().isOk());

        mockMvc.perform(get("/rs/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.eventName", is("第一条事件")))
            .andExpect(jsonPath("$.keyword", is("关键词")))
            .andExpect(status().isOk());
    }
}
