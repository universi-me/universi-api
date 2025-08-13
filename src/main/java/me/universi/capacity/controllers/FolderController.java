package me.universi.capacity.controllers;

import java.util.*;

import me.universi.capacity.entidades.Content;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import me.universi.capacity.dto.ChangeFolderAssignmentsDTO;
import me.universi.api.config.OpenAPIConfig;
import me.universi.capacity.dto.ChangeContentPositionDTO;
import me.universi.capacity.dto.ChangeFolderContentsDTO;
import me.universi.capacity.dto.CreateFolderDTO;
import me.universi.capacity.dto.DuplicateFolderDTO;
import me.universi.capacity.dto.MoveFolderDTO;
import me.universi.capacity.dto.UpdateFolderDTO;
import me.universi.capacity.dto.WatchProfileProgressDTO;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.entidades.FolderProfile;
import me.universi.capacity.service.ContentService;
import me.universi.capacity.service.FolderService;

@RestController
@RequestMapping( "/capacity/folders" )
@SecurityRequirement( name = OpenAPIConfig.AUTHORIZATION )
@Tag(
    name = "Folder",
    description = "Folders are ordered collections of Contents, grouping multiple materials together"
)
public class FolderController {
    private final ContentService contentService;
    private final FolderService folderService;

    public FolderController(ContentService contentService, FolderService folderService) {
        this.contentService = contentService;
        this.folderService = folderService;
    }

    @Operation( summary = "Lists all Folders" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Folder>> list() {
        return ResponseEntity.ok( folderService.findAll() );
    }

    @Operation( summary = "Lists all Contents inside specified Folder" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrReference}/contents", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Content>> contentsByFolder( @Valid @PathVariable @NotBlank( message = "ID inválido" ) String idOrReference ) {
        return ResponseEntity.ok( contentService.findByFolder( folderService.findByIdOrReferenceOrThrow( idOrReference ).getId() ) );
    }

    @Operation( summary = "Fetches specified Folder" )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrReference}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Folder> get( @Valid @PathVariable @NotBlank( message = "ID inválido" ) String idOrReference ) {
        return ResponseEntity.ok( folderService.findByIdOrReferenceOrThrow( idOrReference ) );
    }

    @Operation( summary = "Creates a new empty Folder", description = "A Folder can only be created on a Group the user has Permission to create Folders." )
    @ApiResponse( responseCode = "201" )
    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Folder> create( @Valid @RequestBody CreateFolderDTO createFolderDTO ) {
        return new ResponseEntity<>( folderService.create( createFolderDTO ), HttpStatus.CREATED );
    }

    @Operation( summary = "Updates an existing Folder", description = "A Folder can only be updated by the user who created it or by a system administrator." )
    @ApiResponse( responseCode = "200" )
    @PatchMapping( path = "/{idOrReference}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Folder> edit(
        @Valid @PathVariable @NotBlank( message = "ID inválido" ) String idOrReference,
        @Valid @RequestBody UpdateFolderDTO updateFolderDTO
    ) {
        return ResponseEntity.ok( folderService.edit( idOrReference, updateFolderDTO ) );
    }

    @Operation( summary = "Deletes an existing Folder", description = "A Folder can only be deleted by the user who created it or by a system administrator." )
    @ApiResponse( responseCode = "204" )
    @DeleteMapping( path = "/{idOrReference}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference ) {
        folderService.delete( idOrReference );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Changes the Contents inside an existing Folder", description = "A Folder's Contents can only be changed by the user who created it or by a system administrator." )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( path = "/{idOrReference}/content", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> addContent(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference,
        @Valid @RequestBody ChangeFolderContentsDTO changeFolderContentsDTO
    ) {
        folderService.changeContents( idOrReference, changeFolderContentsDTO );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Reorders a Content of a Folder", description = "A Folder can only be updated by the user who created it or by a system administrator." )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( path = "/{idOrReference}/content/{contentId}", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> moveContent(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference,
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID contentId,
        @Valid @RequestBody ChangeContentPositionDTO changeContentPosition
    ) {
        folderService.changeContentPosition( idOrReference, contentId, changeContentPosition );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Assigns a Folder to a Profile or removes assignments", description = "Assignments of a Folder can only be updated by the user who created it or by a system administrator." )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( path = "/{idOrReference}/assign", consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> changeAssignments(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference,
        @Valid @RequestBody ChangeFolderAssignmentsDTO changeFolderAssignmentsDTO
    ) {
        folderService.changeAssignments( idOrReference, changeFolderAssignmentsDTO );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Lists assignments", description = "Regular users can only list assignments to themselves or assigned by themselves while system administrators have no restrictions." )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/assignments", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<FolderProfile>> getAssignments(
        @RequestParam( name = "folder", required = false ) String idOrReference,
        @RequestParam( name = "assignedBy", required = false ) @Nullable String assignedBy,
        @RequestParam( name = "assignedTo", required = false ) @Nullable String assignedTo
    ) {
        return ResponseEntity.ok( folderService.getAssignments( idOrReference, assignedBy, assignedTo ) );
    }

    @Operation( summary = "Sets a Folder as favorite for the current user" )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( path = "/{idOrReference}/favorite", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> favorite( @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference ) {
        folderService.favorite( idOrReference );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Removes a Folder from the current user's favorite Folders" )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( path = "/{idOrReference}/unfavorite", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> unfavorite( @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference ) {
        folderService.unfavorite( idOrReference );
        return ResponseEntity.noContent().build();
    }

    @Operation( summary = "Watches a user progress on a Folder", description = "Regular users can only watch their own progress or if the progress of their assignments while system administrators have no restrictions." )
    @ApiResponse( responseCode = "200" )
    @GetMapping( path = "/{idOrReference}/watch/{idOrUsername}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<WatchProfileProgressDTO>> watch(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference,
        @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrUsername
    ) {
        return ResponseEntity.ok( folderService.watch( idOrReference, idOrUsername ) );
    }

    @Operation( summary = "Creates a duplicate of the specified Folder", description = "A duplicate Folder can only be created on a Group the user has Permission to create Folders." )
    @ApiResponse( responseCode = "200" )
    @PostMapping( path = "/{idOrReference}/duplicate", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Folder> duplicate(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference,
        @Valid @RequestBody DuplicateFolderDTO duplicateFolderDTO
    ) {
        return ResponseEntity.ok( folderService.duplicate( idOrReference, duplicateFolderDTO ) );
    }

    @Operation( summary = "Moves the specified Folder to a different Group", description = "A Folder can only be moved from and to a Group the user has permission to remove and create Folders, respectively" )
    @ApiResponse( responseCode = "204" )
    @PatchMapping( path = "/{idOrReference}/move", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> moveFolder(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) String idOrReference,
        @Valid @RequestBody MoveFolderDTO moveFolderDTO
    ) {
        folderService.moveFolder( idOrReference, moveFolderDTO );
        return ResponseEntity.noContent().build();
    }
}
