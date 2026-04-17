package com.epam.gym.crm.storage;

import com.epam.gym.crm.model.Trainee;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TraineeStorage {

    private final Map<Long, Trainee> trainees = new HashMap<>();
    private Long nextId = 1L;

    public Map<Long, Trainee> getTrainees() {
        return trainees;
    }

    public Long generateId() {
        return nextId++;
    }

    public void setNextId(Long id) {
        this.nextId = id;
    }

}
