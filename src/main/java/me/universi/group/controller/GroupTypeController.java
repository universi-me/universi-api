package me.universi.group.controller;

import java.util.Collection;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import me.universi.group.DTO.CreateGroupTypeDTO;
import me.universi.group.DTO.UpdateGroupTypeDTO;
import me.universi.group.entities.GroupType;
import me.universi.group.services.GroupTypeService;

@RestController
@RequestMapping( "/group-types" )
public class GroupTypeController {
    private final GroupTypeService groupTypeService;

    public GroupTypeController( GroupTypeService groupTypeService ) {
        this.groupTypeService = groupTypeService;
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<GroupType> get( @PathVariable UUID id ) {
        return ResponseEntity.ok( groupTypeService.findOrThrow( id ) );
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Collection<GroupType>> list() {
        return ResponseEntity.ok( groupTypeService.findAll() );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<GroupType> create( @Valid @RequestBody CreateGroupTypeDTO dto ) {
        return ResponseEntity
            .status( HttpStatus.CREATED )
            .body( groupTypeService.create( dto ) );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<GroupType> update( @Valid @RequestBody UpdateGroupTypeDTO dto, @PathVariable String id ) {
        return ResponseEntity.ok( groupTypeService.update( id, dto ) );
    }

    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @PathVariable String id ) {
        groupTypeService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
