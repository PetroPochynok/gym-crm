package com.epam.gym.crm.repository;

import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.storage.TrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepository {

    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    public Training save(Training training) {
        trainingStorage.getTrainings().put(training.getId(), training);
        return training;
    }

    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(trainingStorage.getTrainings().get(id));
    }

    public List<Training> findAll() {
        return trainingStorage.getTrainings().values().stream().toList();
    }

    public void delete(Long id) {
        trainingStorage.getTrainings().remove(id);
    }
}