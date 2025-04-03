package com.example.todo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

// Spring Boot 메인 애플리케이션
@SpringBootApplication
public class TodoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}

// To-Do 엔티티 정의
@Entity
class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean completed;

    // 기본 생성자
    public Todo() {}

    // 생성자
    public Todo(String title, boolean completed) {
        this.title = title;
        this.completed = completed;
    }

    // Getter & Setter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
    public void setTitle(String title) { this.title = title; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

// JPA 리포지토리 인터페이스
@Repository
interface TodoRepository extends JpaRepository<Todo, Long> {}

// 서비스 레이어
@Service
class TodoService {
    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    public List<Todo> getTodos() {
        return repository.findAll();
    }

    public Todo createTodo(Todo todo) {
        return repository.save(todo);
    }

    public Todo updateTodo(Long id, Todo newTodo) {
        return repository.findById(id).map(todo -> {
            todo.setTitle(newTodo.getTitle());
            todo.setCompleted(newTodo.isCompleted());
            return repository.save(todo);
        }).orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    public void deleteTodo(Long id) {
        repository.deleteById(id);
    }
}

// REST 컨트롤러
@RestController
@RequestMapping("/todos")
@CrossOrigin(origins = "*") // CORS 문제 해결
class TodoController {
    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Todo> getTodos() {
        return service.getTodos();
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return service.createTodo(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        return service.updateTodo(id, todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        service.deleteTodo(id);
    }
}
