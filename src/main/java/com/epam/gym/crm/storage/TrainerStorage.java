package com.epam.gym.crm.storage;

import com.epam.gym.crm.model.Trainer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TrainerStorage {

    @Getter
    private final Map<Long, Trainer> trainers = new HashMap<>();
    @Setter
    private Long nextId = 1L;

    public Long generateId() {
        return nextId++;
    }

}
