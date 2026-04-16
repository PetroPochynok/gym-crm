package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {

    private TrainingRepository trainingRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training create(Training training) {
        return trainingRepository.save(training);
    }

    public Optional<Training> getById(Long id) {
        return trainingRepository.findById(id);
    }

    public List<Training> getAll() {
        return trainingRepository.findAll();
    }
}