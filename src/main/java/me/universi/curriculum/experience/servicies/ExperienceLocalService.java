package me.universi.curriculum.experience.servicies;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.curriculum.experience.entities.ExperienceLocal;
import me.universi.curriculum.experience.exceptions.ExperienceException;
import me.universi.curriculum.experience.repositories.ExperienceLocalRepository;
import me.universi.user.services.UserService;

@Service
public class ExperienceLocalService {
    private final ExperienceLocalRepository experienceLocalRepository;

    public ExperienceLocalService(ExperienceLocalRepository experienceLocalRepository) {
        this.experienceLocalRepository = experienceLocalRepository;
    }

    public static ExperienceLocalService getInstance() {
        return Sys.context.getBean("experienceLocalService", ExperienceLocalService.class);
    }

    public Optional<ExperienceLocal> findById(@NotNull UUID id) {
        return experienceLocalRepository.findById(id);
    }

    public Optional<ExperienceLocal> findByName(@NotNull String name) {
        return findAll()
            .stream()
            .filter(el -> el.getName().equalsIgnoreCase(name))
            .findAny();
    }

    public List<ExperienceLocal> findAll() {
        return experienceLocalRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(ExperienceLocal::getName))
            .toList();
    }

    public ExperienceLocal create(@NotNull String name) throws ExperienceException {
        checkValidName(name);

        if (findByName(name).isPresent())
            throw new ExperienceException("Já existe um local com este nome.");

        var experienceLocal = new ExperienceLocal();
        experienceLocal.setName(name);

        return save(experienceLocal);
    }

    public ExperienceLocal edit(@NotNull UUID id, @Nullable String name) throws ExperienceException {
        // todo: proper authority check
        if (!UserService.getInstance().isUserAdminSession())
            throw new ExperienceException("Você não tem permissão para editar locais");

        var experienceLocal = findById(id).orElseThrow(() -> new ExperienceException("Local não encontrado."));

        if (name != null) {
            checkValidName(name);
            experienceLocal.setName(name);
        }

        return save(experienceLocal);
    }

    public void delete(@NotNull UUID id) throws ExperienceException {
        // todo: proper authority check
        if (!UserService.getInstance().isUserAdminSession())
            throw new ExperienceException("Você não tem permissão para editar locais");

        var experienceLocal = findById(id).orElseThrow(() -> new ExperienceException("Local não encontrado."));

        experienceLocalRepository.delete(experienceLocal);
    }

    private ExperienceLocal save(@NotNull ExperienceLocal experienceLocal) {
        return experienceLocalRepository.saveAndFlush(experienceLocal);
    }

    private Optional<String> validateName(@Nullable String name) {
        if (name == null)
            return Optional.of("O nome do local não foi informado");

        if (name.isBlank())
            return Optional.of("O nome do local não pode ser vazio.");

        return Optional.empty();
    }

    private void checkValidName(@Nullable String name) throws ExperienceException {
        var nameValidation = validateName(name);

        if (nameValidation.isPresent())
            throw new ExperienceException(nameValidation.get());
    }
}
