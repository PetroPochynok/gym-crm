package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.repository.TrainerRepository;
import com.epam.gym.crm.util.CredentialGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TrainerService {

    private TrainerRepository trainerRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public Trainer create(Trainer trainer) {
        Set<String> usernames = trainerRepository.findAll()
                .stream()
                .map(Trainer::getUsername)
                .collect(Collectors.toSet());

        String username = CredentialGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                usernames
        );

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