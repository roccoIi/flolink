package com.flolink.backend.domain.plant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flolink.backend.domain.plant.entity.plantexp.PlantUserExpHistory;

public interface PlantUserExpHistoryRepository extends JpaRepository<PlantUserExpHistory, Integer> {

	@Query("SELECT u FROM PlantUserExpHistory u WHERE u.plant.plantId = :plantId AND u.dateMonth = :dateMonth ORDER BY u.contributeExp DESC")
	List<PlantUserExpHistory> findByPlantIdAndDateMonth(Integer plantId, String dateMonth);

}
