package me.universi.link.services;

import me.universi.link.entities.Link;
import me.universi.link.repositories.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LinkService {
    @Autowired
    private LinkRepository linkRepository;

    public Link findFirstById(Long id) {
        Optional<Link> competenciaOptional = linkRepository.findFirstById(id);
        if(competenciaOptional.isPresent()){
            return competenciaOptional.get();
        }else{
            return null;
        }
    }

    public void save(Link competencia) {
        linkRepository.save(competencia);
    }

    public void delete(Link competencia) {
        linkRepository.delete(competencia);
    }

    public List<Link> findAll() {
        return linkRepository.findAll();
    }
}