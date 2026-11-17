package com.example.minitasks.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskControllerV2.class)
class TaskControllerV2Test {

    @Autowired
    MockMvc mvc;

    @Test
    void createsTaskWithLocation() throws Exception {
        mvc.perform(post("/v2/tasks").contentType("application/json")
                        .content("{\"title\":\"Primera\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v2/tasks/1"))
                .andExpect(jsonPath("$.title").value("Primera"))
                .andExpect(jsonPath("$.done").value(false));
    }
}
