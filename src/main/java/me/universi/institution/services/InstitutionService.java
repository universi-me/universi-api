package me.universi.institution.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import me.universi.Sys;
import me.universi.institution.exceptions.InstitutionException;
import me.universi.institution.entities.Institution;
import me.universi.institution.repositories.InstitutionRepository;
import me.universi.user.services.UserService;

@Service
public class InstitutionService {
    private final InstitutionRepository institutionRepository;

    public InstitutionService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public static InstitutionService getInstance() {
        return Sys.context.getBean("institutionService", InstitutionService.class);
    }

    public Optional<Institution> findById(@NotNull UUID id) {
        return institutionRepository.findById(id);
    }

    public Optional<Institution> findByName(@NotNull String name) {
        return findAll()
            .stream()
            .filter(el -> el.getName().equalsIgnoreCase(name))
            .findAny();
    }

    public List<Institution> findAll() {
        return institutionRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(Institution::getName))
            .toList();
    }

    public Institution create(@NotNull String name) throws InstitutionException {
        name = name.trim();
        checkValidName(name);

        if (findByName(name).isPresent())
            throw new InstitutionException("Já existe uma instituição com este nome.");

        var institution = new Institution();
        institution.setName(name);

        return save(institution);
    }

    public Institution edit(@NotNull UUID id, @Nullable String name) throws InstitutionException {
        // todo: proper authority check
        if (!UserService.getInstance().isUserAdminSession())
            throw new InstitutionException("Você não tem permissão para editar locais");

        var institution = findById(id).orElseThrow(() -> new InstitutionException("Instituição não encontrado."));

        if (name != null) {
            checkValidName(name);
            institution.setName(name);
        }

        return save(institution);
    }

    public void delete(@NotNull UUID id) throws InstitutionException {
        // todo: proper authority check
        if (!UserService.getInstance().isUserAdminSession())
            throw new InstitutionException("Você não tem permissão para editar locais");

        var institution = findById(id).orElseThrow(() -> new InstitutionException("Instituição não encontrado."));

        institutionRepository.delete(institution);
    }

    private Institution save(@NotNull Institution institution) {
        return institutionRepository.saveAndFlush(institution);
    }

    private Optional<String> validateName(@Nullable String name) {
        if (name == null)
            return Optional.of("O nome da instituição não foi informado");

        if (name.isBlank())
            return Optional.of("O nome da instituição não pode ser vazio.");

        return Optional.empty();
    }

    private void checkValidName(@Nullable String name) throws InstitutionException {
        var nameValidation = validateName(name);

        if (nameValidation.isPresent())
            throw new InstitutionException(nameValidation.get());
    }
}
