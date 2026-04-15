package com.textseries.controller;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.textseries.dto.QuestionDTO;
import com.textseries.model.Question;
import com.textseries.service.QuestionService;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:5173")
public class QuestionController {

	private final QuestionService service;

	public QuestionController(QuestionService service) {
		this.service = service;
	}

	@PostMapping
	public Question add(@RequestBody Question q) {
		return service.addQuestion(q);
	}

	@PostMapping("/bulk")
	public List<Question> addBulk(@RequestBody List<Question> questions) {
	    return questions.stream()
	            .map(service::addQuestion)
	            .toList();
	}
	
	@GetMapping("/category/{category}")
	public List<QuestionDTO> getQuiz(
	        @PathVariable String category,
	        Authentication auth) {

	    String studentName= auth.getName();
	    return service.getQuiz(category, studentName);
	}
	
	@PutMapping("/{id}")
	public Question update(@PathVariable Long id, @RequestBody Question q) {
		return service.update(id, q);
	}

	@GetMapping
	public List<Question> all() {
		return service.getAll();
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

}
