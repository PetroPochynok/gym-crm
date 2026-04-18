package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.repository.TraineeRepository;
import com.epam.gym.crm.util.CredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

    private static final Logger LOG = LoggerFactory.getLogger(TraineeService.class);

    private TraineeRepository traineeRepository;
    private UsernameRegistryService usernameRegistryService;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setUsernameRegistryService(UsernameRegistryService usernameRegistryService) {
        this.usernameRegistryService = usernameRegistryService;
    }

    public Trainee create(Trainee trainee) {
        String username = usernameRegistryService.reserveUsername(trainee.getFirstName(), trainee.getLastName());
        String password = CredentialGenerator.generatePassword();

        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        Trainee savedTrainee = traineeRepository.save(trainee);
        LOG.info("Trainee created: id={}, username={}, firstName={}, lastName={}, dateOfBirth={}, address={}, active={}",
                savedTrainee.getId(), savedTrainee.getUsername(), savedTrainee.getFirstName(),
                savedTrainee.getLastName(), savedTrainee.getDateOfBirth(), savedTrainee.getAddress(),
                savedTrainee.isActive());

        return savedTrainee;
    }

    public Trainee update(Trainee trainee) {
        Trainee updatedTrainee = traineeRepository.save(trainee);
        LOG.info("Trainee updated: id={}, username={}, firstName={}, lastName={}, dateOfBirth={}, address={}, active={}",
                updatedTrainee.getId(), updatedTrainee.getUsername(), updatedTrainee.getFirstName(),
                updatedTrainee.getLastName(), updatedTrainee.getDateOfBirth(), updatedTrainee.getAddress(),
                updatedTrainee.isActive());
        return updatedTrainee;
    }

    public Optional<Trainee> getById(Long id) {
        Optional<Trainee> trainee = traineeRepository.findById(id);
        if (trainee.isPresent()) {
            LOG.debug("Trainee found: id={}, username={}, firstName={}, lastName={}",
                    id, trainee.get().getUsername(), trainee.get().getFirstName(), trainee.get().getLastName());
        } else {
            LOG.warn("Trainee not found: id={}", id);
        }
        return trainee;
    }

    public List<Trainee> getAll() {
        List<Trainee> trainees = traineeRepository.findAll();
        LOG.debug("Retrieved all trainees: count={}", trainees.size());
        return trainees;
    }

    public void delete(Long id) {
        Optional<Trainee> trainee = traineeRepository.findById(id);
        if (trainee.isPresent()) {
            String username = trainee.get().getUsername();
            usernameRegistryService.releaseUsername(username);
            traineeRepository.delete(id);
            LOG.info("Trainee deleted: id={}, username={}, firstName={}, lastName={}",
                    id, username, trainee.get().getFirstName(), trainee.get().getLastName());
        } else {
            LOG.warn("Attempted to delete non-existent trainee: id={}", id);
        }
    }
}