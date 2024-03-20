package me.universi.group.controller;

import java.util.*;
import java.util.stream.Collectors;
import me.universi.api.entities.Response;
import me.universi.group.DTO.CompetenceFilterDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.exceptions.GroupException;
import me.universi.group.services.GroupService;
import me.universi.profile.entities.Profile;
import me.universi.roles.enums.FeaturesTypes;
import me.universi.roles.enums.Permission;
import me.universi.roles.services.RolesService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group/participant")
public class GroupParticipantController {
    private final GroupService groupService;
    private final UserService userService;

    public GroupParticipantController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    @PostMapping(value = "/enter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_enter(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(groupEdit.isRootGroup()) {
                throw new GroupException("Você não pode sair do Grupo.");
            }

            if(!groupEdit.isCanEnter()) {
                throw new GroupException("Grupo não permite entrada de participantes.");
            }

            User user = userService.getUserInSession();

            if(groupEdit.isCanEnter() || groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.addParticipantToGroup(groupEdit, user.getProfile())) {
                    response.message = "Você entrou no Grupo.";
                    return;
                } else {
                    throw new GroupException("Você já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao entrar ao grupo");

        });
    }

    @PostMapping(value = "/exit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_exit(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(groupEdit.isRootGroup()) {
                throw new GroupException("Você não pode sair do Grupo.");
            }

            User user = userService.getUserInSession();

            if(groupService.removeParticipantFromGroup(groupEdit, user.getProfile())) {
                response.message = "Você saiu do Grupo.";
            } else {
                throw new GroupException("Você não está neste Grupo.");
            }

        });
    }

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_add(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String participant = (String)body.get("participant");
            if(participant == null) {
                throw new GroupException("Parâmetro participant é nulo.");
            }

            User participantUser = null;
            if(participant != null && !participant.isEmpty()) {
                if (participant.contains("@")) {
                    participantUser = (User) userService.findFirstByEmail(participant);
                } else {
                    participantUser = (User) userService.loadUserByUsername(participant);
                }
            }

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            User user = userService.getUserInSession();

            if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.addParticipantToGroup(groupEdit, participantUser.getProfile())) {
                    response.message = "Participante adicionado com sucesso.";
                    return;
                } else {
                    throw new GroupException("Participante já esta neste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        });
    }

    @PostMapping(value = "/remove", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_remove(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            String participant = (String)body.get("participant");
            if(participant == null) {
                throw new GroupException("Parâmetro participant é nulo.");
            }

            User participantUser = null;
            if(participant != null && !participant.isEmpty()) {
                if (participant.contains("@")) {
                    participantUser = (User) userService.findFirstByEmail(participant);
                } else {
                    participantUser = (User) userService.loadUserByUsername(participant);
                }
            }

            Group groupEdit = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            User user = userService.getUserInSession();

            if(participantUser != null && groupService.verifyPermissionToEditGroup(groupEdit, user)) {
                if(groupService.removeParticipantFromGroup(groupEdit, participantUser.getProfile())) {
                    response.message = "Participante removido com sucesso.";
                    return;
                } else {
                    throw new GroupException("Participante não faz parte deste Grupo.");
                }
            }

            throw new GroupException("Falha ao adicionar participante ao grupo");

        });
    }

    @PostMapping(value = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response participant_list(@RequestBody Map<String, Object> body) {
        return Response.buildResponse(response -> {

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            if(group != null) {
                Collection<ProfileGroup> participants = group.getParticipants();

                List<Profile> profiles = participants.stream()
                        .sorted(Comparator.comparing(ProfileGroup::getJoined).reversed())
                        .map(ProfileGroup::getProfile)
                        .filter(p -> p != null && !p.isHidden())
                        .collect(Collectors.toList());

                response.body.put("participants", profiles);
                return;
            }

            throw new GroupException("Falha ao listar participante ao grupo");

        });
    }


    //Used when filtering participants based on their competences
    @PostMapping(value = "/filter", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response filterParticipants(@RequestBody CompetenceFilterDTO competenceFilter){

        return Response.buildResponse(response -> {
            response.body.put("filteredParticipants", groupService.filterProfilesWithCompetences(competenceFilter));
        });
    }

    @PostMapping("/competences")
    @ResponseBody
    public Response competences(@RequestBody Map<String, Object> body){
        return Response.buildResponse(response ->{

            String groupId = (String)body.get("groupId");
            String groupPath = (String)body.get("groupPath");

            RolesService.getInstance().checkPermission(groupId, FeaturesTypes.COMPETENCE, Permission.READ);

            Group group = groupService.getGroupByGroupIdOrGroupPath(groupId, groupPath);

            response.body.put("competences", groupService.getGroupCompetences(group));
        });

    }
}
