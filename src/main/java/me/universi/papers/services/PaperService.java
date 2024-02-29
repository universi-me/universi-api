package me.universi.papers.services;

import java.util.*;
import me.universi.Sys;
import me.universi.group.entities.Group;
import me.universi.group.entities.ProfileGroup;
import me.universi.group.services.GroupService;
import me.universi.papers.entities.PaperProfile;
import me.universi.papers.enums.FeaturesTypes;
import me.universi.papers.enums.Permission;
import me.universi.papers.repositories.PaperFeatureRepository;
import me.universi.profile.entities.Profile;
import me.universi.papers.entities.PaperFeature;
import me.universi.papers.exceptions.PaperException;
import me.universi.papers.repositories.PaperProfileRepository;
import me.universi.papers.repositories.PaperRepository;
import me.universi.profile.services.ProfileService;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import me.universi.papers.entities.Paper;

@Service
public class PaperService {
    private final UserService userService;
    private final ProfileService profileService;
    private final GroupService groupService;
    private final PaperRepository paperRepository;
    private final PaperProfileRepository paperProfileRepository;
    private final PaperFeatureRepository paperFeatureRepository;

    private Paper defaultPaper;
    private PaperProfile defaultPaperProfile;
    private PaperFeature defaultPaperFeature;

    public PaperService(UserService userService, ProfileService profileService, GroupService groupService, PaperRepository paperRepository, PaperProfileRepository paperProfileRepository, PaperFeatureRepository paperFeatureRepository) {
        this.userService = userService;
        this.profileService = profileService;
        this.groupService = groupService;
        this.paperRepository = paperRepository;
        this.paperProfileRepository = paperProfileRepository;
        this.paperFeatureRepository = paperFeatureRepository;

        defaultPaper = new Paper();
        defaultPaper.name = "USER";

        defaultPaperProfile = new PaperProfile();
        defaultPaperProfile.paper = defaultPaper;

        defaultPaperFeature = new PaperFeature();
        defaultPaperFeature.paper = defaultPaper;
        defaultPaperFeature.permission = Permission.DEFAULT;

    }

    public static PaperService getInstance() {
        return Sys.context.getBean("paperService", PaperService.class);
    }

    public Paper savePaper(Paper paper) {
        return paperRepository.save(paper);
    }

    public Paper createPaper(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new PaperException("Usuário não possui permissão para criar um papel de usuário.");
        }

        Object name = body.get("name");
        Object description = body.get("description");
        Object groupId = body.get("groupId");

        if (name == null) {
            throw new PaperException("Parâmetro name é nulo.");
        }

        if(groupId == null) {
            throw new PaperException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

        Paper paper = new Paper();
        paper.name = name.toString();
        paper.description = description != null ? description.toString() : null;
        paper.group = group;

        return savePaper(paper);
    }

    public Paper editPaper(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new PaperException("Usuário não possui permissão para editar um papel de usuário.");
        }

        Object paperId = body.get("paperId");
        Object name = body.get("name");
        Object description = body.get("description");

        if (paperId == null) {
            throw new PaperException("Parâmetro paperId é nulo.");
        }

        Paper paper = paperRepository.findFirstById(UUID.fromString(paperId.toString())).orElse(null);
        if(paper == null) {
            throw new PaperException("Papel não encontrado.");
        }

        if (name != null) {
            paper.name = name.toString();
        }
        if (description != null) {
            paper.description = description.toString();
        }

