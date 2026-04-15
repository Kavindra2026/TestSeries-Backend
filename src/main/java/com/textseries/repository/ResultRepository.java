
package com.textseries.repository;

import com.textseries.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {

	// Har student ka best score
	@Query(value = """
		    SELECT * FROM result r
		    WHERE r.id = (
		        SELECT r2.id FROM result r2
		        WHERE r2.email = r.email   -- ✅ CHANGE HERE
		        ORDER BY r2.score DESC, r2.submitted_at DESC
		        LIMIT 1
		    )
		    ORDER BY r.score DESC
		""", nativeQuery = true)
		List<Result> findBestScores();
	  List<Result> findTop5ByOrderBySubmittedAtDesc();
	  List<Result> findByEmailOrderBySubmittedAtDesc(String email);

	List<Result> findByStudentNameOrderBySubmittedAtDesc(String studentName);
}