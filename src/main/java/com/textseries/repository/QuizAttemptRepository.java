package com.textseries.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.textseries.model.QuizAttempt;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

	Optional<QuizAttempt> findByEmailAndCategory(String email, String category);
}
