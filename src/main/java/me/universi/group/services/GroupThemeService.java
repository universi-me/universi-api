package me.universi.group.services;

import me.universi.group.DTO.UpdateGroupThemeDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings;
import me.universi.group.entities.GroupTheme;
import me.universi.group.repositories.GroupThemeRepository;
import me.universi.role.services.RoleService;
import org.springframework.stereotype.Service;

@Service
public class GroupThemeService {
    private final GroupService groupService;
    private final GroupThemeRepository groupThemeRepository;

    public GroupThemeService(GroupService groupService, GroupThemeRepository groupThemeRepository) {
        this.groupService = groupService;
        this.groupThemeRepository = groupThemeRepository;
    }

    public GroupTheme updateTheme(UpdateGroupThemeDTO updateGroupThemeDTO) {

        Group group = groupService.findByIdOrPathOrThrow( updateGroupThemeDTO.group() );

        RoleService.getInstance().checkIsAdmin(group);

        groupService.checkPermissionToEdit( group );

        GroupSettings groupSettings = group.getGroupSettings();
        if (groupSettings == null) {
            return null;
        }

        GroupTheme groupTheme = groupSettings.theme;
        if (groupTheme == null) {
            groupTheme = new GroupTheme();
            groupTheme.groupSettings = groupSettings;
            groupTheme = groupThemeRepository.save(groupTheme);
        }
        if (updateGroupThemeDTO.primary_color() != null) {
            groupTheme.primaryColor = updateGroupThemeDTO.primary_color().isEmpty() ? null : updateGroupThemeDTO.primary_color();
        }
        if (updateGroupThemeDTO.secondary_color() != null) {
            groupTheme.secondaryColor = updateGroupThemeDTO.secondary_color().isEmpty() ? null : updateGroupThemeDTO.secondary_color();
        }
        if (updateGroupThemeDTO.background_color() != null) {
            groupTheme.backgroundColor = updateGroupThemeDTO.background_color().isEmpty() ? null : updateGroupThemeDTO.background_color();
        }
        if (updateGroupThemeDTO.card_background_color() != null) {
            groupTheme.cardBackgroundColor = updateGroupThemeDTO.card_background_color().isEmpty() ? null : updateGroupThemeDTO.card_background_color();
        }
        if (updateGroupThemeDTO.card_item_color() != null) {
            groupTheme.cardItemColor = updateGroupThemeDTO.card_item_color().isEmpty() ? null : updateGroupThemeDTO.card_item_color();
        }
        if (updateGroupThemeDTO.font_color_v1() != null) {
            groupTheme.fontColorV1 = updateGroupThemeDTO.font_color_v1().isEmpty() ? null : updateGroupThemeDTO.font_color_v1();
        }
        if (updateGroupThemeDTO.font_color_v2() != null) {
            groupTheme.fontColorV2 = updateGroupThemeDTO.font_color_v2().isEmpty() ? null : updateGroupThemeDTO.font_color_v2();
        }
        if (updateGroupThemeDTO.font_color_v3() != null) {
            groupTheme.fontColorV3 = updateGroupThemeDTO.font_color_v3().isEmpty() ? null : updateGroupThemeDTO.font_color_v3();
        }
        if (updateGroupThemeDTO.font_color_links() != null) {
            groupTheme.fontColorLinks = updateGroupThemeDTO.font_color_links().isEmpty() ? null : updateGroupThemeDTO.font_color_links();
        }
        if (updateGroupThemeDTO.font_color_disabled() != null) {
            groupTheme.fontColorDisabled = updateGroupThemeDTO.font_color_disabled().isEmpty() ? null : updateGroupThemeDTO.font_color_disabled();
        }
        if (updateGroupThemeDTO.button_hover_color() != null) {
            groupTheme.buttonHoverColor = updateGroupThemeDTO.button_hover_color().isEmpty() ? null : updateGroupThemeDTO.button_hover_color();
        }
        if (updateGroupThemeDTO.font_color_alert() != null) {
            groupTheme.fontColorAlert = updateGroupThemeDTO.font_color_alert().isEmpty() ? null : updateGroupThemeDTO.font_color_alert();
        }
        if (updateGroupThemeDTO.font_color_success() != null) {
            groupTheme.fontColorSuccess = updateGroupThemeDTO.font_color_success().isEmpty() ? null : updateGroupThemeDTO.font_color_success();
        }
        if (updateGroupThemeDTO.wrong_invalid_color() != null) {
            groupTheme.wrongInvalidColor = updateGroupThemeDTO.wrong_invalid_color().isEmpty() ? null : updateGroupThemeDTO.wrong_invalid_color();
        }

        return groupThemeRepository.save(groupTheme);
    }
}
