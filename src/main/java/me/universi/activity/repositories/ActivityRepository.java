package me.universi.activity.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.universi.activity.entities.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
}
