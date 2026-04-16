package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.repository.TraineeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TraineeService {

    private TraineeRepository traineeRepository;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    public Trainee create(Trainee trainee) {
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