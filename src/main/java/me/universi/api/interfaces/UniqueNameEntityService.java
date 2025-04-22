package me.universi.api.interfaces;

import java.util.Optional;
import java.util.function.Predicate;

import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiNoEntityException;

public abstract class UniqueNameEntityService<T> extends EntityService<T> {
    protected String fieldName = "nome";

    public abstract Optional<T> findByName( String name );
    public final T findByNameOrThrow( String name ) throws UniversiNoEntityException {
        return findByName( name ).orElseThrow( () -> makeNotFoundException( fieldName, name ) );
    }

    public abstract Optional<T> findByIdOrName( String idOrName );
    public final T findByIdOrNameOrThrow( String idOrName ) throws UniversiNoEntityException {
        return findByIdOrName( idOrName ).orElseThrow( () -> makeNotFoundException( "ID ou " + fieldName, idOrName ) );
    }

    public final boolean isNameAvailable( String name ) {
        return findByName( name ).isEmpty();
    }

    public final void checkNameAvailable( String name ) throws UniversiConflictingOperationException {
        if ( !isNameAvailable( name ) )
            throw new UniversiConflictingOperationException( entityName + " de " + fieldName + " '" + name + "' já existe" );
    }

    public final void checkNameAvailableIgnoreIf( String name, Predicate<T> ignoreIf ) throws UniversiConflictingOperationException {
        var entity = findByName( name );
        if ( entity.isPresent() && ( ignoreIf == null || !ignoreIf.test( entity.get() ) ) )
            throw new UniversiConflictingOperationException( entityName + " de " + fieldName + " '" + name + "' já existe" );
    }
}
