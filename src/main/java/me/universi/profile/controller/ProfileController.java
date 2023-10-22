package me.universi.profile.controller;

import me.universi.api.entities.Response;
import me.universi.capacity.service.CapacityService;
import me.universi.profile.entities.Profile;
import me.universi.profile.enums.Gender;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/profile")
public class ProfileController {
    @Autowired
    public UserService userService;

    @Autowired
    public ProfileService profileService;

    @Autowired
    public CapacityService capacityService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile() {
        return Response.buildResponse(response -> {

            User userSession = userService.getUserInSession();

            Profile userProfile = userSession.getProfile();
            if(userProfile == null) {
                throw new ProfileException("Perfil não encontrado.");
            }

            response.body.put("profile", userProfile);

        });
    }

    @PostMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_edit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

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
                String nameString = String.valueOf(name);
                if(nameString.isEmpty()) {
                    throw new ProfileException("O nome não pode estar vazio.");
                } else if(nameString.length() > 50) {
                    throw new ProfileException("O nome não pode ter mais de 50 caracteres.");
                }
                profileGet.setFirstname(nameString);
            }
            if(lastname != null) {
                String lastnameString = String.valueOf(lastname);
                if(lastnameString.isEmpty()) {
                    throw new ProfileException("O sobrenome não pode estar vazio.");
                } else if(lastnameString.length() > 50) {
                    throw new ProfileException("O sobrenome não pode ter mais de 50 caracteres.");
                }
                profileGet.setLastname(lastnameString);
            }
            if(imageUrl != null) {
                String imageUrlString = String.valueOf(imageUrl);
                if(!imageUrlString.isEmpty()) {
                    if(imageUrlString.length() > 255) {
                        throw new ProfileException("A URL da imagem não pode ter mais de 255 caracteres.");
                    }
                    profileGet.setImage(imageUrlString);
                }
            }
            if(bio != null) {
                profileGet.setBio(String.valueOf(bio));
            }
            if(gender != null) {
                String genderString = String.valueOf(gender);
                if(!genderString.isEmpty()) {
                    if(genderString.length() > 4) {
                        throw new ProfileException("O gênero não pode ter mais de 4 caractere.");
                    }
                    profileGet.setGender(Gender.valueOf(genderString));
                }
            }

            profileService.save(profileGet);

            userService.updateUserInSession();

            response.message = "As Alterações foram salvas com sucesso.";
            response.redirectTo = "/profile/" + profileGet.getUser().getUsername();

        });
    }

    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_get(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("profile", profileGet);

        });
    }

    @PostMapping(value = "/recomendations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_recomendations(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("recomendationsSend", profileGet.getRecomendationsSend());
            response.body.put("recomendationsReceived", profileGet.getRecomendationsReceived());

        });
    }

    @PostMapping(value = "/groups", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_groups(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("groups", profileGet.getGroups());

        });
    }

    @PostMapping(value = "/links", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_links(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("links", profileGet.getLinks());

        });
    }

    @PostMapping(value = "/competences", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_competences(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));

            response.body.put("competences", profileGet.getCompetences());

        });
    }

    @PostMapping(value = "/folders", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_folders(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            Object profileId = body.get("profileId");
            Object username = body.get("username");
            Object assignedOnly = body.get("assignedOnly");

            if (profileId == null && username == null) {
                throw new IllegalArgumentException("Parâmetro profileId ou username devem ser informados");
            }

            Profile profile = profileService.getProfileByUserIdOrUsername(profileId, username);
            response.body.put("folders", capacityService.findFoldersByProfile(profile.getId(), Boolean.valueOf(String.valueOf(assignedOnly))));
        });
    }

    // get image of profile
    @GetMapping(value = "/image/{profileId}")
    public ResponseEntity<Void> get_image(@PathVariable String profileId) {
        Profile profile = profileService.findFirstById(profileId);
        if(profile != null) {
            if(profile.getImage() != null) {
                String urlImage = (profile.getImage().startsWith("/")) ? "/api" + profile.getImage() : profile.getImage();
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(urlImage)).build();
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
    }

    @PostMapping(value = "/educations", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_educations(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
                Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));
                response.body.put("educations", profileService.findEducationByProfile(profileGet));
        });
    }

    @PostMapping(value = "/experiences", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response profile_experiences(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {
            Profile profileGet = profileService.getProfileByUserIdOrUsername(body.get("profileId"), body.get("username"));
            response.body.put("experiences", profileService.findExperienceByProfile(profileGet));
        });
    }

}
