package com.textseries.service;

import com.textseries.model.Test;
import com.textseries.repository.TestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestService {

    private final TestRepository repo;

    public TestService(TestRepository repo) {
        this.repo = repo;
    }

    public Test create(Test t) {
        return repo.save(t);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
    public List<Test> getAllActive() {
        return repo.findByActiveTrue();
    }
    
    public List<Test> getAll() {
        return repo.findAll();
    }
    
    public Test getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found"));
    }
    
    public Test update(Long id, Test updated) {
        Test t = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Test not found"));

        t.setTestName(updated.getTestName());
        t.setSubject(updated.getSubject());
        t.setTotalQuestions(updated.getTotalQuestions());
        t.setTimeLimit(updated.getTimeLimit());
        t.setActive(updated.isActive());

        return repo.save(t);
    }
}