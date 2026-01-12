package com.sparta.cafeorderback.repository;

import com.sparta.cafeorderback.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

}
