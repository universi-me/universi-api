package me.universi.job.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import me.universi.job.entities.Job;

public interface JobRepository extends JpaRepository<Job, UUID> {
}
