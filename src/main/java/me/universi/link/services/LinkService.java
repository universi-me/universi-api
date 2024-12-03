package me.universi.link.services;

import me.universi.link.dto.CreateLinkDTO;
import me.universi.link.dto.UpdateLinkDTO;
import me.universi.link.entities.Link;
import me.universi.link.repositories.LinkRepository;
import me.universi.profile.entities.Profile;
import me.universi.profile.services.ProfileService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.UUID;

@Service
public class LinkService {
    private LinkRepository linkRepository;
    private ProfileService profileService;

    public LinkService( LinkRepository linkRepository, ProfileService profileService ) {
        this.linkRepository = linkRepository;
        this.profileService = profileService;
    }

    public Optional<Link> find( UUID id ) {
        return linkRepository.findFirstById(id);
    }

    public Link findOrThrow( UUID id ) throws EntityNotFoundException {
        // TODO: UniversiEntityNotFoundException
        return find( id ).orElseThrow( () -> new EntityNotFoundException( "Link de ID '" + id + "' não encontrado" ) );
    }

    public Link create( CreateLinkDTO createLinkDTO ) {
        var profile = profileService.getProfileInSession();

        var link = new Link();
        link.setProfile( profile );
        link.setUrl( createLinkDTO.url() );
        link.setName( createLinkDTO.name() );
        link.setTypeLink( createLinkDTO.type() );

        return linkRepository.saveAndFlush( link );
    }

    public void remove( UUID id ) {
        remove( id, profileService.getProfileInSession() );
    }

    public void remove( UUID linkId, Profile profile ) {
        var link = findOrThrow( linkId );
        canModifyOrThrow( link, profile );

        linkRepository.delete( link );
    }

    public Link update( UUID id, UpdateLinkDTO updateLinkDTO ) {
        var link = findOrThrow( id );
        canModifyOrThrow( link );

        if ( updateLinkDTO.name() != null )
            link.setName( updateLinkDTO.name() );

        if ( updateLinkDTO.type() != null )
            link.setTypeLink( updateLinkDTO.type() );

        if ( updateLinkDTO.url() != null )
            link.setUrl( updateLinkDTO.url() );

        return linkRepository.saveAndFlush( link );
    }

    public boolean canModify( Link link ) {
        return canModify( link, profileService.getProfileInSession() );
    }

    public boolean canModify( Link link, Profile profile ) {
        return link.getProfile().getId().equals( profile.getId() );
    }

    public void canModifyOrThrow( Link link ) {
        canModifyOrThrow( link, profileService.getProfileInSession() );
    }

    public void canModifyOrThrow( Link link, Profile profile ) throws AccessDeniedException {
        if ( !canModify( link, profile ) )
            // TODO: UniversiForbiddenAccessException
            throw new AccessDeniedException( "Não é possível modificar o Link de ID '" + link.getId() + "'" );
    }
}
