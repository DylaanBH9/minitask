package com.example.minitasks.service;

import com.example.minitasks.domain.Task;
import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.dto.TaskResponseDTO;
import com.example.minitasks.repository.TaskRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponseDTO createTask(CreateTaskDTO dto) {
        Task saved = repository.save(new Task(dto.title()));
        return new TaskResponseDTO(saved.getId(), saved.getTitle(), saved.isDone());
    }
}
