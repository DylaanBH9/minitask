package com.example.minitasks.web;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskControllerV1.class)
class TaskControllerV1Test {

    @Autowired
    MockMvc mvc;

    @Test
    void listsTasks() throws Exception {
        mvc.perform(post("/v1/tasks").contentType("application/json")
                        .content("{\"title\":\"Para listar\"}"))
                .andExpect(status().isCreated());

        mvc.perform(get("/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title == 'Para listar')]").exists());
    }

    @Test
    void createsTask() throws Exception {
        mvc.perform(post("/v1/tasks").contentType("application/json")
                        .content("{\"title\":\"Primera\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Primera"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void getsExistingTask() throws Exception {
        String body = mvc.perform(post("/v1/tasks").contentType("application/json")
                        .content("{\"title\":\"Para consultar\"}"))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        int id = JsonPath.read(body, "$.id");

        mvc.perform(get("/v1/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Para consultar"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void missingTaskReturns404() throws Exception {
        mvc.perform(get("/v1/tasks/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
