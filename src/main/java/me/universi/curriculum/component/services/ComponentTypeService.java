package me.universi.curriculum.component.services;

import me.universi.curriculum.component.entities.ComponentType;
import me.universi.curriculum.component.repositories.ComponentTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComponentTypeService {

    private ComponentTypeRepository componentTypeRepository;

    public ComponentTypeService(ComponentTypeRepository componentTypeRepository){
        this.componentTypeRepository = componentTypeRepository;
    }

    public ComponentType save(ComponentType componentType){
        return componentTypeRepository.save(componentType);
    }

    public List<ComponentType> findAll(){
        return componentTypeRepository.findAll();
    }

    public Optional <ComponentType> findFirstById(UUID id){
        return componentTypeRepository.findFirstById(id);
    }

    public ComponentType update(ComponentType newComponentType, UUID id) throws Exception{
        return componentTypeRepository.findById(id).map(componentType -> {
            componentType.setName(newComponentType.getName());
            return componentTypeRepository.saveAndFlush(componentType);
        }).orElseGet(()->{
            try {
                return componentTypeRepository.saveAndFlush(newComponentType);
            }catch (Exception e){
                return null;
            }
        });
    }
}
