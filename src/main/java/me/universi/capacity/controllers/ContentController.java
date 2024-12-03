package me.universi.capacity.controllers;

import java.util.List;
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
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.dto.CreateContentDTO;
import me.universi.capacity.dto.UpdateContentDTO;
import me.universi.capacity.dto.UpdateContentStatusDTO;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.service.ContentService;

@RestController
@RequestMapping("/api/capacity/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Content>> list() {
        return ResponseEntity.ok( contentService.findAll() );
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Content> get( @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id ) {
        return ResponseEntity.ok( contentService.findOrThrow( id ) );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Content> create( @Valid @RequestBody CreateContentDTO createContentDTO ) {
        return new ResponseEntity<>( contentService.create( createContentDTO ), HttpStatus.CREATED );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Content> edit(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id,
        @Valid @RequestBody UpdateContentDTO updateContentDTO
    ) {
        return ResponseEntity.ok( contentService.update( id, updateContentDTO ) );
    }

    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id ) {
        contentService.delete( id );
        return ResponseEntity.noContent().build();
    }

    @GetMapping( path = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ContentStatus> status( @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id ) {
        return ResponseEntity.ok( contentService.findStatusById( id ) );
    }

    @PatchMapping( path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ContentStatus> editStatus(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id,
        @Valid @RequestBody UpdateContentStatusDTO updateContentStatusDTO
    ) {
        return ResponseEntity.ok( contentService.setStatus( id, updateContentStatusDTO.contentStatusType() ) );
    }
}
