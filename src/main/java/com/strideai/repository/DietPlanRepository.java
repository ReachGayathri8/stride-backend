package com.strideai.repository;

import com.strideai.model.DietPlan;
import com.strideai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, UUID> {
    Optional<DietPlan> findTopByUserOrderByValidFromDesc(User user);
}