        return savePaper(paper);
    }

    public boolean assignPaper(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new PaperException("Usuário não possui permissão para atribuir um papel de usuário.");
        }

        Object paperId = body.get("paperId");
        Object profileId = body.get("profileId");

        if (paperId == null) {
            throw new PaperException("Parâmetro paperId é nulo.");
        }
        if (profileId == null) {
            throw new PaperException("Parâmetro profileId é nulo.");
        }

        Paper paper = paperRepository.findFirstById(UUID.fromString(paperId.toString())).orElse(null);
        if(paper == null) {
            throw new PaperException("Papel não encontrado.");
        }
        Profile profile = profileService.findFirstById(UUID.fromString(profileId.toString()));
        if(profile == null) {
            throw new PaperException("Perfil não encontrado.");
        }

        PaperProfile paperProfile = paperProfileRepository.findFirstByProfileAndGroup(profile, paper.group).orElse(null);
        if(paperProfile == null) {
            paperProfile = new PaperProfile();
        }
        paperProfile.paper = paper;
        paperProfile.group = paper.group;
        paperProfile.profile = profile;

        paperProfileRepository.save(paperProfile);
        return true;
    }

    public PaperFeature setValuePaperFeature(Map<String, Object> body) {

        if(!userService.isUserAdminSession()) {
            throw new PaperException("Usuário não possui permissão para alterar status de uma feature.");
        }

        Object paperId = body.get("paperId");
        Object featureString = body.get("feature");

        Object value = body.get("value");

        if (value == null) {
            throw new PaperException("Parâmetro value é nulo.");
        }

        PaperFeature paperFeature = null;

        if(paperFeature == null && paperId != null && featureString != null) {
            Paper paper = paperRepository.findFirstById(UUID.fromString(paperId.toString())).orElse(null);
            if(paper == null) {
                throw new PaperException("Papel de usuário não encontrado.");
            }
            FeaturesTypes feature = FeaturesTypes.valueOf(featureString.toString());
            if(feature == null) {
                throw new PaperException("Funcionalidade não encontrado.");
            }
            paperFeature = paperFeatureRepository.findFirstByPaperAndFeatureType(paper, feature).orElse(null);
            if(paperFeature == null) {
                paperFeature = new PaperFeature();
                paperFeature.featureType = feature;
                paperFeature.paper = paper;
            }
        }
        if(paperFeature == null) {
            throw new PaperException("Feature não encontrada.");
        }

        paperFeature.permission = Integer.parseInt(value.toString());

        return paperFeatureRepository.save(paperFeature);
    }

    public Collection<Paper> listPaper(Map<String, Object> body) {
        if(!userService.isUserAdminSession()) {
            throw new PaperException("Você não possui permissão para listar papéis.");
        }

        Object groupId = body.get("groupId");

        if(groupId == null) {
            throw new PaperException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);

        return paperRepository.findAllByGroup(group);
    }

    public void checkPermission(Profile profile, Group group, FeaturesTypes feature, int forPermission) {
        if (profile == null) {
            throw new PaperException("Perfil não encontrado.");
        }
        if (group == null) {
            throw new PaperException("Grupo não encontrado.");
        }
        if (feature == null) {
            throw new PaperException("Funcionalidade não encontrada.");
        }

        PaperProfile paperProfile = paperProfileRepository.findFirstByProfileAndGroup(profile, group).orElse(null);

        if (paperProfile != null) {

            Paper paper = paperProfile.paper;
            if (paper != null) {
                PaperFeature paperFeature = paperFeatureRepository.findFirstByPaperAndFeatureType(paper, feature).orElse(defaultPaperFeature);

                if (paperFeature != null) {

                    if (paperFeature.permission < forPermission) {
                        throw new PaperException("Você não possui permissão para acessar a funcionalidade \""+ feature.label +"\".");
                    }

                } else {
                    //throw new PaperException("Funcionalidade não encontrada.");
                }

            } else {
                //throw new PaperException("Papel não encontrado.");
            }

        } else {
            //throw new PaperException("Perfil não possui papel no grupo.");
        }
    }

    public void checkPermission(Group group, FeaturesTypes feature, int forPermission) {
        checkPermission(
                UserService.getInstance().getUserInSession().getProfile(),
                group,
                feature,
                forPermission
        );
    }

    public void checkPermission(String groupId, FeaturesTypes feature, int forPermission) {
        checkPermission(
                GroupService.getInstance().getGroupByGroupIdOrGroupPath(groupId, null),
                feature,
                forPermission
        );
    }

    public Collection<Profile> listPaperProfile(Map<String, Object> body) {
        Object groupId = body.get("groupId");

        if(groupId == null) {
            throw new PaperException("Parâmetro groupId é nulo.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);


        Collection<ProfileGroup> participants = group.participants;
        Collection<PaperProfile> papers = paperProfileRepository.findAllByGroup(group);

        Collection<Profile> profiles = new ArrayList<>();

        for(ProfileGroup profileGroup : participants) {
            Profile profile = profileGroup.profile;
            for(PaperProfile paperProfile : papers) {
                if(paperProfile.profile.equals(profile)) {
                    profile.paper = paperProfile.paper;
                    break;
                }
            }
            profiles.add(profileGroup.profile);
        }

        return profiles;
    }

    public Paper getAssignedPaper(Map<String, Object> body) {
        Object profileId = body.get("profileId");
        Object groupId = body.get("groupId");

        if(profileId == null) {
            throw new PaperException("Parâmetro profileId é nulo.");
        }
        if(groupId == null) {
            throw new PaperException("Parâmetro groupId é nulo.");
        }
        Profile profile = profileService.findFirstById(UUID.fromString(profileId.toString()));
        if(profile == null) {
            throw new PaperException("Perfil não encontrado.");
        }
        Group group = groupService.getGroupByGroupIdOrGroupPath(groupId.toString(), null);
        if(group == null) {
            throw new PaperException("Grupo não encontrado.");
        }

        PaperProfile paperProfile = paperProfileRepository.findFirstByProfileAndGroup(profile, group).orElse(defaultPaperProfile);

        return paperProfile.paper;
    }
}
