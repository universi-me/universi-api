package me.universi.api.interfaces;

import java.util.Optional;

import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiNoEntityException;

public abstract class UniqueNameEntityService<T> extends EntityService<T> {
    public abstract Optional<T> findByName( String name );
    public final T findByNameOrThrow( String name ) throws UniversiNoEntityException {
        return findByName( name ).orElseThrow( () -> makeNotFoundException( "nome", name ) );
    }

    public abstract Optional<T> findByIdOrName( String idOrName );
    public final T findByIdOrNameOrThrow( String idOrName ) throws UniversiNoEntityException {
        return findByIdOrName( idOrName ).orElseThrow( () -> makeNotFoundException( "ID ou nome", idOrName ) );
    }

    public final boolean isNameAvailable( String name ) {
        return findByName( name ).isEmpty();
    }

    public final void checkNameAvailable( String name ) throws UniversiConflictingOperationException {
        if ( !isNameAvailable( name ) )
            throw new UniversiConflictingOperationException( entityName + " de nome '" + name + "' já existe" );
    }
}
