package com.example.minitasks.web;

import com.example.minitasks.domain.SimpleTask;
import com.example.minitasks.dto.CreateTaskDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * V1 de la API: los datos viven en memoria, sin servicio ni persistencia.
 */
@RestController
@RequestMapping("/v1/tasks")
public class TaskControllerV1 {

    private final List<SimpleTask> tasks = new ArrayList<>();

    @GetMapping
    public List<SimpleTask> list() {
        return tasks;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimpleTask> get(@PathVariable("id") long id) {
        return ResponseEntity.ok(tasks.stream()
                .filter(task -> task.id() == id)
                .findFirst()
                .orElseThrow());
    }

    @PostMapping
    public ResponseEntity<SimpleTask> create(@RequestBody CreateTaskDTO dto) {
        SimpleTask task = new SimpleTask(tasks.size() + 1, dto.title(), false);
        tasks.add(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }
}
