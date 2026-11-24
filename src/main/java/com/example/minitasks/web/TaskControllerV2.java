package com.example.minitasks.web;

import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.dto.TaskResponseDTO;
import com.example.minitasks.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * V2 de la API: la creación delega en la capa de servicio.
 */
@RestController
@RequestMapping("/v2/tasks")
public class TaskControllerV2 {

    private final TaskService service;

    public TaskControllerV2(TaskService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@RequestBody CreateTaskDTO dto) {
        TaskResponseDTO created = service.createTask(dto);
        return ResponseEntity.created(URI.create("/v2/tasks/" + created.id())).body(created);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponseDTO> complete(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.completeTask(id));
    }
}
