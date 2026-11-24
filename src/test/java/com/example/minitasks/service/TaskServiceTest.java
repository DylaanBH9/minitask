package com.example.minitasks.service;

import com.example.minitasks.domain.Task;
import com.example.minitasks.dto.CreateTaskDTO;
import com.example.minitasks.dto.TaskResponseDTO;
import com.example.minitasks.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository repository;

    @InjectMocks
    TaskService service;

    @Test
    void createStripsTitleEdges() {
        when(repository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        TaskResponseDTO result = service.createTask(new CreateTaskDTO("  Repasar Spring Boot  "));

        assertThat(result.title()).isEqualTo("Repasar Spring Boot");
    }

    @Test
    void createKeepsCleanTitleAndDoneFalse() {
        when(repository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        TaskResponseDTO result = service.createTask(new CreateTaskDTO("Repasar Spring Boot"));

        assertThat(result.title()).isEqualTo("Repasar Spring Boot");
        assertThat(result.done()).isFalse();
    }

    @Test
    void completeMarksTaskDone() {
        Task task = new Task("Repasar Spring Boot");
        task.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        TaskResponseDTO result = service.completeTask(1L);

        assertThat(result.done()).isTrue();
    }

    @Test
    void completeTwiceKeepsTaskDone() {
        Task task = new Task("Repasar Spring Boot");
        task.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(any(Task.class))).thenAnswer(call -> call.getArgument(0));

        service.completeTask(1L);
        TaskResponseDTO result = service.completeTask(1L);

        assertThat(result.done()).isTrue();
    }
}
