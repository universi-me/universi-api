package me.universi.api.interfaces;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

public abstract class UniqueNameEntityService<T> extends EntityService<T> {
    public abstract Optional<T> findByName( String name );
    public final T findByNameOrThrow( String name ) throws EntityNotFoundException {
        return findByName( name ).orElseThrow( () -> makeNotFoundException( "nome", name ) );
    }

    public abstract Optional<T> findByIdOrName( String idOrName );
    public final T findByIdOrNameOrThrow( String idOrName ) throws EntityNotFoundException {
        return findByIdOrName( idOrName ).orElseThrow( () -> makeNotFoundException( "ID ou nome", idOrName ) );
    }

    public final boolean isNameAvailable( String name ) {
        return findByName( name ).isEmpty();
    }

    public final void checkNameAvailable( String name ) throws IllegalStateException {
        if ( !isNameAvailable( name ) )
            throw new IllegalStateException( entityName + " de nome '" + name + "' já existe" );
    }
}
