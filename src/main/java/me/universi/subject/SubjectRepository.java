package me.universi.subject;

import me.universi.subject.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, UUID>{

    Subject findFirstBySubject(String subject);
}
