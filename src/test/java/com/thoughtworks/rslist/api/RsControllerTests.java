package com.thoughtworks.rslist.api;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RsControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldGetAllRsEvents() throws Exception {
        mockMvc.perform(get("/rs/list").contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("[第一条事件, 第二条事件, 第三条事件]"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetOneRsEvent() throws Exception {
        mockMvc.perform(get("/rs/list/1").contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("第一条事件"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldGetRsEventBetween() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2").contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("[第一条事件, 第二条事件]"))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldAddRsEvent() throws Exception {
        mockMvc.perform(post("/rs/list").contentType(MediaType.APPLICATION_JSON).content("第四条事件"))
            .andExpect(status().isOk());

        assertEquals(4, RsController.rsList.size());
        assertEquals("第四条事件", RsController.rsList.get(3));
    }
}
