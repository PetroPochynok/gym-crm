package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.repository.TraineeRepository;
import com.epam.gym.crm.util.CredentialGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

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

        return traineeRepository.save(trainee);
    }

    public Trainee update(Trainee trainee) {
        return traineeRepository.save(trainee);
    }

    public Optional<Trainee> getById(Long id) {
        return traineeRepository.findById(id);
    }

    public List<Trainee> getAll() {
        return traineeRepository.findAll();
    }

    public void delete(Long id) {
        traineeRepository.findById(id).map(Trainee::getUsername).ifPresent(usernameRegistryService::releaseUsername);
        traineeRepository.delete(id);
    }
}