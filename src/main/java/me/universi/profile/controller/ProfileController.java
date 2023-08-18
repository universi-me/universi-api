package me.universi.profile.controller;

import me.universi.api.entities.Response;
import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/profile")
public class ProfileController {
    @Autowired
    public UserService userService;

    @Autowired
    public ProfileService profileService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile() {
        Response response = new Response(); // default
        try {

            User userSession = userService.getUserInSession();

            Profile userProfile = userSession.getProfile();
            if(userProfile == null) {
                throw new ProfileException("Perfil não encontrado.");
            }

            response.body.put("profile", userProfile);
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response perfil_editar(@RequestBody Map<String, Object> body) {

        Response response = new Response(); // default

        try {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            Object name      = body.get("name");
            Object lastname  = body.get("lastname");
            Object imageUrl  = body.get("imageUrl");
            Object bio       = body.get("bio");
            Object gender    = body.get("gender");

            if(!userService.isSessionOfUser(profileGet.getUser())) {
                User userSession = userService.getUserInSession();
                if(!userService.isUserAdmin(userSession)) {
                    throw new ProfileException("Você não tem permissão para editar este perfil.");
                }
            }

            if(name != null) {
                profileGet.setFirstname(String.valueOf(name));
            }
            if(lastname != null) {
                profileGet.setLastname(String.valueOf(lastname));
            }
            if(imageUrl != null) {
                String imageUrlString = String.valueOf(imageUrl);
                if(imageUrlString.length()>0) {
                    profileGet.setImage(imageUrlString);
                }
            }
            if(bio != null) {
                profileGet.setBio(String.valueOf(bio));
            }
            if(gender != null) {
                String genderString = String.valueOf(gender);
                if(genderString.length()>0) {
                    profileGet.setGender(Gender.valueOf(genderString));
                }
            }

            profileService.save(profileGet);

            userService.updateUserInSession();

            response.message = "As Alterações foram salvas com sucesso.";
            response.success = true;
            response.redirectTo = "/profile/" + profileGet.getUser().getUsername();

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_get(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("profile", profileGet);
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }

    @PostMapping(value = "/recomendations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_recomendations(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("recomendationsSend", profileGet.getRecomendationsSend());
            response.body.put("recomendationsReceived", profileGet.getRecomendationsReceived());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }

    @PostMapping(value = "/groups", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_groups(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("groups", profileGet.getGroups());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }

    @PostMapping(value = "/links", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_links(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("links", profileGet.getLinks());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }

    @PostMapping(value = "/competences", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_competences(@RequestBody Map<String, Object> body) {
        Response response = new Response(); // default
        try {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("competences", profileGet.getCompetences());
            response.success = true;

        } catch (Exception e) {
            response.message = e.getMessage();
        }

        return response;
    }
}
