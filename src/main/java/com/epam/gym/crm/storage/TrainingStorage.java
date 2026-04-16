package com.epam.gym.crm.storage;

import com.epam.gym.crm.model.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainingStorage {

    private final Map<Long, Training> trainings = new HashMap<>();

    public Map<Long, Training> getTrainings() {
        return trainings;
    }

}
