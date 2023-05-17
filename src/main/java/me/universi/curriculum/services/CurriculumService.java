package me.universi.curriculum.services;

import me.universi.competence.entities.Competence;
import me.universi.curriculum.entities.Curriculum;
import me.universi.curriculum.repositories.CurriculumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository;

    public Curriculum save(Curriculum curriculum){
        return curriculumRepository.save(curriculum);
    }
    public void delete(Long id) {
        curriculumRepository.deleteById(id);
    }

    public List<Curriculum> findAll() {
        return curriculumRepository.findAll();
    }

    public Optional<Curriculum> findById(Long id){
        return curriculumRepository.findById(id);
    }

    public void update(Curriculum curriculum){
        curriculumRepository.saveAndFlush(curriculum);
    }

}
