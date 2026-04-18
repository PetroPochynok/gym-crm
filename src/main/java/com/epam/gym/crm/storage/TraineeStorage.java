package com.epam.gym.crm.storage;

import com.epam.gym.crm.model.Trainee;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TraineeStorage {

    @Getter
    private final Map<Long, Trainee> trainees = new HashMap<>();
    @Setter
    private Long nextId = 1L;

    public Long generateId() {
        return nextId++;
    }

}
