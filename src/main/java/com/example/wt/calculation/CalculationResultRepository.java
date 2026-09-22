package com.example.wt.calculation;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalculationResultRepository extends JpaRepository<CalculationResult, Long> {

    List<CalculationResult> findAllByOrderByCreatedAtDescIdDesc(Pageable pageable);
}
