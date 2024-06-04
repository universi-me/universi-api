package me.universi.profile.services;

import me.universi.Sys;
import me.universi.curriculum.education.entities.Education;
import me.universi.curriculum.experience.entities.Experience;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.repositories.PerfilRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired
    private PerfilRepository perfilRepository;

    public static ProfileService getInstance() {
        return Sys.context.getBean("profileService", ProfileService.class);
    }

    // Retorna um Perfil passando o id
    public Profile findFirstById(UUID id) {
        return perfilRepository.findFirstById(id).orElse(null);
    }

    // Retorna um Perfil passando o id como string
    public Profile findFirstById(String id) {
        return perfilRepository.findFirstById(UUID.fromString(id)).orElse(null);
    }

    public void save(Profile profile) {
        perfilRepository.saveAndFlush(profile);
    }

    public void saveAll(Collection<Profile> profile) {
        perfilRepository.saveAllAndFlush(profile);
    }

    public void update(Profile profile) {
        perfilRepository.saveAndFlush(profile);
    }

    public Collection<Profile> findAll(){ return perfilRepository.findAll(); }

    public void delete(Profile profile) {
        perfilRepository.delete(profile);
    }

    public void deleteAll() { perfilRepository.deleteAll();}

    public Profile getProfileByUserIdOrUsername(Object profileId, Object username) throws ProfileException {

        if(profileId == null && username == null) {
            throw new ProfileException("Parametro perfilId e username é nulo.");
        }

        Profile profileGet = null;

        if(profileId != null) {
            String profileIdString = String.valueOf(profileId);
            if(profileIdString.length() > 0) {
                profileGet = findFirstById(profileIdString);
            }
        }
        if(profileGet == null && username != null) {
            String usernameString = String.valueOf(username);
            if(usernameString.length() > 0) {
                profileGet = ((User)UserService.getInstance().loadUserByUsername(usernameString)).getProfile();
            }
        }

        if(profileGet == null) {
            throw new ProfileException("Perfil não encontrado.");
        }

        return profileGet;
    }

    public Collection<Education> findEducationByProfile(Profile profile){
        Collection<Education> educationsActive = new ArrayList<>();
        for (Education education: profile.getEducations()) {
            if (!education.isDeleted()){
                educationsActive.add(education);
            }
        }
        return educationsActive;
    }

    public Collection<Experience> findExperienceByProfile(Profile profile){
        Collection<Experience> experiencesActive = new ArrayList<>();
        for (Experience experience: profile.getExperiences()) {
            if (!experience.isDeleted()){
                experiencesActive.add(experience);
            }
        }
        return experiencesActive;
    }

    // search the first 5 containing the string uppercase or lowercase
    public Collection<Profile> findTop5ByNameContainingIgnoreCase(String nome){ return perfilRepository.findTop5ByFirstnameContainingIgnoreCase(nome); }

    public Profile getProfileInSession() {
        return findFirstById(UserService.getInstance().getUserInSession().getProfile().getId());
    }

    public boolean isSessionOfProfile(@NotNull Profile profile) {
        return profile.getId().equals(getProfileInSession().getId());
    }
}
