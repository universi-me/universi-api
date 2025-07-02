package me.universi.user.services;

import java.util.List;
import java.util.regex.Pattern;
import me.universi.Sys;
import me.universi.group.entities.GroupEnvironment;
import me.universi.group.services.OrganizationService;
import me.universi.role.services.RoleService;
import me.universi.user.dto.*;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.UserException;
import me.universi.util.CastingUtil;
import me.universi.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final PasswordEncoder passwordEncoder;
    private final LoginService loginService;
    private final EmailService emailService;
    private final GoogleService googleService;

    @Value("${SIGNUP_ENABLED}")
    public boolean signupEnabled;

    @Value("${SIGNUP_CONFIRMATION_ENABLED}")
    public boolean signupConfirmationEnabled;

    @Value("${RECOVERY_ENABLED}")
    public boolean recoveryEnabled;

    public AccountService(PasswordEncoder passwordEncoder, LoginService loginService, EmailService emailService, GoogleService googleService) {
        this.passwordEncoder = passwordEncoder;
        this.loginService = loginService;
        this.emailService = emailService;
        this.googleService = googleService;
    }

    // bean instance via context
    public static AccountService getInstance() {
        return Sys.context().getBean("accountService", AccountService.class);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean usernameRegex(String username) {
        return matchRegex(username, "^[a-z0-9_.-]{1,49}$");
    }

    public boolean passwordRegex(String password) {
        return matchRegex(password, "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^A-Za-zçÇ]).{8,255}$");
    }

    public boolean emailRegex(String email) {
        return matchRegex(email, "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");
    }

    public boolean matchRegex(String input, String expression) {
        try {
            return Pattern.compile(expression).matcher(input).find();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean passwordValid(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void checkPassword(User user, String rawPassword) throws UserException {
        if(!passwordValid(user, rawPassword)) {
            throw new UserException("Credenciais inválidas!");
        }
    }

    public void checkPasswordInSession(Object rawPassword) throws UserException {
        User userSession = loginService.getUserInSession();
        if(userSession.getPassword() == null || userSession.getPassword().isEmpty()) {
            return;
        }
        if(rawPassword == null || String.valueOf(rawPassword).isEmpty()) {
            throw new UserException("Requerido autenticar-se por senha!");
        }
        checkPassword(userSession, String.valueOf(rawPassword));
    }

    public void saveRawPasswordToUser(User user, String rawPassword, boolean logout) throws UserException {
        if(!passwordRegex(rawPassword)) {
            throw new UserException("Senha está com formato inválido!");
        }
        user.setPassword(encodePassword(rawPassword));
        user.setExpired_credentials(false);
        UserService.getInstance().save(user);
        if(logout) {
            loginService.logoutUser(user);
        }
    }

    public void recoveryPassword( RecoveryPasswordDTO recoveryPasswordDTO ) {

        if(!isRecoveryEnabled()) {
            throw new UserException("Recuperação de senha está desativada!");
        }

        googleService.checkRecaptchaWithToken(recoveryPasswordDTO.recaptchaToken());

        String usernameOrEmail = recoveryPasswordDTO.username();

        if(usernameOrEmail == null) {
            throw new UserException("Parametro username é nulo.");
        }

        User user = null;

        try {
            user = (User) UserService.getInstance().loadUserByUsername(usernameOrEmail);
        } catch (Exception e) {
            throw new UserException("Conta não encontrada!");
        }

        emailService.sendRecoveryPasswordEmail(user);
    }

    public void recoveryNewPassword( RecoveryNewPasswordDTO recoveryNewPasswordDTO ) {

        if(!isRecoveryEnabled()) {
            throw new UserException("Recuperação de senha está desativada!");
        }

        String token = recoveryNewPasswordDTO.token();
        String newPassword = recoveryNewPasswordDTO.newPassword();

        if(token == null) {
            throw new UserException("Parametro token é nulo.");
        }
        if(newPassword == null) {
            throw new UserException("Parametro newPassword é nulo.");
        }

        if(!passwordRegex(newPassword)) {
            throw new UserException("Nova Senha está com formato inválido!");
        }

        User user = UserService.getInstance().getUserByRecoveryPasswordToken(token);

        if(user == null) {
            throw new UserException("Token de recuperação de senha inválido ou expirado!");
        }

        user.setRecoveryPasswordToken(null);
        user.setInactive(false);
        saveRawPasswordToUser(user, newPassword, true);
    }

    public void requestConfirmAccountEmail() {

        if(!isConfirmAccountEnabled()) {
            throw new UserException("Confirmação de conta está desativada!");
        }

        User user = loginService.getUserInSession();
        if(isAccountConfirmed(user)) {
            throw new UserException("Conta já confirmada!");
        }
        emailService.sendConfirmAccountEmail(user, false);
    }

    public Boolean confirmAccountEmail(String token) throws RuntimeException {

        if(!isConfirmAccountEnabled()) {
            throw new UserException("Confirmação de conta está desativada!");
        }

        User user = token==null ? null : UserService.getInstance().getUserByRecoveryPasswordToken(token);
        if(user == null) {
            return false;
        }

        user.setRecoveryPasswordToken(null);
        user.setInactive(false);
        user.setConfirmed(true);
        UserService.getInstance().save(user);

        return true;
    }

    public GetAvailableCheckDTO availableUsernameCheck(String username) {
        boolean usernameRegex = usernameRegex(username);
        boolean usernameExist = UserService.getInstance().usernameExist(username);

        return new GetAvailableCheckDTO(
                usernameRegex && !usernameExist,
                !usernameRegex ? "Verifique o formato do nome de usuário." : usernameExist ? "Este nome de usuário está em uso." : "Usuário Disponível para uso."
        );
    }

    public GetAvailableCheckDTO availableEmailCheck(String email) {
        boolean emailRegex = emailRegex(email);
        boolean emailExist = UserService.getInstance().emailExist(email);
        boolean emailAvailableForOrganization = OrganizationService.getInstance().isEmailAvailable(email);

        return new GetAvailableCheckDTO(
                emailRegex && !emailExist && emailAvailableForOrganization,
                !emailRegex ? "Verifique o formato do email." : emailExist ? "Este email já está em uso." : !emailAvailableForOrganization ? "Email não autorizado.\nUtilize seu email corporativo." :  "Email Disponível para uso."
        );
    }

    public boolean createAccount(CreateAccountDTO createAccountDTO) throws UserException {

        // check if register is enabled
        if(!isSignupEnabled()) {
            throw new UserException("Registrar-se está desativado!");
        }

        // check recaptcha if available
        googleService.checkRecaptchaWithToken(createAccountDTO.recaptchaToken());

        String username = createAccountDTO.username();
        String email = createAccountDTO.email();

        String firstname = createAccountDTO.firstname();
        String lastname = createAccountDTO.lastname();

        String password = createAccountDTO.password();

        if (username==null || username.isEmpty()) {
            throw new UserException("Verifique o campo Usuário!");
        } else {
            username = username.trim().toLowerCase();
            if(!usernameRegex(username)) {
                throw new UserException("Nome de usuário está com formato inválido!");
            }
        }
        if (email==null || email.isEmpty()) {
            throw new UserException("Verifique o campo Email!");
        } else {
            email = email.trim().toLowerCase();
            if(!emailRegex(email)) {
                throw new UserException("Email está com formato inválido!");
            }
        }
        if (password==null || password.isEmpty()) {
            throw new UserException("Verifique o campo Senha!");
        } else {
            if(!passwordRegex(password)) {
                throw new UserException("Senha está com formato inválido!");
            }
        }

        if(UserService.getInstance().usernameExist(username)) {
            throw new UserException("Usuário \""+username+"\" já esta cadastrado!");
        }
        if(UserService.getInstance().emailExist(email)) {
            throw new UserException("Email \""+email+"\" já esta cadastrado!");
        }
        if(!OrganizationService.getInstance().isEmailAvailable(email)) {
            throw new UserException("Email \""+email+"\" não esta disponível para cadastro!");
        }

        User user = new User();
        user.setVersionDate(ConvertUtil.getDateTimeNow());
        user.setName(username);
        user.setEmail(email);
        if(isConfirmAccountEnabled()) {
            user.setInactive(true);
        }
        saveRawPasswordToUser(user, password, false);

        UserService.getInstance().createUser(user, firstname, lastname, createAccountDTO.department().orElse(null));

        if(isConfirmAccountEnabled()) {
            emailService.sendConfirmAccountEmail(user, true);
        }

        return true;
    }

    public void editAccount(UpdateAccountDTO updateAccountDTO) {
        String newPassword = updateAccountDTO.newPassword();
        if(newPassword == null || newPassword.isEmpty()) {
            throw new UserException("Parametro newPassword é nulo.");
        }

        if(!passwordRegex(newPassword)) {
            throw new UserException("Nova Senha está com formato inválido!");
        }

        String password = updateAccountDTO.password();

        User user = loginService.getUserInSession();

        if (passwordValid(user, password)) {

            saveRawPasswordToUser(user, newPassword, false);

        } else {
            throw new UserException("Credenciais Invalidas!");
        }
    }

    public void adminEditAccount(EditAccountDTO editAccountDTO) {
        if(!UserService.getInstance().isUserAdminSession()) {
            throw new UserException("Você não tem permissão para editar usuário.");
        }

        var userId = CastingUtil.getUUID( editAccountDTO.userId()).orElse( null );
        if(userId == null) {
            throw new UserException("Parametro userId é nulo.");
        }

        String username = editAccountDTO.username();
        String email = editAccountDTO.email();
        String password = editAccountDTO.password();
        String authorityLevel = editAccountDTO.authorityLevel();

        Boolean emailVerified = editAccountDTO.emailVerified();
        Boolean blockedAccount = editAccountDTO.blockedAccount();
        Boolean inactiveAccount = editAccountDTO.inactiveAccount();
        Boolean credentialsExpired = editAccountDTO.credentialsExpired();
        Boolean expiredUser = editAccountDTO.expiredUser();

        User userEdit = UserService.getInstance().find(userId).orElse( null );
        if(userEdit == null) {
            throw new UserException("Usuário não encontrado.");
        }

        String usernameOld = userEdit.getUsername();

        if(username != null && !username.isEmpty()) {
            if(UserService.getInstance().usernameExist(username) && !username.equals(usernameOld)) {
                throw new UserException("Usuário \""+username+"\" já esta cadastrado!");
            }
            if(usernameRegex(username)) {
                userEdit.setName(username);
            } else {
                throw new UserException("Nome de Usuário está com formato inválido!");
            }
        }
        if(email != null && !email.isEmpty()) {
            if(UserService.getInstance().emailExist(email) && !email.equals(userEdit.getEmail())) {
                throw new UserException("Email \""+email+"\" já esta cadastrado!");
            }
            if(emailRegex(email)) {
                userEdit.setEmail(email);
            } else {
                throw new UserException("Email está com formato inválido!");
            }
        }
        if(password != null && !password.isEmpty()) {
            saveRawPasswordToUser(userEdit, password, false);
        }

        if(authorityLevel != null && !authorityLevel.isEmpty()) {
            userEdit.setAuthority(Authority.valueOf(authorityLevel));
        }

        if(emailVerified != null) {
            userEdit.setEmail_verified(emailVerified);
        }
        if(blockedAccount != null) {
            userEdit.setBlocked_account(blockedAccount);
        }
        if(inactiveAccount != null) {
            userEdit.setInactive(inactiveAccount);
        }
        if(credentialsExpired != null) {
            userEdit.setExpired_credentials(credentialsExpired);
        }
        if(expiredUser != null) {
            userEdit.setExpired_user(expiredUser);
        }

        UserService.getInstance().save(userEdit);

        // force logout
        loginService.logoutUser(userEdit);
    }

    public List<User> adminListAccount(String byRole) {
        if(!UserService.getInstance().isUserAdminSession()) {
            throw new UserException("Você não tem permissão para listar usuários.");
        }
        return UserService.getInstance().findAllUsers(byRole);
    }

    public GetAccountDTO getAccountSession() {
        GetAccountDTO getAccount = null;
        if(loginService.userIsLoggedIn()) {
            getAccount = new GetAccountDTO(loginService.getUserInSession(), RoleService.getInstance().getAllRolesSession());
        }
        return getAccount;
    }

    // is account confirmed
    public boolean isAccountConfirmed(User user) {
        return user.isConfirmed();
    }

    public boolean isConfirmAccountEnabled() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.signup_confirm_account_enabled;
        }
        return signupConfirmationEnabled;
    }

    public boolean isSignupEnabled() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.signup_enabled;
        }
        return signupEnabled;
    }

    public boolean isRecoveryEnabled() {
        GroupEnvironment envG = OrganizationService.getInstance().getEnvironment();
        if(envG != null) {
            return envG.recovery_enabled;
        }
        return recoveryEnabled;
    }
}