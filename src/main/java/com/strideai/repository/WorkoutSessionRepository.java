package com.strideai.repository;

import com.strideai.model.WorkoutSession;
import com.strideai.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {

    Page<WorkoutSession> findByUserOrderByStartedAtDesc(User user, Pageable pageable);

    long countByUser(User user);

    @Query("SELECT COALESCE(SUM(w.totalReps), 0) FROM WorkoutSession w WHERE w.user = :user AND w.status = 'COMPLETED'")
    long sumTotalRepsByUser(@Param("user") User user);

    @Query("SELECT AVG(w.avgAccuracy) FROM WorkoutSession w WHERE w.user = :user AND w.status = 'COMPLETED'")
    Double avgAccuracyByUser(@Param("user") User user);

    List<WorkoutSession> findTop7ByUserOrderByStartedAtDesc(User user);
}
