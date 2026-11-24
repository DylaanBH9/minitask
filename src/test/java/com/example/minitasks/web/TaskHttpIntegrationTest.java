package com.example.minitasks.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Recorrido completo del slice: MockMvc → TaskControllerV2 → TaskService →
 * TaskRepository real → Hibernate → H2 (tablas creadas por Flyway).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskHttpIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void createsCompletesAndFailsForMissingTask() throws Exception {
        String location = mvc.perform(post("/v2/tasks").contentType("application/json")
                        .content("{\"title\":\"  Repasar Spring Boot  \"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/v2/tasks/")))
                .andExpect(jsonPath("$.title").value("Repasar Spring Boot"))
                .andExpect(jsonPath("$.done").value(false))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(patch(location + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));

        mvc.perform(patch("/v2/tasks/999999/complete"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
