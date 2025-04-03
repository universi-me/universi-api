package me.universi.group.DTO;

import me.universi.competence.entities.Competence;
import me.universi.image.entities.ImageMetadata;
import me.universi.profile.entities.Profile;
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
        ImageMetadata image,
        String bio,
        Gender gender,
        Date creationDate,
        Collection<Competence> competences
) {
    public ProfileWithCompetencesDTO( Profile profile, Collection<Competence> competences ) {
        this(
            profile.getId(),
            profile.getUser(),
            profile.getFirstname(),
            profile.getLastname(),
            profile.getImage(),
            profile.getBio(),
            profile.getGender(),
            profile.getCreationDate(),
            competences
        );
    }

    public boolean hasCompetence(String typeId, int level){
        for(Competence competence : competences){
            if(competence.getCompetenceType().getId().toString().equals(typeId) && competence.getLevel() == level)
                return true;
        }
        return false;
    }

}
