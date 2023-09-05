package me.universi.curriculum.component.controller;

import me.universi.curriculum.component.entities.ComponentType;
import me.universi.curriculum.component.services.ComponentTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/componentType")
public class ComponentTypeController {

    private ComponentTypeService componentTypeService;

    public ComponentTypeController(ComponentTypeService componentTypeService){
        this.componentTypeService = componentTypeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComponentType creatComponentType(@RequestBody ComponentType componentType){
        return componentTypeService.save(componentType);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ComponentType> getAllComponentType() throws Exception{
        return componentTypeService.findAll();
    }
    @GetMapping(value = "/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ComponentType getByName(@PathVariable String name){
        return componentTypeService.findFirstByName(name);
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<ComponentType> getCurriculumType(@PathVariable UUID id){
        return componentTypeService.findFirstById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ComponentType update(@RequestBody ComponentType newComponentType, @PathVariable UUID id) throws Exception {
        return componentTypeService.update(newComponentType, id);
    }

}
