package com.textseries.service;

import com.textseries.dto.AnalysisDTO;
import com.textseries.dto.SubmitRequestDTO;
import com.textseries.model.Question;
import com.textseries.model.QuizAttempt;
import com.textseries.model.Result;
import com.textseries.model.Test;
import com.textseries.model.User;
import com.textseries.repository.QuestionRepository;
import com.textseries.repository.QuizAttemptRepository;
import com.textseries.repository.ResultRepository;
import com.textseries.repository.TestRepository;
import com.textseries.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResultService {

	private final QuestionRepository questionRepo;
	private final ResultRepository resultRepo;
	private final UserRepository userRepo;
	private final TestRepository testRepo;
	private final QuizAttemptRepository attemptRepo;

	public ResultService(QuestionRepository questionRepo, ResultRepository resultRepo, UserRepository userRepo,
			TestRepository testRepo, QuizAttemptRepository attemptRepo) {
		this.questionRepo = questionRepo;
		this.resultRepo = resultRepo;
		this.userRepo = userRepo;
		this.attemptRepo = attemptRepo;
		this.testRepo = testRepo;
	}

	// 🧠 Submit Test
	public Result calculateResult(SubmitRequestDTO request) {

		String email = SecurityContextHolder.getContext().getAuthentication().getName();

		if (request.getTestId() == null) {
			throw new RuntimeException("❌ TestId missing");
		}

		if (!canAttempt(email, request.getTestId())) {
			throw new RuntimeException("❌ Attempt limit reached");
		}

		Test test = testRepo.findById(request.getTestId()).orElseThrow(() -> new RuntimeException("Test not found"));

		QuizAttempt attempt = attemptRepo.findByEmailAndTestId(email, request.getTestId())
				.orElseThrow(() -> new RuntimeException("❌ Start time not found"));

		OffsetDateTime start = attempt.getStartTime();

		if (start == null) {
			throw new RuntimeException("❌ Test not started properly");
		}

		OffsetDateTime now = OffsetDateTime.now();

		System.out.println("START: " + start);
		System.out.println("NOW: " + now);

		long seconds = Duration.between(start, now).toSeconds();
		System.out.println("SECONDS: " + seconds);
		System.out.println("ALLOWED: " + (test.getTimeLimit() * 60));

		long allowedTime = test.getTimeLimit() * 60;

		OffsetDateTime endTime = start.plusSeconds(test.getTimeLimit() * 60);

		boolean timeOver = OffsetDateTime.now().isAfter(endTime);

		if (timeOver) {
		    System.out.println("⏰ Time Over - Auto Submit");
		}
		int correct = 0;
		int wrong = 0;

		Map<Long, String> answers = request.getAnswers() != null ? request.getAnswers() : Map.of();

		List<Question> allQuestions = questionRepo.findByTestId(request.getTestId());

		for (Question q : allQuestions) {
			String userAns = answers.get(q.getId());

			if (userAns == null)
				wrong++;
			else if (q.getCorrectAnswer().equals(userAns))
				correct++;
			else
				wrong++;
		}

		double score = correct - (wrong * 0.25);

		User user = userRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

		Result result = Result.builder().studentName(user.getStudentName()).email(user.getEmail()).score(score)
				.totalQuestions(correct + wrong).correctAnswers(correct).wrongAnswers(wrong).testId(request.getTestId())
				.submittedAt(LocalDateTime.now()).build();

		Result saved = resultRepo.save(result);

		increaseAttempt(email, request.getTestId());

		return saved;
	}

	public List<AnalysisDTO> getAnalysis(Map<Long, String> answers, Long testId) {

		Map<Long, String> safeAnswers = answers != null ? answers : Map.of();

		List<AnalysisDTO> list = new ArrayList<>();

		if (testId == null) {
			throw new RuntimeException("❌ TestId missing in analysis");
		}

		List<Question> allQuestions = questionRepo.findByTestId(testId);

		if (allQuestions.isEmpty()) {
			throw new RuntimeException("❌ No questions found for testId: " + testId);
		}

		System.out.println("TEST ID RECEIVED: " + testId);

		for (Question q : allQuestions) {

			String userAns = safeAnswers.get(q.getId());

			if (userAns == null || !q.getCorrectAnswer().equals(userAns)) {

				list.add(new AnalysisDTO(q.getQuestion(), q.getOptionA(), q.getOptionB(), q.getOptionC(),
						q.getOptionD(), q.getCorrectAnswer(), userAns == null ? "Not Answered" : userAns));
			}
		}

		return list;
	}

	public boolean canAttempt(String email, Long testId) {

	    QuizAttempt attempt = attemptRepo
	        .findByEmailAndTestId(email, testId)
	        .orElse(null);

	    if (attempt == null) {
	        attempt = QuizAttempt.builder()
	            .email(email)
	            .testId(testId)
	            .attempts(0)
	            .build();

	        attemptRepo.save(attempt);
	    }

	    return attempt.getAttempts() < 20;  
	}

	public void increaseAttempt(String email, Long testId) {
		QuizAttempt attempt = attemptRepo.findByEmailAndTestId(email, testId)
				.orElse(QuizAttempt.builder().email(email).testId(testId).attempts(0).build());

		attempt.setAttempts(attempt.getAttempts() + 1);
		attemptRepo.save(attempt);
	}

	public long totalAttempts() {
		return resultRepo.count();
	}

	// 🏆 Leaderboard
	public List<Result> getLeaderboard() {
		return resultRepo.findBestScores();
	}

	public double getAverageScore() {
		return resultRepo.findAll().stream().mapToDouble(Result::getScore).average().orElse(0);
	}

	public Result getTopper() {
		return resultRepo.findAll().stream().max((a, b) -> Double.compare(a.getScore(), b.getScore())).orElse(null);
	}

	public List<Result> getRecent() {
		return resultRepo.findTop5ByOrderBySubmittedAtDesc();
	}

	// 📜 User History
	public List<Result> getUserHistory(String email) {
		return resultRepo.findByEmailOrderBySubmittedAtDesc(email);
	}
}