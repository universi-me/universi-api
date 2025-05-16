package me.universi.group.services;

import java.util.*;
import java.util.stream.Collectors;
import me.universi.group.DTO.CreateGroupFeatureDTO;
import me.universi.group.DTO.UpdateGroupFeatureDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings.GroupFeatures;
import me.universi.group.entities.GroupSettings.GroupSettings;
import me.universi.group.exceptions.GroupException;
import me.universi.group.repositories.GroupFeaturesRepository;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import me.universi.util.ConvertUtil;
import org.springframework.stereotype.Service;

@Service
public class GroupFeatureService {
    private final GroupFeaturesRepository groupFeaturesRepository;
    private final GroupService groupService;
    private final UserService userService;

    public GroupFeatureService(GroupFeaturesRepository groupFeaturesRepository, GroupService groupService, UserService userService) {
        this.groupFeaturesRepository = groupFeaturesRepository;
        this.groupService = groupService;
        this.userService = userService;
    }

    public GroupFeatures createFeature(CreateGroupFeatureDTO createGroupFeatureDTO) {

        Group group = groupService.findByIdOrPathOrThrow( createGroupFeatureDTO.group() );

        if(group != null) {
            groupService.checkPermissionToEdit( group );

            if (createGroupFeatureDTO.name() == null || createGroupFeatureDTO.name().isEmpty()) {
                throw new GroupException("Nome da feature está vazio.");
            }
            GroupSettings groupSettings = group.getGroupSettings();
            if (groupSettings == null) {
                return null;
            }
            if (groupFeaturesRepository.existsByGroupSettingsIdAndName(groupSettings.getId(), createGroupFeatureDTO.name())) {
                throw new GroupException("Feature já existe.");
            }
            GroupFeatures groupFeature = new GroupFeatures();
            groupFeature.groupSettings = groupSettings;
            groupFeature.name = createGroupFeatureDTO.name().trim();
            if (createGroupFeatureDTO.description() != null) {
                groupFeature.description = createGroupFeatureDTO.description();
            }
            if (createGroupFeatureDTO.enabled() != null) {
                groupFeature.enabled = createGroupFeatureDTO.enabled();
            }
            return groupFeaturesRepository.save(groupFeature);
        }
        throw new GroupException("Falha ao criar feature.");
    }

    public GroupFeatures updateFeature(UpdateGroupFeatureDTO updateGroupFeatureDTO) {

        GroupFeatures groupFeature = groupFeaturesRepository.findFirstById(updateGroupFeatureDTO.groupFeatureId()).orElseThrow( () -> new GroupException("Feature não encontrada.") );
        Group group = groupService.getGroupByGroupSettingsId(groupFeature.groupSettings.getId());

        if(group != null) {
            groupService.checkPermissionToEdit( group );

            if (updateGroupFeatureDTO.enabled() != null) {
                groupFeature.enabled = updateGroupFeatureDTO.enabled();
            }
            if (updateGroupFeatureDTO.description() != null) {
                groupFeature.description = updateGroupFeatureDTO.description();
            }
            return groupFeaturesRepository.save(groupFeature);
        }
        throw new GroupException("Falha ao editar feature.");
    }

    public List<GroupFeatures> listFeaturesByGroupId(UUID groupId) {
        Group group = groupService.findOrThrow( groupId );

        if(group != null) {

            if(!groupService.hasPermissionToEdit(group)) {
                throw new GroupException("Você não tem permissão para gerenciar este grupo.");
            }

            Collection<GroupFeatures> features = group.getGroupSettings().features;

            List<GroupFeatures> featuresList = features.stream()
                    .sorted(Comparator.comparing(me.universi.group.entities.GroupSettings.GroupFeatures::getAdded).reversed())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return featuresList;
        }

        throw new GroupException("Falha ao listar features.");
    }

    public boolean deleteFeature(UUID groupFeatureId) {
        if(groupFeatureId == null) {
            return false;
        }

        GroupFeatures groupFeature = groupFeaturesRepository.findFirstById(groupFeatureId).orElseThrow( () -> new GroupException("Feature não encontrada.") );
        Group group = groupService.getGroupByGroupSettingsId(groupFeature.groupSettings.getId());

        if(group != null) {
            groupService.checkPermissionToEdit( group );

            groupFeature.setRemoved(ConvertUtil.getDateTimeNow());
            groupFeature.setDeleted(true);
            groupFeaturesRepository.save(groupFeature);
            return true;
        }

        throw new GroupException("Falha ao deletar feature.");
    }
}
