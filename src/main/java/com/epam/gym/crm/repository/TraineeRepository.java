package com.epam.gym.crm.repository;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.storage.TraineeStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepository {

    private TraineeStorage traineeStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    public Trainee save(Trainee trainee) {
        traineeStorage.getTrainees().put(trainee.getId(), trainee);
        return trainee;
    }

    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(traineeStorage.getTrainees().get(id));
    }

    public List<Trainee> findAll() {
        return traineeStorage.getTrainees().values().stream().toList();
    }

    public void delete(Long id) {
        traineeStorage.getTrainees().remove(id);
    }
}