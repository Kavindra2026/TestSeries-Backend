package com.textseries.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textseries.model.Test;
import com.textseries.service.TestService;

@RestController
@RequestMapping("/api/tests")
public class TestController {

	private final TestService service;

	public TestController(TestService service) {
		this.service = service;
	}
 
	// Admin create test
	@PostMapping
	public Test create(@RequestBody Test t) {
		if (t.getTestName() == null || t.getTestName().isEmpty()) {
			throw new RuntimeException("❌ Test name required");
		}
		return service.create(t);
	}
	
	@GetMapping("/active")
	public List<Test> getActive() {
	    return service.getAllActive();
	}
	
	
	@GetMapping
	public List<Test> getAll() {
	    return service.getAll();    
	}
	
	// Get single test
	@GetMapping("/{id}")
	public Test getById(@PathVariable Long id) {
		return service.getById(id);
	}
	
	
	@PutMapping("/{id}")
	public Test update(@PathVariable Long id, @RequestBody Test t) {
	    Test existing = service.getById(id);
	    existing.setTestName(t.getTestName());
	    existing.setSubject(t.getSubject());
	    existing.setTotalQuestions(t.getTotalQuestions());
	    existing.setTimeLimit(t.getTimeLimit());
	    existing.setActive(t.isActive());
	    return service.create(existing);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
	    service.delete(id);
	}

}