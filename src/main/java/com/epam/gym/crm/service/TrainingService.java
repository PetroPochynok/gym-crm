package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrainingService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainingService.class);

    private TrainingRepository trainingRepository;

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training create(Training training) {
        Training savedTraining = trainingRepository.save(training);
        LOG.info("Training created: id={}, traineeName=id:{}, trainerName=id:{}, name={}, type={}, date={}, duration={}min",
                savedTraining.getId(), savedTraining.getTraineeId(), savedTraining.getTrainerId(),
                savedTraining.getTrainingName(), savedTraining.getTrainingType(),
                savedTraining.getTrainingDate(), savedTraining.getDuration());
        return savedTraining;
    }

    public Optional<Training> getById(Long id) {
        Optional<Training> training = trainingRepository.findById(id);
        if (training.isPresent()) {
            Training t = training.get();
            LOG.debug("Training found: id={}, traineeName=id:{}, trainerName=id:{}, name={}, type={}",
                    id, t.getTraineeId(), t.getTrainerId(), t.getTrainingName(), t.getTrainingType());
        } else {
            LOG.warn("Training not found: id={}", id);
        }
        return training;
    }

    public List<Training> getAll() {
        List<Training> trainings = trainingRepository.findAll();
        LOG.debug("Retrieved all trainings: count={}", trainings.size());
        return trainings;
    }
}