package me.universi.curriculum.component.controller;


import me.universi.curriculum.component.entities.Component;
import me.universi.curriculum.component.services.ComponentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping(value = "/api/component")
public class ComponentController {

    private ComponentService componentService;

    public ComponentController(ComponentService componentService){
        this.componentService = componentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Component createComponent(@RequestBody Component newComponent) throws Exception{
        return  componentService.save(newComponent);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Component> getAllComponent() throws Exception{
        return componentService.findAll();
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Optional<Component> getComponent(@PathVariable UUID id){
        return componentService.findFirstById(id);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Component update(@RequestBody Component newComponent, @PathVariable UUID id) throws Exception {
        return componentService.update(newComponent, id);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable UUID id){
        componentService.delete(id);
    }

}
