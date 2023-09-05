package me.universi.curriculum.component.services;

import me.universi.curriculum.component.entities.Component;
import me.universi.curriculum.component.repositories.ComponentRespository;
import me.universi.profile.services.ProfileService;
import me.universi.user.entities.User;
import me.universi.user.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComponentService {

    private ComponentRespository componentRespository;
    private ProfileService profileService;
    private UserService userService;
    private ComponentTypeService componentTypeService;

    public ComponentService(ComponentRespository componentRespository, ProfileService profileService, UserService userService, ComponentTypeService componentTypeService){
        this.componentRespository = componentRespository;
        this.profileService = profileService;
        this.userService = userService;
        this.componentTypeService = componentTypeService;
    }

    public Component save(Component component) throws Exception{
        try {
            User user = userService.getUserInSession();
            component.setProfile(user.getProfile());
            return componentRespository.saveAndFlush(component);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void delete(UUID id){ componentRespository.deleteById(id);}

    public List<Component> findAll(){
        return componentRespository.findAll();
    }

    public Optional<Component> findFirstById(UUID id){
        return componentRespository.findFirstById(id);
    }

    public Component update(Component newComponent, UUID id) throws Exception{
        return componentRespository.findById(id).map(component -> {
            component.setComponentType(newComponent.getComponentType());
            component.setDescription(newComponent.getDescription());
            component.setTitle(newComponent.getTitle());
            component.setStartDate(newComponent.getStartDate());
            component.setEndDate(newComponent.getEndDate());
            component.setPresentDate(newComponent.getPresentDate());
            return componentRespository.saveAndFlush(component);
        }).orElseGet(()->{
            try {
                User user = userService.getUserInSession();
                newComponent.setProfile(user.getProfile());
                return componentRespository.saveAndFlush(newComponent);
            }catch (Exception e){
                return null;
            }
        });
    }
}
