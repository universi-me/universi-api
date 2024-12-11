package me.universi.link.controller;

import me.universi.link.dto.CreateLinkDTO;
import me.universi.link.dto.UpdateLinkDTO;
import me.universi.link.entities.Link;
import me.universi.link.services.LinkService;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping( "/api/links" )
public class LinkController {
    private LinkService linkService;

    public LinkController( LinkService linkService ) {
        this.linkService = linkService;
    }

    @PostMapping( value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Link> create( @Valid @RequestBody CreateLinkDTO createLinkDTO ) {
        return new ResponseEntity<>( linkService.create( createLinkDTO ), HttpStatus.CREATED );
    }

    @DeleteMapping( value= "/{id}" )
    public ResponseEntity<Void> remove(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id
    ) {
        linkService.remove( id );
        return ResponseEntity.noContent().build();
    }

    @PatchMapping( value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Link> update(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id,
        @Valid @RequestBody UpdateLinkDTO updateLinkDTO
    ) {
        return ResponseEntity.ok( linkService.update( id, updateLinkDTO ) );
    }

    @GetMapping( value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Link> get(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id
    ) {
        return ResponseEntity.ok( linkService.findOrThrow( id ) );
    }
}
