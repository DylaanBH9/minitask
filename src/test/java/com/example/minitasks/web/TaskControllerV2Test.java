package com.example.minitasks.web;

import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.dto.TaskResponseDTO;
import com.example.minitasks.domain.exceptions.TaskNotFoundException;
import com.example.minitasks.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskControllerV2.class)
class TaskControllerV2Test {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    TaskService service;

    @Test
    void createsTaskWithLocation() throws Exception {
        when(service.createTask(new CreateTaskDTO("Primera")))
                .thenReturn(new TaskResponseDTO(1L, "Primera", false));

        mvc.perform(post("/v2/tasks").contentType("application/json")
                        .content("{\"title\":\"Primera\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/v2/tasks/1"))
                .andExpect(jsonPath("$.title").value("Primera"))
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void completesTask() throws Exception {
        when(service.completeTask(1L)).thenReturn(new TaskResponseDTO(1L, "Primera", true));

        mvc.perform(patch("/v2/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));
    }

    @Test
    void completingMissingTaskReturns404() throws Exception {
        when(service.completeTask(999L)).thenThrow(new TaskNotFoundException(999L));

        mvc.perform(patch("/v2/tasks/999/complete"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));
    }
}
