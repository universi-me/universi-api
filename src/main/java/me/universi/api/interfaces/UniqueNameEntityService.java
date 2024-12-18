package me.universi.api.interfaces;

import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;

public abstract class UniqueNameEntityService<T> extends EntityService<T> {
    public abstract Optional<T> findByName( String name );
    public final T findByNameOrThrow( String name ) throws EntityNotFoundException {
        return findByName( name ).orElseThrow( () -> new EntityNotFoundException( entityName + " de nome '" + name + "' não existe" ) );
    }

    public abstract Optional<T> findByIdOrName( String idOrName );
    public final T findByIdOrNameOrThrow( String idOrName ) throws EntityNotFoundException {
        return findByIdOrName( idOrName ).orElseThrow( () -> new EntityNotFoundException( entityName + " de nome ou ID '" + idOrName + "' não existe" ) );
    }
}
