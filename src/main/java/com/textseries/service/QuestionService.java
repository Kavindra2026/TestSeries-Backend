package com.textseries.service;

import com.textseries.dto.QuestionDTO;
import com.textseries.model.Question;
import com.textseries.repository.QuestionRepository;
import com.textseries.repository.TestRepository;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService {

	private final QuestionRepository repo;

 
	public QuestionService(QuestionRepository repo ) {
	    this.repo = repo;
	}

	// Add Question
	public Question addQuestion(Question q) {
		return repo.save(q);
	}

	public List<Question> getAll() {
		return repo.findAll();
	}

	public Question update(Long id, Question updated) {

		Question q = repo.findById(id).orElseThrow(() -> new RuntimeException("Question not found"));

		q.setQuestion(updated.getQuestion());
		q.setOptionA(updated.getOptionA());
		q.setOptionB(updated.getOptionB());
		q.setOptionC(updated.getOptionC());
		q.setOptionD(updated.getOptionD());
		q.setCorrectAnswer(updated.getCorrectAnswer());
		q.setTest(updated.getTest());

		return repo.save(q);
	}

	public void delete(Long id) {
		repo.deleteById(id);
	}

	// Get Quiz
	public List<QuestionDTO> getQuizByTest(Long testId, String username) {

	    List<Question> questions = repo.findByTestId(testId);

	    Collections.shuffle(questions, new Random(Objects.hash(username, testId)));

	    return questions.stream()
	            .map(this::convertToDTO)
	            .toList();
	}

	private QuestionDTO convertToDTO(Question q) {
		QuestionDTO dto = new QuestionDTO();
		dto.setId(q.getId());
		dto.setQuestion(q.getQuestion());
		dto.setOptionA(q.getOptionA());
		dto.setOptionB(q.getOptionB());
		dto.setOptionC(q.getOptionC());
		dto.setOptionD(q.getOptionD());
		return dto;
	}
}

