package me.universi.user.services;

import jakarta.annotation.Nullable;
import java.util.*;
import me.universi.Sys;
import me.universi.api.interfaces.EntityService;
import me.universi.group.DTO.AddGroupParticipantDTO;
import me.universi.group.entities.Group;
import me.universi.group.services.GroupParticipantService;
import me.universi.group.services.OrganizationService;
import me.universi.profile.entities.Profile;
import me.universi.profile.exceptions.ProfileException;
import me.universi.profile.repositories.PerfilRepository;
import me.universi.profile.services.DepartmentService;
import me.universi.role.services.RoleService;
import me.universi.user.entities.User;
import me.universi.user.enums.Authority;
import me.universi.user.exceptions.UserException;
import me.universi.user.repositories.UserRepository;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService extends EntityService<User> implements UserDetailsService {
    private final UserRepository userRepository;
    private final PerfilRepository profileRepository;
    private final RoleHierarchyImpl roleHierarchy;
    private final LoginService loginService;

    public UserService(UserRepository userRepository, PerfilRepository profileRepository, RoleHierarchyImpl roleHierarchy, LoginService loginService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleHierarchy = roleHierarchy;
        this.loginService = loginService;
    }

    // UserService bean instance via context
    public static UserService getInstance() {
        return Sys.context.getBean("userService", UserService.class);
    }

    @Override
    public Optional<User> findUnchecked( UUID id ) {
        return userRepository.findById( id );
    }

    @Override
    public List<User> findAllUnchecked() {
        return userRepository.findAll();
    }

    public Optional<User> findByUsername( String username ) {
        var organization = OrganizationService.getInstance().getUserlessOrganization();

        return organization == null
            ? userRepository.findFirstByName( username )
            : userRepository.findFirstByNameAndOrganizationId( username, organization.getId() );
    }

    public Optional<User> findByEmail( String email ) {
        var organization = OrganizationService.getInstance().getUserlessOrganization();
        return organization == null
            ? userRepository.findFirstByEmail( email )
            : userRepository.findFirstByEmailAndOrganizationId( email, organization.getId() );
    }

    @Override
    public User loadUserByUsername( String username ) throws UsernameNotFoundException {
        return findByUsernameOrEmail( username )
            .orElseThrow( () -> new UsernameNotFoundException("Usuário não encontrado!") );
    }

    public Optional<User> findByUsernameOrEmail( String usernameOrEmail ) {
        var organization = OrganizationService.getInstance().getUserlessOrganization();
        return organization == null
            ? userRepository.findFirstByEmailOrName( usernameOrEmail )
            : userRepository.findFirstByEmailOrNameAndOrganizationId( usernameOrEmail, organization.getId() );
    }

    public void createUser(User user, String firstname, String lastname, @Nullable String departmentId) throws UserException {
        if (user==null) {
            throw new UserException("Usuario está vazio!");
        } else if (user.getUsername()==null) {
            throw new UserException("username está vazio!");
        }

        user.setAuthority(Authority.ROLE_USER);
        save(user);

        if(user.getProfile() == null) {
            Profile userProfile = new Profile();

            if(firstname != null) {
                String nameString = String.valueOf(firstname);
                if(nameString.length() > 50) {
                    throw new ProfileException("O nome não pode ter mais de 50 caracteres.");
                }
                if(!nameString.isEmpty()) {
                    userProfile.setFirstname(nameString);
                }
            }
            if(lastname != null) {
                String lastnameString = String.valueOf(lastname);
                if(lastnameString.length() > 50) {
                    throw new ProfileException("O sobrenome não pode ter mais de 50 caracteres.");
                }
                if(!lastnameString.isEmpty()) {
                    userProfile.setLastname(lastnameString);
                }
            }

            if ( departmentId != null ) {
                var department = DepartmentService.getInstance().findByIdOrNameOrThrow( departmentId );
                userProfile.setDepartment( department );
            }

            userProfile.setUser(user);
            profileRepository.saveAndFlush(userProfile);
            try {
                // add organization to user profile
                var org = OrganizationService.getInstance().getOrganization();

                GroupParticipantService.getInstance().addParticipant( new AddGroupParticipantDTO (
                    org,
                    userProfile,
                    RoleService.getInstance().getGroupMemberRole( org )
                ) );
            } catch (Exception ignored) {
            }
            user.setProfile(userProfile);
        }
    }

    public boolean usernameExist(String username) {
        return findByUsername( username ).isPresent();
    }

    public boolean emailExist(String email) {
        return findByEmail( email ).isPresent();
    }

    public void save(User user) {
        if(user.getOrganization() == null) {
            user.setOrganization(OrganizationService.getInstance().getOrganization());
        }
        userRepository.saveAndFlush(user);
    }

    // check if user has authority following springsecurity hierarchy
    public boolean userHasAuthority(User user, Authority authority, boolean equal) {
        if(equal) {
            return user.getAuthority().equals(authority);
        }
        Collection<? extends GrantedAuthority> reachableRoles = roleHierarchy.getReachableGrantedAuthorities(user.getAuthorities());
        return reachableRoles.contains(new SimpleGrantedAuthority(authority.toString()));
    }

    public boolean isUserRole(User user, Authority role, boolean equal) {
        try {
            return userHasAuthority(user, role, equal);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isUserAdmin(User userSession) {
        return isUserRole(userSession, Authority.ROLE_ADMIN, false);
    }

    public boolean isUserDev(User userSession) {
        return isUserRole(userSession, Authority.ROLE_DEV, false);
    }

    public boolean isUserAdminSession() {
        return isUserAdmin(loginService.getUserInSession());
    }

    public boolean isUserDevSession() {
        return isUserDev(loginService.getUserInSession());
    }

    public boolean userNeedAnProfile(User user, boolean checkAdmin) {
        try {
            if(checkAdmin && isUserAdmin(user)) {
                return false;
            }
            return user.getProfile() == null
                || user.getProfile().getFirstname() == null
                || user.getProfile().getLastname() == null;
        } catch (Exception e) {
            return true;
        }
    }

    public User getUserByRecoveryPasswordToken(String token) {
        return userRepository.findFirstByRecoveryPasswordToken(token).orElse(null);
    }

    public List<User> findAllUsers(Object byROLE) {
        Group organization = OrganizationService.getInstance().getOrganization();
        if(byROLE != null && !String.valueOf(byROLE).isEmpty()) {
            return organization == null ? userRepository.findAllByAuthority(Authority.valueOf(String.valueOf(byROLE))) : userRepository.findAllByAuthorityAndOrganizationId(Authority.valueOf(String.valueOf(byROLE)), organization.getId());
        }
        return organization == null ? userRepository.findAll() : userRepository.findAllByOrganizationId(organization.getId());
    }

    @Override
    public boolean hasPermissionToEdit( User user ) {
        return loginService.isSessionOfUser( user );
    }

    @Override
    public boolean hasPermissionToDelete( User user ) {
        return hasPermissionToEdit( user ) || isUserAdminSession();
    }
}
