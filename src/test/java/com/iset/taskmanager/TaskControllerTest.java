package com.iset.taskmanager;

import com.iset.taskmanager.controller.TaskController;
import com.iset.taskmanager.model.Task;
import com.iset.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class TaskControllerTest {

    @Test
    void testCreateAndListTasks() {
        TaskController controller = new TaskController();
        int initialSize = controller.getAllTasks().size();

        Task created = controller.createTask(new Task(null, "Déployer sur Minikube", false));
        assertNotNull(created.getId());
        assertEquals("Déployer sur Minikube", created.getTitle());
        assertFalse(created.isCompleted());
        assertEquals(initialSize + 1, controller.getAllTasks().size());
    }

    @Test
    void testCompleteAndDeleteTask() {
        TaskController controller = new TaskController();
        Task created = controller.createTask(new Task(null, "Exécuter OWASP ZAP", false));

        ResponseEntity<Task> completed = controller.completeTask(created.getId());
        assertEquals(HttpStatus.OK, completed.getStatusCode());
        assertNotNull(completed.getBody());
        assertTrue(completed.getBody().isCompleted());

        ResponseEntity<Void> deleted = controller.deleteTask(created.getId());
        assertEquals(HttpStatus.NO_CONTENT, deleted.getStatusCode());
    }

    @Test
    void testNotFoundResponses() {
        TaskController controller = new TaskController();
        assertEquals(HttpStatus.NOT_FOUND, controller.completeTask(9999L).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, controller.deleteTask(9999L).getStatusCode());
    }

    @Test
    void testTaskServiceOperations() {
        TaskService service = new TaskService();
        Task task = service.createTask("Audit de sécurité Trivy");
        assertTrue(service.findById(task.getId()).isPresent());
        assertTrue(service.markAsCompleted(task.getId()).isPresent());
        assertTrue(service.deleteById(task.getId()));
        assertFalse(service.findById(task.getId()).isPresent());
    }
}
