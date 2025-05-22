package me.universi.group.services;

import me.universi.group.DTO.UpdateGroupEnvironmentDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings.GroupEnvironment;
import me.universi.group.entities.GroupSettings.GroupSettings;
import me.universi.group.exceptions.GroupException;
import me.universi.group.repositories.GroupEnvironmentRepository;
import me.universi.role.services.RoleService;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class GroupEnvironmentService {

    private final GroupService groupService;
    private final UserService userService;
    private final GroupEnvironmentRepository groupEnvironmentRepository;

    public GroupEnvironmentService(GroupService groupService, UserService userService, GroupEnvironmentRepository groupEnvironmentRepository) {
        this.groupService = groupService;
        this.userService = userService;
        this.groupEnvironmentRepository = groupEnvironmentRepository;
    }

    //get organization environment
    public GroupEnvironment getOrganizationEnvironment() {
        Group group = OrganizationService.getInstance().getOrganization();

        RoleService.getInstance().checkIsAdmin(group);

        if(group != null) {
            groupService.checkPermissionToEdit( group );

            return groupService.getGroupEnvironment(group);
        }

        throw new GroupException("Falha ao listar o ambiente.");
    }

    //update organization environment
    public GroupEnvironment updateOrganizationEnvironment(UpdateGroupEnvironmentDTO updateGroupEnvironment) {
        Group group = OrganizationService.getInstance().getOrganization();

        RoleService.getInstance().checkIsAdmin(group);

        if(group != null) {
            groupService.checkPermissionToEdit( group );
            GroupEnvironment groupEnvironment = editEnvironment(group, updateGroupEnvironment);

            if(groupEnvironment != null) {
                return groupEnvironment;
            } else {
                throw new GroupException("Variáveis Ambiente não existe.");
            }
        }
        throw new GroupException("Falha ao editar Variáveis Ambiente.");
    }

    // edit group environment
    public GroupEnvironment editEnvironment(Group group, UpdateGroupEnvironmentDTO updateGroupEnvironment) {
        if(group == null) {
            return null;
        }
        if(!group.isRootGroup()) {
            throw new GroupException("Este grupo não é uma organização.");
        }
        GroupSettings groupSettings = group.getGroupSettings();
        if(groupSettings == null) {
            return null;
        }
        GroupEnvironment groupEnvironment = groupSettings.environment;
        if(groupEnvironment == null) {
            groupEnvironment = new GroupEnvironment();
            groupEnvironment.groupSettings = groupSettings;
            groupEnvironment = groupEnvironmentRepository.save(groupEnvironment);
        }
        if(updateGroupEnvironment.signup_enabled() != null) {
            groupEnvironment.signup_enabled = updateGroupEnvironment.signup_enabled();
        }
        if(updateGroupEnvironment.signup_confirm_account_enabled() != null) {
            groupEnvironment.signup_confirm_account_enabled = updateGroupEnvironment.signup_confirm_account_enabled();
        }
        if(updateGroupEnvironment.login_google_enabled() != null) {
            groupEnvironment.login_google_enabled = updateGroupEnvironment.login_google_enabled();
        }
        if(updateGroupEnvironment.google_client_id() != null) {
            groupEnvironment.google_client_id = updateGroupEnvironment.google_client_id().isEmpty() ? null : updateGroupEnvironment.google_client_id();
        }
        if(updateGroupEnvironment.recaptcha_enabled() != null) {
            groupEnvironment.recaptcha_enabled = updateGroupEnvironment.recaptcha_enabled();
        }
        if(updateGroupEnvironment.recaptcha_api_key() != null) {
            groupEnvironment.recaptcha_api_key = updateGroupEnvironment.recaptcha_api_key().isEmpty() ? null : updateGroupEnvironment.recaptcha_api_key();
        }
        if(updateGroupEnvironment.recaptcha_api_project_id() != null) {
            groupEnvironment.recaptcha_api_project_id = updateGroupEnvironment.recaptcha_api_project_id().isEmpty() ? null : updateGroupEnvironment.recaptcha_api_project_id();
        }
        if(updateGroupEnvironment.recaptcha_site_key() != null) {
            groupEnvironment.recaptcha_site_key = updateGroupEnvironment.recaptcha_site_key().isEmpty() ? null : updateGroupEnvironment.recaptcha_site_key();
        }
        if(updateGroupEnvironment.keycloak_enabled() != null) {
            groupEnvironment.keycloak_enabled = updateGroupEnvironment.keycloak_enabled();
        }
        if(updateGroupEnvironment.keycloak_client_id() != null) {
            groupEnvironment.keycloak_client_id = updateGroupEnvironment.keycloak_client_id().isEmpty() ? null : updateGroupEnvironment.keycloak_client_id();
        }
        if(updateGroupEnvironment.keycloak_client_secret() != null) {
            groupEnvironment.keycloak_client_secret = updateGroupEnvironment.keycloak_client_secret().isEmpty() ? null : updateGroupEnvironment.keycloak_client_secret();
        }
        if(updateGroupEnvironment.keycloak_realm() != null) {
            groupEnvironment.keycloak_realm = updateGroupEnvironment.keycloak_realm().isEmpty() ? null : updateGroupEnvironment.keycloak_realm();
        }
        if(updateGroupEnvironment.keycloak_url() != null) {
            groupEnvironment.keycloak_url = updateGroupEnvironment.keycloak_url().isEmpty() ? null : updateGroupEnvironment.keycloak_url();
        }
        if(updateGroupEnvironment.keycloak_redirect_url() != null) {
            groupEnvironment.keycloak_redirect_url = updateGroupEnvironment.keycloak_redirect_url().isEmpty() ? null : updateGroupEnvironment.keycloak_redirect_url();
        }

        if(updateGroupEnvironment.alert_new_content_enabled() != null) {
            groupEnvironment.alert_new_content_enabled = updateGroupEnvironment.alert_new_content_enabled();
        }
        if(updateGroupEnvironment.message_template_new_content() != null) {
            if(updateGroupEnvironment.message_template_new_content().length() > 6000) {
                throw new GroupException("O template de mensagem para novo conteúdo não pode ter mais de 6000 caracteres.");
            }
            groupEnvironment.message_template_new_content = updateGroupEnvironment.message_template_new_content().isEmpty() ? null : updateGroupEnvironment.message_template_new_content();
        }
        if(updateGroupEnvironment.alert_assigned_content_enabled() != null) {
            groupEnvironment.alert_assigned_content_enabled = updateGroupEnvironment.alert_assigned_content_enabled();
        }
        if(updateGroupEnvironment.message_template_assigned_content() != null) {
            if(updateGroupEnvironment.message_template_assigned_content().length() > 6000) {
                throw new GroupException("O template de mensagem para conteúdo atribuído não pode ter mais de 6000 caracteres.");
            }
            groupEnvironment.message_template_assigned_content = updateGroupEnvironment.message_template_assigned_content().isEmpty() ? null : updateGroupEnvironment.message_template_assigned_content();
        }

        boolean needUpdateEmailConfiguration = false;
        if(updateGroupEnvironment.email_enabled() != null && updateGroupEnvironment.email_enabled() != groupEnvironment.email_enabled ||
                updateGroupEnvironment.email_host() != null && !updateGroupEnvironment.email_host().equals(groupEnvironment.email_host) ||
                updateGroupEnvironment.email_port() != null && !updateGroupEnvironment.email_port().equals(groupEnvironment.email_port) ||
                updateGroupEnvironment.email_protocol() != null && !updateGroupEnvironment.email_protocol().equals(groupEnvironment.email_protocol) ||
                updateGroupEnvironment.email_username() != null && !updateGroupEnvironment.email_username().equals(groupEnvironment.email_username) ||
                updateGroupEnvironment.email_password() != null && !updateGroupEnvironment.email_password().equals(groupEnvironment.email_password)) {
            needUpdateEmailConfiguration = true;
        }

        if(updateGroupEnvironment.email_enabled() != null) {
            groupEnvironment.email_enabled = updateGroupEnvironment.email_enabled();
        }
        if(updateGroupEnvironment.email_host() != null) {
            groupEnvironment.email_host = updateGroupEnvironment.email_host().isEmpty() ? null : updateGroupEnvironment.email_host();
        }
        if(updateGroupEnvironment.email_port() != null) {
            groupEnvironment.email_port = updateGroupEnvironment.email_port().isEmpty() ? null : updateGroupEnvironment.email_port();
        }
        if(updateGroupEnvironment.email_protocol() != null) {
            groupEnvironment.email_protocol = updateGroupEnvironment.email_protocol().isEmpty() ? null : updateGroupEnvironment.email_protocol();
        }
        if(updateGroupEnvironment.email_username() != null) {
            groupEnvironment.email_username = updateGroupEnvironment.email_username().isEmpty() ? null : updateGroupEnvironment.email_username();
        }
        if(updateGroupEnvironment.email_password() != null) {
            groupEnvironment.email_password = updateGroupEnvironment.email_password().isEmpty() ? null : updateGroupEnvironment.email_password();
        }

        groupEnvironmentRepository.save(groupEnvironment);

        if(needUpdateEmailConfiguration) {
            userService.setupEmailSender();
        }

        if(updateGroupEnvironment.organization_name() != null) {
            Group currentOrganization = OrganizationService.getInstance().getOrganization();
            if(currentOrganization != null) {
                currentOrganization.setName(updateGroupEnvironment.organization_name());
                groupService.save(currentOrganization);
            }
        }

        if(updateGroupEnvironment.organization_nickname() != null) {
            Group currentOrganization = OrganizationService.getInstance().getOrganization();
            if(currentOrganization != null) {
                String nickname = groupService.checkNicknameAvailable(
                    updateGroupEnvironment.organization_nickname(),
                    group.getParentGroup().orElse( null )
                );

                currentOrganization.setNickname(nickname);
                groupService.save(currentOrganization);
            }
        }

        return groupEnvironment;
    }

}
