package me.universi.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import me.universi.api.config.OpenAPIConfig;
import me.universi.user.dto.*;
import me.universi.user.entities.User;
import me.universi.user.services.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "")
@Tag(
    name = "User",
    description = "Users data store more simple Profile data, mostly related to user authentication and authorization on the platform"
)
public class UserController {
    private final KeycloakService keycloakService;
    private final AccountService accountService;
    private final LoginService loginService;
    private final GoogleService googleService;

    public UserController(KeycloakService keycloakService, AccountService accountService, LoginService loginService, GoogleService googleService) {
        this.keycloakService = keycloakService;
        this.accountService = accountService;
        this.loginService = loginService;
        this.googleService = googleService;
    }

    @Operation( summary = "Fetches your own User data alongside your Role on each Group you are a participant" )
    @ApiResponse( responseCode = "200" )
    @ApiResponse( responseCode = "401", content = @Content() )
    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDTO> account() {
        GetAccountDTO getAccountDTO = accountService.getAccountSession();
        return getAccountDTO != null ? ResponseEntity.ok(getAccountDTO) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Operation( summary = "Updates your password" )
    @ApiResponse( responseCode = "204" )
    @PatchMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> account_edit( @Valid @RequestBody UpdateAccountDTO updateAccountDTO) {
        accountService.editAccount( updateAccountDTO );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Logs you out of your account" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping("/account/logout")
    public ResponseEntity<Boolean> logout() {
        return ResponseEntity.ok( loginService.logoutUserSession() );
    }

    @Operation( summary = "Registers a new User data for you on the platform", description = "Some organizations may have disabled this feature" )
    @ApiResponse( responseCode = "200" )
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> signup( @Valid @RequestBody CreateAccountDTO createAccountDTO ) {
        return ResponseEntity.ok( accountService.createAccount( createAccountDTO ) );
    }

    @Operation( summary = "Checks if specified username is currently available" )
    @ApiResponse( responseCode = "200" )
    @GetMapping(value = "/available/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAvailableCheckDTO> available_username_check( @Valid @PathVariable @NotNull( message = "username inválido" ) String username ) {
        return ResponseEntity.ok( accountService.availableUsernameCheck( username ) );
    }

    @Operation( summary = "Checks if specified email is currently available" )
    @ApiResponse( responseCode = "200" )
    @GetMapping(value = "/available/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAvailableCheckDTO> available_email_check( @Valid @PathVariable @NotNull( message = "email inválido" ) String email ) {
        return ResponseEntity.ok( accountService.availableEmailCheck( email ) );
    }

    @Operation( summary = "Updates User data for a different User", description = "Only available to system administrators" )
    @ApiResponse( responseCode = "204" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @PatchMapping(value = "/admin/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> admin_account_edit( @Valid @RequestBody EditAccountDTO editAccountDTO ) {
        accountService.adminEditAccount( editAccountDTO );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Lists all users with specified Authority level", description = "Only available to system administrators" )
    @ApiResponse( responseCode = "200" )
    @SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
    @GetMapping(value = "/admin/accounts/{accessLevel}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> admin_account_list( @Valid @PathVariable @NotNull( message = "accessLevel inválido" ) String accessLevel ) {
        return accountService.adminListAccount( accessLevel );
    }

    @Operation( summary = "Logins on the platform via Keycloak token", description = "Some organizations may have disabled this feature" )
    @ApiResponse( responseCode = "200" )
    @PostMapping(value = "/login/keycloak", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> oauth_keycloak_session( @Valid @RequestBody LoginCodeDTO loginCodeDTO ) {
        return ResponseEntity.ok( keycloakService.keycloakLogin(loginCodeDTO) );
    }

    @Operation( summary = "Redirects to Keycloak login", description = "Some organizations may have disabled this feature" )
    @ApiResponse( responseCode = "302" )
    @GetMapping(value = "/login/keycloak/auth")
    public ResponseEntity<Void> keycloak_login() {
        return ResponseEntity.status(HttpStatus.FOUND).location( keycloakService.getKeycloakLoginUrl() ).build();
    }

    @Operation( summary = "Logins on the platform via Google token", description = "Some organizations may have disabled this feature" )
    @ApiResponse( responseCode = "200" )
    @PostMapping(value = "/login/google", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponseDTO> login_google( @Valid @RequestBody LoginTokenDTO loginTokenDTO ) {
        return ResponseEntity.ok( googleService.googleLogin(loginTokenDTO) );
    }

    // recovery user password
    @Operation( summary = "Sends a password recovery email to this User", description = "Some organizations may have disabled this feature" )
    @ApiResponse( responseCode = "204" )
    @PostMapping(value = "/recovery-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> recovery_password( @Valid @RequestBody RecoveryPasswordDTO recoveryPasswordDTO ) {
        accountService.recoveryPassword( recoveryPasswordDTO );
        return ResponseEntity.noContent().build();
    }

    // create new password for user
    @Operation( summary = "Recovers account password" )
    @ApiResponse( responseCode = "204" )
    @PostMapping(value = "/new-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> recovery_new_password(@Valid @RequestBody RecoveryNewPasswordDTO recoveryNewPasswordDTO ) {
        accountService.recoveryNewPassword( recoveryNewPasswordDTO );
        return ResponseEntity.noContent().build();
    }

    // request confirm account
    @Operation( summary = "Requests an email confirmation token", description = "Some organizations may require email confirmation before allowing the first login" )
    @ApiResponse( responseCode = "204" )
    @GetMapping(value = "/confirm-account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> request_confirm_account() {
        accountService.requestConfirmAccountEmail();
        return ResponseEntity.noContent().build();
    }

    // confirm account
    @Operation( summary = "Confirms a user's email", description = "Some organizations may require email confirmation before allowing the first login" )
    @ApiResponse( responseCode = "201" )
    @GetMapping(value = "/confirm-account/{token}")
    public ResponseEntity<Boolean> confirm_account( @Parameter( description = "Email confirmation token sent through email" ) @PathVariable("token")String token) {
        return ResponseEntity.ok( accountService.confirmAccountEmail( token ) );
    }

}