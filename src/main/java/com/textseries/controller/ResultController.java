package com.textseries.controller;

import com.textseries.dto.AnalysisDTO;
import com.textseries.dto.SubmitRequestDTO;
import com.textseries.model.Result;
import com.textseries.service.ResultService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/result")

public class ResultController {

	private final ResultService service;

	public ResultController(ResultService service) {
		this.service = service;
	}

	@PostMapping
	public Result submit(@RequestBody SubmitRequestDTO request) {
		if (request.getAnswers() == null) {
		    throw new RuntimeException("Answers missing ❌");
		}
		
		return service.calculateResult(request);
	}

	// 🏆 Leaderboard
	@GetMapping("/leaderboard")
	public List<Result> leaderboard() {
		return service.getLeaderboard();
	}

	@PostMapping("/analysis")
	public List<AnalysisDTO> analysis(@RequestBody SubmitRequestDTO request) {
	    return service.getAnalysis(request.getAnswers(), request.getTestId());
	}

	// 📜 History
	@GetMapping("/history")
	public List<Result> history(Authentication auth) {

		if (auth == null) {
			throw new RuntimeException("Unauthorized");
		}

		String email = auth.getName();
		return service.getUserHistory(email);
	}

	@GetMapping("/can-attempt/{testId}")
	public boolean canAttempt(Authentication auth, @PathVariable Long testId) {
	    return service.canAttempt(auth.getName(), testId);
	}
	
	@GetMapping("/admin/analytics")
	public long totalAttempts() {
		return service.totalAttempts();
	}

	@GetMapping("/admin/avg-score")
	public double avgScore() {
		return service.getAverageScore();
	}

	@GetMapping("/admin/topper")
	public Result topper() {
		return service.getTopper();
	}

	@GetMapping("/admin/recent")
	public List<Result> getRecent() {
		return service.getRecent();
	}

}