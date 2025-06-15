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
import me.universi.capacity.dto.CreateCategoryDTO;
import me.universi.capacity.dto.UpdateCategoryDTO;
import me.universi.capacity.entidades.Category;
import me.universi.capacity.entidades.Content;
import me.universi.capacity.entidades.Folder;
import me.universi.capacity.service.CategoryService;
import me.universi.capacity.service.ContentService;
import me.universi.capacity.service.FolderService;

@RestController
@RequestMapping( "/capacity/categories" )
public class CategoryController {
    private final CategoryService categoryService;
    private final ContentService contentService;
    private final FolderService folderService;

    public CategoryController(CategoryService categoryService, ContentService contentService, FolderService folderService) {
        this.categoryService = categoryService;
        this.contentService = contentService;
        this.folderService = folderService;
    }

    @GetMapping( path = "", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Category>> list() {
        return ResponseEntity.ok( categoryService.findAll() );
    }

    @GetMapping( path = "/{id}/contents", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Content>> contentsByCategory( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( contentService.findByCategory( id ) );
    }

    @GetMapping( path = "/{id}/folders", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<List<Folder>> foldersByCategory( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( folderService.findByCategory( id ) );
    }

    @GetMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Category> get( @Valid @PathVariable @NotNull UUID id ) {
        return ResponseEntity.ok( categoryService.findOrThrow( id ) );
    }

    @PostMapping( path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Category> create( @Valid @RequestBody CreateCategoryDTO createCategoryDTO ) {
        return new ResponseEntity<>( categoryService.create( createCategoryDTO ), HttpStatus.CREATED );
    }

    @PatchMapping( path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Category> update(
        @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id,
        @Valid @RequestBody UpdateCategoryDTO updateCategoryDTO
    ) {
        return ResponseEntity.ok( categoryService.update( id, updateCategoryDTO ) );
    }

    @DeleteMapping( path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<Void> delete( @Valid @PathVariable @NotNull( message = "ID inválido" ) UUID id ) {
        categoryService.delete( id );
        return ResponseEntity.noContent().build();
    }
}
