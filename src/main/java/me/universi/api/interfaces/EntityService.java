package me.universi.api.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;

import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.api.exceptions.UniversiNoEntityException;

public abstract class EntityService<T> {
    protected String entityName;

    public abstract Optional<T> find( UUID id );
    public final T findOrThrow( UUID id ) throws UniversiNoEntityException {
        return find( id ).orElseThrow( () -> makeNotFoundException( "ID", id ) );
    }

    public abstract List<T> findAll();

    public boolean hasPermissionToCreate() { return true; }
    public final void checkPermissionToCreate() throws UniversiForbiddenAccessException {
        if ( !hasPermissionToCreate() )
            throw makeDeniedException( "criar" );
    }

    public abstract boolean hasPermissionToEdit( @NonNull T entity );
    public final void checkPermissionToEdit( @NonNull T entity ) throws UniversiForbiddenAccessException {
        if ( !hasPermissionToEdit( entity ) )
            throw makeDeniedException( "alterar" );
    }

    public abstract boolean hasPermissionToDelete( @NonNull T entity );
    public final void checkPermissionToDelete( @NonNull T entity ) throws UniversiForbiddenAccessException {
        if ( !hasPermissionToEdit( entity ) )
            throw makeDeniedException( "deletar" );
    }

    public final UniversiNoEntityException makeNotFoundException( String fieldName, Object value ) {
        return new UniversiNoEntityException( entityName + " de " + fieldName + " '" + value + "' não existe" );
    }

    public final UniversiForbiddenAccessException makeDeniedException( String action ) {
        return new UniversiForbiddenAccessException( "Você não tem permissão para " + action + " este " + entityName );
    }
}
