package com.example.minitasks.web;

import com.example.minitasks.domain.SimpleTask;
import com.example.minitasks.dto.CreateTaskDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * V2 de la API: misma creación que V1 pero respondiendo con Location.
 */
@RestController
@RequestMapping("/v2/tasks")
public class TaskControllerV2 {

    private final List<SimpleTask> tasks = new ArrayList<>();

    @PostMapping
    public ResponseEntity<SimpleTask> create(@RequestBody CreateTaskDTO dto) {
        SimpleTask task = new SimpleTask(tasks.size() + 1, dto.title(), false);
        tasks.add(task);
        return ResponseEntity.created(URI.create("/v2/tasks/" + task.id())).body(task);
    }
}
