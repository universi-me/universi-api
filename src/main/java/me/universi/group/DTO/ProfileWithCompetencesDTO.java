package me.universi.group.DTO;

import me.universi.competence.entities.Competence;
import me.universi.profile.enums.Gender;
import me.universi.user.entities.User;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public record ProfileWithCompetencesDTO(
        UUID id,
        User user,
        String firstname,
        String lastname,
        String image,
        String bio,
        Gender gender,
        Date creationDate,
        Collection<Competence> competences
) {

    public boolean hasCompetence(String typeId, int level){
        for(Competence competence : competences){
            if(competence.getCompetenceType().getId().toString().equals(typeId) && competence.getLevel() == level)
                return true;
        }
        return false;
    }

}
