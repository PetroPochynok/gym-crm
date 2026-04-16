package com.epam.gym.crm.repository;

import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.storage.TrainerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepository {

    private TrainerStorage trainerStorage;

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    public Trainer save(Trainer trainer) {
        trainerStorage.getTrainers().put(trainer.getId(), trainer);
        return trainer;
    }

    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(trainerStorage.getTrainers().get(id));
    }

    public List<Trainer> findAll() {
        return trainerStorage.getTrainers().values().stream().toList();
    }

    public void delete(Long id) {
        trainerStorage.getTrainers().remove(id);
    }
}