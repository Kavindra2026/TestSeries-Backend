package com.textseries.service;

import com.textseries.dto.AnalysisDTO;
import com.textseries.dto.SubmitRequestDTO;
import com.textseries.model.Question;
import com.textseries.model.QuizAttempt;
import com.textseries.model.Result;
import com.textseries.model.User;
import com.textseries.repository.QuestionRepository;
import com.textseries.repository.QuizAttemptRepository;
import com.textseries.repository.ResultRepository;
import com.textseries.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ResultService {

	private final QuestionRepository questionRepo;
	private final ResultRepository resultRepo;
	private final UserRepository userRepo;
	private final QuizAttemptRepository attemptRepo;

	public ResultService(QuestionRepository questionRepo, ResultRepository resultRepo, UserRepository userRepo,
			QuizAttemptRepository attemptRepo) {
		this.questionRepo = questionRepo;
		this.resultRepo = resultRepo;
		this.userRepo = userRepo;
		this.attemptRepo = attemptRepo;
	}

	// 🧠 Submit Test
	public Result calculateResult(SubmitRequestDTO request) {

	    String email = SecurityContextHolder.getContext().getAuthentication().getName();

	    if (request.getCategory() == null || request.getCategory().isEmpty()) {
	        throw new RuntimeException("❌ Category missing");
	    }

	    if (request.getStartTime() == null) {
	        throw new RuntimeException("❌ Start time missing");
	    }

	    if (!canAttempt(email, request.getCategory())) {
	        throw new RuntimeException("❌ Attempt limit reached");
	    }

	    LocalDateTime now = LocalDateTime.now();
	    long seconds = Duration.between(request.getStartTime(), now).toSeconds();

	    int correct = 0;
	    int wrong = 0;

	    Map<Long, String> answers = request.getAnswers() != null ? request.getAnswers() : Map.of();

	    List<Question> allQuestions = questionRepo.findByCategory(request.getCategory());

	    for (Question q : allQuestions) {
	        String userAns = answers.get(q.getId());

	        if (userAns == null) wrong++;
	        else if (q.getCorrectAnswer().equals(userAns)) correct++;
	        else wrong++;
	    }

	    double score = correct - (wrong * 0.25);

	    User user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    Result result = Result.builder()
	            .studentName(user.getStudentName())
	            .email(user.getEmail())
	            .score(score)
	            .totalQuestions(correct + wrong)
	            .correctAnswers(correct)
	            .wrongAnswers(wrong)
	            .category(request.getCategory())
	            .submittedAt(LocalDateTime.now())
	            .build();

	    Result saved = resultRepo.save(result);

	    increaseAttempt(email, request.getCategory());

	    return saved;
	}
	public List<AnalysisDTO> getAnalysis(Map<Long, String> answers, String category){

		Map<Long, String> safeAnswers = answers != null ? answers : Map.of();

		List<AnalysisDTO> list = new ArrayList<>();
		if (category == null || category.isEmpty()) {
		    throw new RuntimeException("❌ Category missing in analysis");
		}

		List<Question> allQuestions = questionRepo.findByCategory(category);

		if (allQuestions.isEmpty()) {
		    throw new RuntimeException("❌ No questions found for category: " + category);
		}
		System.out.println("CATEGORY RECEIVED: " + category);
		
		for (Question q : allQuestions) {

			String userAns = safeAnswers.get(q.getId());

			if (userAns == null || !q.getCorrectAnswer().equals(userAns)) {

				list.add(new AnalysisDTO(q.getQuestion(), q.getOptionA(), q.getOptionB(), q.getOptionC(),
						q.getOptionD(), q.getCorrectAnswer(), userAns == null ? "Not Answered" : userAns));
			}
		}

		return list;
	}


	public boolean canAttempt(String email, String category) {
		QuizAttempt attempt = attemptRepo.findByEmailAndCategory(email, category).orElse(null);

		if (attempt == null)
			return true;

		return attempt.getAttempts() < 20;
	}

	public void increaseAttempt(String email, String category) {
		QuizAttempt attempt = attemptRepo.findByEmailAndCategory(email, category)
				.orElse(QuizAttempt.builder().email(email).category(category).attempts(0).build());

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
		return resultRepo.findAll().stream().mapToDouble(Result::getScore) // ✅ correct
				.average().orElse(0);
	}

	public Result getTopper() {
		return resultRepo.findAll().stream().max((a, b) -> Double.compare(a.getScore(), b.getScore())) // ✅
				.orElse(null);
	}

	public List<Result> getRecent() {
		return resultRepo.findTop5ByOrderBySubmittedAtDesc();
	}

	// 📜 User History
	public List<Result> getUserHistory(String email) {
		return resultRepo.findByEmailOrderBySubmittedAtDesc(email);
	}
}