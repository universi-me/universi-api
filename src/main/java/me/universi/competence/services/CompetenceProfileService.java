package me.universi.competence.services;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.competence.entities.Competence;
import me.universi.competence.entities.CompetenceProfile;
import me.universi.competence.entities.CompetenceType;
import me.universi.competence.repositories.CompetenceProfileRepository;
import me.universi.profile.entities.Profile;

@Service
public class CompetenceProfileService {
    private final CompetenceProfileRepository competenceProfileRepository;

    public CompetenceProfileService( CompetenceProfileRepository competenceProfileRepository ) {
        this.competenceProfileRepository = competenceProfileRepository;
    }

    public static CompetenceProfileService getInstance( ) {
        return Sys.context.getBean("competenceProfileService", CompetenceProfileService.class);
    }

    public List<CompetenceProfile> findAll( ) {
        return competenceProfileRepository.findAll().stream()
            .filter( this::validate )
            .toList();
    }

    public List<CompetenceProfile> findByProfile( @NotNull Profile profile ) {
        return findAll().stream()
            .filter( cp -> cp.getProfile().getId().equals( profile.getId() ) )
            .toList();
    }

    public Optional<CompetenceProfile> findByProfile( @NotNull Profile profile, @NotNull CompetenceType competenceType ) {
        return findByProfile( profile ).stream()
            .filter( cp -> cp.getCompetence().getCompetenceType().getId().equals( competenceType.getId() ) )
            .findAny();
    }

    public @NotNull CompetenceProfile addToProfile( @NotNull Profile profile, @NotNull Competence competence ) {
        return competenceProfileRepository.saveAndFlush( new CompetenceProfile( profile, competence ) );
    }

    public List<@NotNull CompetenceProfile> addToProfile( @NotNull Profile profile, Collection<@NotNull Competence> competences ) {
        var competenceProfiles = competences.stream()
            .map( c -> competenceProfileRepository.save( new CompetenceProfile( profile, c ) ) )
            .toList();

        competenceProfileRepository.flush();
        return competenceProfiles;
    }

    public boolean validate( CompetenceProfile competenceProfile ) {
        return competenceProfile != null
            && CompetenceService.getInstance().validate( competenceProfile.getCompetence() );
    }
}
