package kr.co.lotteon.repository;

import kr.co.lotteon.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface pointHistoryRepository extends JpaRepository<PointHistory, Integer> {
}
