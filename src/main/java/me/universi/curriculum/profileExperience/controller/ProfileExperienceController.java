package me.universi.curriculum.profileExperience.controller;

import me.universi.curriculum.education.entities.TypeEducation;
import me.universi.curriculum.profileExperience.entities.ProfileExperience;
import me.universi.curriculum.profileExperience.servicies.ProfileExperienceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/curriculum/profileExperience")
public class ProfileExperienceController {

    private ProfileExperienceService profileExperienceService;

    public ProfileExperienceController(ProfileExperienceService profileExperienceService){
        this.profileExperienceService = profileExperienceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileExperience save(@RequestBody ProfileExperience profileExperience) throws Exception{
        return  profileExperienceService.save(profileExperience);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProfileExperience> findAll() throws Exception{
        return profileExperienceService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ProfileExperience> getProfileExperience(@PathVariable UUID id){
        return profileExperienceService.findById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProfileExperience update(@RequestBody ProfileExperience newProfileExperience, @PathVariable UUID id) throws Exception {
        return profileExperienceService.update(newProfileExperience, id);
    }
}
