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
 
import org.springframework.web.bind.annotation.RestController;

import com.textseries.dto.QuestionDTO;
import com.textseries.model.Question;
import com.textseries.model.QuestionRequestDTO;
import com.textseries.model.Test;
import com.textseries.repository.TestRepository;
import com.textseries.service.QuestionService;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "http://localhost:5173")
public class QuestionController {

	private final QuestionService service;
	private final TestRepository testRepo;

	public QuestionController(QuestionService service, TestRepository testRepo) {
		this.service = service;
		this.testRepo = testRepo;
	}

	@PostMapping
	public Question add(@RequestBody QuestionRequestDTO dto) {

		System.out.println("DTO RECEIVED: " + dto);
		System.out.println("TEST ID: " + dto.getTestId());

		if (dto.getTestId() == null) {
			throw new RuntimeException("Test ID missing ❌");
		}

		Test test = testRepo.findById(dto.getTestId()).orElseThrow(() -> new RuntimeException("Test not found ❌"));

		Question q = new Question();
		q.setQuestion(dto.getQuestion());
		q.setOptionA(dto.getOptionA());
		q.setOptionB(dto.getOptionB());
		q.setOptionC(dto.getOptionC());
		q.setOptionD(dto.getOptionD());
		q.setCorrectAnswer(dto.getCorrectAnswer());
		q.setTest(test);

		return service.addQuestion(q);
	}

	@PostMapping("/bulk")
	public List<Question> addBulk(@RequestBody List<Question> questions) {
		return questions.stream().map(service::addQuestion).toList();
	}

	@GetMapping("/test/{testId}")
	public List<QuestionDTO> getQuizByTest(@PathVariable Long testId, Authentication auth) {

		String studentName = (auth != null) ? auth.getName() : "guest";
		return service.getQuizByTest(testId, studentName);
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
