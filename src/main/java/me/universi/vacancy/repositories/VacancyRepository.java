package me.universi.vacancy.repositories;

import me.universi.vacancy.entities.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, UUID> {
    Vacancy findFirstById(UUID id);
}
