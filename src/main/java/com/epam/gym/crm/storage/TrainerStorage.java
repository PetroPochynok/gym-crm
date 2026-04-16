package com.epam.gym.crm.storage;

import com.epam.gym.crm.model.Trainer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainerStorage {

    private final Map<Long, Trainer> trainers = new HashMap<>();

    public Map<Long, Trainer> getTrainers() {
        return trainers;
    }
}
