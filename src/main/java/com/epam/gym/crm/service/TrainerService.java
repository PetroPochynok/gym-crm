package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.repository.TrainerRepository;
import com.epam.gym.crm.util.CredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainerService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainerService.class);

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

        Trainer savedTrainer = trainerRepository.save(trainer);
        LOG.info("Trainer created: id={}, username={}, firstName={}, lastName={}, specialization={}, active={}",
                savedTrainer.getId(), savedTrainer.getUsername(), savedTrainer.getFirstName(),
                savedTrainer.getLastName(), savedTrainer.getSpecialization(), savedTrainer.isActive());

        return savedTrainer;
    }

    public Trainer update(Trainer trainer) {
        Trainer updatedTrainer = trainerRepository.save(trainer);
        LOG.info("Trainer updated: id={}, username={}, firstName={}, lastName={}, specialization={}, active={}",
                updatedTrainer.getId(), updatedTrainer.getUsername(), updatedTrainer.getFirstName(),
                updatedTrainer.getLastName(), updatedTrainer.getSpecialization(), updatedTrainer.isActive());
        return updatedTrainer;
    }

    public Optional<Trainer> getById(Long id) {
        Optional<Trainer> trainer = trainerRepository.findById(id);
        if (trainer.isPresent()) {
            LOG.debug("Trainer found: id={}, username={}, firstName={}, lastName={}, specialization={}",
                    id, trainer.get().getUsername(), trainer.get().getFirstName(),
                    trainer.get().getLastName(), trainer.get().getSpecialization());
        } else {
            LOG.warn("Trainer not found: id={}", id);
        }
        return trainer;
    }

    public List<Trainer> getAll() {
        List<Trainer> trainers = trainerRepository.findAll();
        LOG.debug("Retrieved all trainers: count={}", trainers.size());
        return trainers;
    }
}