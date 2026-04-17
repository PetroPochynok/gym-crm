package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.repository.TrainerRepository;
import com.epam.gym.crm.util.CredentialGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private TrainerRepository trainerRepository;
    private UsernameRegistryService usernameRegistryService;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setUsernameRegistryService(UsernameRegistryService usernameRegistryService) {
        this.usernameRegistryService = usernameRegistryService;
    }

    public Trainer create(Trainer trainer) {
        String username = usernameRegistryService.reserveUsername(trainer.getFirstName(), trainer.getLastName());
        String password = CredentialGenerator.generatePassword();

        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);

        return trainerRepository.save(trainer);
    }

    public Trainer update(Trainer trainer) {
        return trainerRepository.save(trainer);
    }

    public Optional<Trainer> getById(Long id) {
        return trainerRepository.findById(id);
    }

    public List<Trainer> getAll() {
        return trainerRepository.findAll();
    }
}