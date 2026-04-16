package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.repository.TraineeRepository;
import com.epam.gym.crm.util.CredentialGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TraineeService {

    private TraineeRepository traineeRepository;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    public Trainee create(Trainee trainee) {
        Set<String> existingUsernames = traineeRepository.findAll()
                .stream()
                .map(Trainee::getUsername)
                .collect(Collectors.toSet());

        String username = CredentialGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                existingUsernames
        );

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
        traineeRepository.delete(id);
    }
}