package me.universi.group.services;

import java.util.*;

import me.universi.Sys;
import me.universi.api.exceptions.*;
import me.universi.api.interfaces.EntityService;
import me.universi.group.DTO.CreateEmailFilterDTO;
import me.universi.group.DTO.UpdateEmailFilterDTO;
import me.universi.group.entities.Group;
import me.universi.group.entities.GroupSettings.GroupEmailFilter;
import me.universi.group.enums.GroupEmailFilterType;
import me.universi.group.repositories.GroupEmailFilterRepository;
import me.universi.role.services.RoleService;
import me.universi.util.CastingUtil;
import me.universi.util.ConvertUtil;
import org.springframework.stereotype.Service;

@Service
public class GroupEmailFilterService extends EntityService<GroupEmailFilter> {

    private final GroupService groupService;
    private final GroupEmailFilterRepository groupEmailFilterRepository;

    public GroupEmailFilterService(GroupService groupService, GroupEmailFilterRepository groupEmailFilterRepository) {
        this.groupService = groupService;
        this.groupEmailFilterRepository = groupEmailFilterRepository;

        this.entityName = "Filtro de Email";
    }

    public GroupEmailFilterService getInstance() {
        return Sys.context.getBean( "groupEmailFilterService", GroupEmailFilterService.class );
    }

    @Override
    protected Optional<GroupEmailFilter> findUnchecked(UUID id) {
        return groupEmailFilterRepository.findById( id );
    }

    @Override
    protected List<GroupEmailFilter> findAllUnchecked() {
        return groupEmailFilterRepository.findAll();
    }

    // add email filter to group
    public GroupEmailFilter createEmailFilter(CreateEmailFilterDTO createEmailFilterDTO) {
        Group group = groupService.findByIdOrPathOrThrow( createEmailFilterDTO.groupId() );

        groupService.checkPermissionToEdit( group );

        GroupEmailFilter groupEmailFilter = new GroupEmailFilter();
        groupEmailFilter.groupSettings = group.groupSettings;
        groupEmailFilter.email = createEmailFilterDTO.email();
        groupEmailFilter.type = CastingUtil.getEnum( GroupEmailFilterType.class, createEmailFilterDTO.type() )
            .orElseThrow( () -> new UniversiBadRequestException( "Tipo de " + this.entityName + " de valor '" + createEmailFilterDTO.type() + "' não existe" ) );
        groupEmailFilter.enabled = createEmailFilterDTO.enabled();

        return groupEmailFilterRepository.saveAndFlush( groupEmailFilter );
    }

    public GroupEmailFilter updateEmailFilter( UpdateEmailFilterDTO dto ) {
        var filter = findOrThrow( dto.groupEmailFilterId() );
        checkPermissionToEdit( filter );

        dto.email().ifPresent( filter::setEmail );
        dto.type().ifPresent( type -> {
            filter.setType(
                CastingUtil.getEnum( GroupEmailFilterType.class , type )
                .orElseThrow( () -> new UniversiBadRequestException( "Tipo de " + this.entityName + " de valor '" + type + "' não existe" ) )
            );
        } );

        dto.enabled().ifPresent( filter::setEnabled );
        return groupEmailFilterRepository.saveAndFlush( filter );
    }

    public void deleteEmailFilter(UUID groupEmailFilterId) {
        var filter = findOrThrow( groupEmailFilterId );
        checkPermissionToDelete( filter );

        filter.setRemoved(ConvertUtil.getDateTimeNow());
        filter.setDeleted(true);
        groupEmailFilterRepository.saveAndFlush( filter );
    }

    public List<GroupEmailFilter> listGroupEmailFilters( UUID groupId ) {
        Group group = groupService.findOrThrow( groupId );
        RoleService.getInstance().checkIsAdmin(group);

        if( !groupService.hasPermissionToEdit(group) )
            throw new UniversiForbiddenAccessException("Você não tem permissão para gerenciar este grupo.");

        return group.getGroupSettings().getFilterEmails().stream()
            .sorted( Comparator.comparing( GroupEmailFilter::getAdded ).reversed() )
            .filter( this::isValid )
            .toList();
    }

    @Override
    public boolean hasPermissionToEdit( GroupEmailFilter groupEmailFilter ) {
        return groupService.hasPermissionToEdit( groupEmailFilter.getGroup() );
    }

    @Override
    public boolean hasPermissionToDelete( GroupEmailFilter groupEmailFilter ) {
        return groupService.hasPermissionToEdit( groupEmailFilter.getGroup() );
    }
}
