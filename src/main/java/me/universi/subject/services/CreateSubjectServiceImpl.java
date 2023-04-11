package me.universi.subject.services;

import me.universi.subject.SubjectRepository;
import me.universi.subject.entities.Subject;
import me.universi.subject.exception.SubjectExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateSubjectServiceImpl implements CreateSubjectService {

    private final SubjectRepository subjectRepository;

    @Autowired
    public CreateSubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject createSubject(Subject subject){
        Subject subject1 = this.subjectRepository.findBySubject(subject.getSubject());

        if (subject.getSubject().equals(subject1.getSubject())){
            throw new SubjectExistsException();
        }

        return this.subjectRepository.save(subject);
    }
}
