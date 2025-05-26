package me.universi.group.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.api.exceptions.UniversiServerException;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupEnvironment;
import me.universi.group.enums.GroupType;
import me.universi.group.repositories.GroupRepository;
import me.universi.user.services.UserService;

@Service
public class OrganizationService {
    private final GroupService groupService;
    private final GroupRepository groupRepository;

    @Value("${LOCAL_ORGANIZATION_ID_ENABLED}")
    private boolean localOrganizationEnabled;

    @Value("${LOCAL_ORGANIZATION_ID}")
    private String localOrganizationNickname;

    public OrganizationService(GroupRepository groupRepository, GroupService groupService) {
        this.groupRepository = groupRepository;
        this.groupService = groupService;
    }

    public static @NotNull OrganizationService getInstance() {
        return Sys.context.getBean( "organizationService", OrganizationService.class );
    }

    public @NotNull Group getOrganization() {
        var userService = UserService.getInstance();

        if ( userService.userIsLoggedIn() ) {
            var user = userService.getUserInSession();
            if ( user != null && user.getOrganization() != null )
                return user.getOrganization();
        }

        return getUserlessOrganization();
    }

    public @NotNull Group getUserlessOrganization() {
        var nickname = useLocalOrganization()
            ? localOrganizationNickname
            : UserService.getInstance().getSubdomainFromRequest();

        return groupRepository.findFirstByParentGroupIsNullAndNicknameIgnoreCase( nickname )
            .orElseThrow( () -> new UniversiServerException( "Organização local de apelido '" + localOrganizationNickname + "' não existe." ) );
    }

    public GroupEnvironment getEnvironment() {
        return groupService.getGroupEnvironment( getOrganization() );
    }

    public boolean isEmailAvailable( String email ) {
        if ( email == null ) return false;
        var emailFilters = getOrganization().getGroupSettings().getFilterEmails();

        if ( emailFilters.isEmpty() || emailFilters.stream().allMatch( f -> !f.isEnabled() ) )
            return true;

        for ( var filter : emailFilters ) {
            if ( !filter.isEnabled() )
                continue;

            if ( filter.matches( email ) )
                return true;
        }

        return false;
    }

    public void setup() {
        if ( !useLocalOrganization() )
            return;

        var org = getUserlessOrganization();
        org.nickname = localOrganizationNickname.trim().toLowerCase();
        org.name = localOrganizationNickname.trim().toUpperCase();
        org.type = GroupType.INSTITUTION;

        groupRepository.saveAndFlush( org );
    }

    private boolean useLocalOrganization() { return localOrganizationEnabled || !UserService.getInstance().isProduction(); }
}
