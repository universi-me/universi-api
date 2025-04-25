package me.universi.group.DTO;

import jakarta.validation.constraints.NotNull;

public record UpdateGroupEnvironmentDTO(
        Boolean signup_enabled,
        Boolean signup_confirm_account_enabled,
        Boolean login_google_enabled,
        String google_client_id,
        Boolean recaptcha_enabled,
        String recaptcha_api_key,
        String recaptcha_api_project_id,
        String recaptcha_site_key,
        Boolean keycloak_enabled,
        String keycloak_client_id,
        String keycloak_client_secret,
        String keycloak_realm,
        String keycloak_url,
        String keycloak_redirect_url,
        Boolean alert_new_content_enabled,
        String message_template_new_content,
        Boolean alert_assigned_content_enabled,
        String message_template_assigned_content,
        Boolean email_enabled,
        String email_host,
        String email_port,
        String email_protocol,
        String email_username,
        String email_password,
        String organization_name,
        String organization_nickname
) { }
