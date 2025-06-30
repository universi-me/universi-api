package me.universi.api.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import jakarta.validation.constraints.NotNull;
import me.universi.api.exceptions.UniversiForbiddenAccessException;
import me.universi.api.exceptions.UniversiNoEntityException;

public abstract class EntityService<T> {
    protected String entityName;

    protected abstract Optional<T> findUnchecked( UUID id );
    public final Optional<T> find( UUID id ) {
        return findUnchecked( id ).filter( this::isValid );
    }
    public final @NotNull T findOrThrow( UUID id ) throws UniversiNoEntityException {
        return find( id ).orElseThrow( () -> makeNotFoundException( "ID", id ) );
    }

    public final List<Optional<T>> find( List<UUID> ids ) {
        return new ArrayList<>( ids.stream().map( this::find ).toList() );
    }
    public final List<T> findOrThrow( List<UUID> ids ) {
        return new ArrayList<>( ids.stream().map( this::findOrThrow ).toList() );
    }

    protected abstract List<T> findAllUnchecked();
    public final List<T> findAll() {
        return findAllUnchecked().stream().filter( this::isValid ).toList();
    }

    public boolean hasPermissionToCreate() { return true; }
    public void checkPermissionToCreate() throws UniversiForbiddenAccessException {
        if ( !hasPermissionToCreate() )
            throw makeDeniedException( "criar" );
    }

    public abstract boolean hasPermissionToEdit( @NotNull T entity );
    public final void checkPermissionToEdit( @NotNull T entity ) throws UniversiForbiddenAccessException {
        if ( !hasPermissionToEdit( entity ) )
            throw makeDeniedException( "alterar" );
    }

    public abstract boolean hasPermissionToDelete( @NotNull T entity );
    public final void checkPermissionToDelete( @NotNull T entity ) throws UniversiForbiddenAccessException {
        if ( !hasPermissionToEdit( entity ) )
            throw makeDeniedException( "deletar" );
    }

    public final UniversiNoEntityException makeNotFoundException( String fieldName, Object value ) {
        return new UniversiNoEntityException( entityName + " de " + fieldName + " '" + value + "' não existe" );
    }

    public final UniversiForbiddenAccessException makeDeniedException( String action ) {
        return new UniversiForbiddenAccessException( "Você não tem permissão para " + action + " este " + entityName );
    }

    public boolean isValid( T entity ) {
        return entity != null;
    }
}
