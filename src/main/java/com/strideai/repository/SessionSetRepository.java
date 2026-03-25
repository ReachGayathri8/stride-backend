package com.strideai.repository;

import com.strideai.model.SessionSet;
import com.strideai.model.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionSetRepository extends JpaRepository<SessionSet, UUID> {
    List<SessionSet> findBySession(WorkoutSession session);
}
