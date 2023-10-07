package me.universi.vacancy.typeVacancy.repository;

import me.universi.vacancy.typeVacancy.entities.TypeVacancy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TypeVacancyRepository extends JpaRepository<TypeVacancy, UUID> {
}
