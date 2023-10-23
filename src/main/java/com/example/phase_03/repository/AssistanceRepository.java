package com.example.phase_03.repository;

import com.example.phase_03.entity.Assistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AssistanceRepository extends JpaRepository<Assistance,Long> {
    Optional<Assistance> findByTitle (String title);

}
