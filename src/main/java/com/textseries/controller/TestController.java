package com.textseries.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.textseries.model.QuizAttempt;
import com.textseries.model.Test;
import com.textseries.repository.QuizAttemptRepository;
import com.textseries.service.TestService;

@RestController
@RequestMapping("/api/tests")
public class TestController {

	private final TestService service;
	private final QuizAttemptRepository attemptRepo;  

	public TestController(TestService service, QuizAttemptRepository attemptRepo) {
		this.service = service;
		this.attemptRepo = attemptRepo; 
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

	@PostMapping("/start/{testId}")
	public OffsetDateTime startTest(@PathVariable Long testId) {

	    String email = SecurityContextHolder.getContext().getAuthentication().getName();

	    QuizAttempt attempt = attemptRepo
	        .findByEmailAndTestId(email, testId)
	        .orElse(QuizAttempt.builder()
	            .email(email)
	            .testId(testId)
	            .attempts(0)
	            .build());

	    OffsetDateTime start = OffsetDateTime.now();

	    // 🔥 IMPORTANT: overwrite only if null
	    if (attempt.getStartTime() == null) {
	        attempt.setStartTime(start);
	    }

	    attemptRepo.save(attempt);

	    return attempt.getStartTime(); // ✅ return stored value
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
		return service.update(id, t);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

}