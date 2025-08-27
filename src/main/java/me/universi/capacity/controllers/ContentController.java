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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import me.universi.api.config.OpenAPIConfig;
import me.universi.capacity.dto.CreateContentDTO;
import me.universi.capacity.dto.UpdateContentDTO;
import me.universi.capacity.dto.UpdateContentStatusDTO;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.ContentStatus;
import me.universi.capacity.service.ContentService;

@RestController
@RequestMapping( "/capacity/contents" )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "Content",
    description = "Contents store links to capacitation materials, like videos or files"
)
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @Operation( summary = "Lists all Contents" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Content>> list() {
        return ResponseEntity.ok( contentService.findAll() );
    }

    @Operation( summary = "Fetches a single Content with specified ID" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Content> get( @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id ) {
        return ResponseEntity.ok( contentService.findOrThrow( id ) );
    }

    @Operation( summary = "Creates a new Content", description = "A Content can only be created in a folder the user owns or by a system administrator." )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Content> create( @Valid @RequestBody CreateContentDTO createContentDTO ) {
        return new ResponseEntity<>( contentService.create( createContentDTO ), HttpStatus.CREATED );
    }

    @Operation( summary = "Updates an existing Content", description = "A Content can only be updated by the user who created it or by a system administrator." )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Content> edit(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id,
        @Valid @RequestBody UpdateContentDTO updateContentDTO
    ) {
        return ResponseEntity.ok( contentService.update( id, updateContentDTO ) );
    }

    @Operation( summary = "Deletes an existing Content", description = "A Content can only be deleted by the user who created it or by a system administrator." )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id ) {
        contentService.delete( id );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Gets the ContentStatus of the specified Content for the current user" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ContentStatus> status( @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id ) {
        return ResponseEntity.ok( contentService.findStatusById( id ) );
    }

    @Operation( summary = "Updates the ContentStatus of the specified Content for the current user" )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<ContentStatus> editStatus(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id,
        @Valid @RequestBody UpdateContentStatusDTO updateContentStatusDTO
    ) {
        return ResponseEntity.ok( contentService.setStatus( id, updateContentStatusDTO.contentStatusType() ) );
    }
}
