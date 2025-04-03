package me.universi.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URL;
import java.util.List;
import me.universi.user.dto.*;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDTO> account() {
        GetAccountDTO getAccountDTO = userService.getAccountSession();
        return getAccountDTO != null ? ResponseEntity.ok(getAccountDTO) : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping(value = "/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> account_edit( @Valid @RequestBody UpdateAccountDTO updateAccountDTO) {
        userService.editAccount( updateAccountDTO );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/account/logout")
    public ResponseEntity<Boolean> logout() {
        return ResponseEntity.ok( userService.logoutUserSession() );
    }

    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> signup( @Valid @RequestBody CreateAccountDTO createAccountDTO ) {
        return ResponseEntity.ok( userService.createAccount( createAccountDTO ) );
    }

    @GetMapping(value = "/available/username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAvailableCheckDTO> available_username_check( @Valid @PathVariable @NotNull( message = "username inválido" ) String username ) {
        return ResponseEntity.ok( userService.availableUsernameCheck( username ) );
    }

    @GetMapping(value = "/available/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAvailableCheckDTO> available_email_check( @Valid @PathVariable @NotNull( message = "email inválido" ) String email ) {
        return ResponseEntity.ok( userService.availableEmailCheck( email ) );
    }

    @PatchMapping(value = "/admin/account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> admin_account_edit( @Valid @RequestBody EditAccountDTO editAccountDTO ) {
        userService.adminEditAccount( editAccountDTO );
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/admin/accounts/{accessLevel}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> admin_account_list( @Valid @PathVariable @NotNull( message = "accessLevel inválido" ) String accessLevel ) {
        return userService.adminListAccount( accessLevel );
    }

    @PostMapping(value = "/login/keycloak", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> oauth_keycloak_session( @Valid @RequestBody LoginCodeDTO loginCodeDTO ) {
        return ResponseEntity.ok( userService.keycloackLogin(loginCodeDTO) );
    }

    @GetMapping(value = "/login/keycloak/auth")
    public ResponseEntity<Void> keycloak_login() {
        return ResponseEntity.status(HttpStatus.FOUND).location( userService.getKeycloakLoginUrl() ).build();
    }

    @PostMapping(value = "/login/google", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> login_google( @Valid @RequestBody LoginTokenDTO loginTokenDTO ) {
        return ResponseEntity.ok( userService.googleLogin(loginTokenDTO) );
    }

    // recovery user password
    @PostMapping(value = "/recovery-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> recovery_password( @Valid @RequestBody RecoveryPasswordDTO recoveryPasswordDTO ) {
        userService.recoveryPassword( recoveryPasswordDTO );
        return ResponseEntity.noContent().build();
    }

    // create new password for user
    @PostMapping(value = "/new-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> recovery_new_password(@Valid @RequestBody RecoveryNewPasswordDTO recoveryNewPasswordDTO ) {
        userService.recoveryNewPassword( recoveryNewPasswordDTO );
        return ResponseEntity.noContent().build();
    }

    // request confirm account
    @GetMapping(value = "/confirm-account", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> request_confirm_account() {
        userService.requestConfirmAccountEmail();
        return ResponseEntity.noContent().build();
    }

    // confirm account
    @GetMapping(value = "/confirm-account/{token}")
    public ResponseEntity<Boolean> confirm_account(@PathVariable("token")String token) {
        return ResponseEntity.ok( userService.confirmAccountEmail( token ) );
    }

}