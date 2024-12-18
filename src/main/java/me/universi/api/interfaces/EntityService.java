package me.universi.api.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;

import jakarta.persistence.EntityNotFoundException;

public abstract class EntityService<T> {
    protected String entityName;

    public abstract Optional<T> find( UUID id );
    public final T findOrThrow( UUID id ) throws EntityNotFoundException {
        return find( id ).orElseThrow( () -> new EntityNotFoundException( entityName + " de ID '" + id + "' não existe" ) );
    }

    public abstract List<T> findAll();

    public abstract boolean hasPermissionToEdit( @NonNull T entity );
    public final void checkPermissionToEdit( @NonNull T entity ) throws AccessDeniedException {
        if ( !hasPermissionToEdit( entity ) )
            throw new AccessDeniedException( "Você não tem permissão para alterar este " + entityName );
    }

    public abstract boolean hasPermissionToDelete( @NonNull T entity );
    public final void checkPermissionToDelete( @NonNull T entity ) throws AccessDeniedException {
        if ( !hasPermissionToEdit( entity ) )
            throw new AccessDeniedException( "Você não tem permissão para deletar este " + entityName );
    }
}
