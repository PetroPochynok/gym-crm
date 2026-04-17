package com.epam.gym.crm.facade;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.service.TraineeService;
import com.epam.gym.crm.service.TrainerService;
import com.epam.gym.crm.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // Trainee
    public Trainee createTrainee(Trainee trainee) {
        return traineeService.create(trainee);
    }

    public Optional<Trainee> getTraineeById(Long id) {
        return traineeService.getById(id);
    }

    public List<Trainee> getAllTrainees() {
        return traineeService.getAll();
    }

    public void deleteTrainee(Long id) {
        traineeService.delete(id);
    }

    // Trainer
    public Trainer createTrainer(Trainer trainer) {
        return trainerService.create(trainer);
    }

    public Optional<Trainer> getTrainerById(Long id) {
        return trainerService.getById(id);
    }

    public List<Trainer> getAllTrainers() {
        return trainerService.getAll();
    }

    // Training
    public Training createTraining(Training training) {
        return trainingService.create(training);
    }

    public Optional<Training> getTrainingById(Long id) {
        return trainingService.getById(id);
    }

    public List<Training> getAllTrainings() {
        return trainingService.getAll();
    }
}