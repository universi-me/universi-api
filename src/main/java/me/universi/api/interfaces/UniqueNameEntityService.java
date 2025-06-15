package me.universi.api.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import jakarta.validation.constraints.NotNull;
import me.universi.api.exceptions.UniversiConflictingOperationException;
import me.universi.api.exceptions.UniversiNoEntityException;

public abstract class UniqueNameEntityService<T> extends EntityService<T> {
    protected String fieldName = "nome";

    protected abstract Optional<T> findByNameUnchecked( String name );
    public final Optional<T> findByName( String name ) {
        return findByNameUnchecked( name ).filter( this::isValid );
    }
    public final @NotNull T findByNameOrThrow( String name ) throws UniversiNoEntityException {
        return findByName( name ).orElseThrow( () -> makeNotFoundException( fieldName, name ) );
    }

    protected abstract Optional<T> findByIdOrNameUnchecked( String idOrName );
    public final Optional<T> findByIdOrName( String idOrName ) {
        return findByIdOrNameUnchecked( idOrName ).filter( this::isValid );
    }
    public final @NotNull T findByIdOrNameOrThrow( String idOrName ) throws UniversiNoEntityException {
        return findByIdOrName( idOrName ).orElseThrow( () -> makeNotFoundException( "ID ou " + fieldName, idOrName ) );
    }

    public final List<Optional<T>> findByName( List<String> names ) {
        return new ArrayList<>( names.stream().map( this::findByName ).toList() );
    }
    public final List<T> findByNameOrThrow( List<String> names ) {
        return new ArrayList<>( names.stream().map( this::findByNameOrThrow ).toList() );
    }
    public final List<Optional<T>> findByIdOrName( List<String> idsOrNames ) {
        return new ArrayList<>( idsOrNames.stream().map( this::findByIdOrName ).toList() );
    }
    public final List<T> findByIdOrNameOrThrow( List<String> idsOrNames ) {
        return new ArrayList<>( idsOrNames.stream().map( this::findByIdOrNameOrThrow ).toList() );
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
