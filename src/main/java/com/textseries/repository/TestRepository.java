package com.textseries.repository;
 
import com.textseries.model.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, Long> {

    List<Test> findByActiveTrue();
    
}