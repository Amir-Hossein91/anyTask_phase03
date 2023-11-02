package com.example.phase_03.repository;

import com.example.phase_03.entity.Assistance;
import com.example.phase_03.entity.SubAssistance;
import com.example.phase_03.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubAssistanceRepository extends JpaRepository<SubAssistance,Long> {
    Optional<SubAssistance> findByTitleAndAssistance (String title, Assistance assistance);

    Optional<List<SubAssistance>> findByTechniciansContaining(Technician technician);
}
