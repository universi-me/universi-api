package me.universi.subject;

import me.universi.subject.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>{

    Subject findBySubject(String subject);
}
