package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.model.TrainingType;
import com.epam.gym.crm.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingService trainingService;

    private Training testTraining;

    @BeforeEach
    void setUp() {
        testTraining = new Training();
        testTraining.setId(1L);
        testTraining.setTraineeId(1L);
        testTraining.setTrainerId(1L);
        testTraining.setTrainingName("Morning Strength");
        testTraining.setTrainingType(TrainingType.STRENGTH);
        testTraining.setTrainingDate(LocalDate.of(2026, 1, 10));
        testTraining.setDuration(60);
    }

    @Test
    void testCreate_SuccessfullyCreatesTraining() {
        when(trainingRepository.save(any(Training.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Training result = trainingService.create(testTraining);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getTraineeId());
        assertEquals(1L, result.getTrainerId());
        assertEquals("Morning Strength", result.getTrainingName());
        assertEquals(TrainingType.STRENGTH, result.getTrainingType());
        assertEquals(60, result.getDuration());

        verify(trainingRepository, times(1)).save(any(Training.class));
    }

    @ParameterizedTest
    @EnumSource(TrainingType.class)
    void testCreate_WithDifferentTrainingTypes(TrainingType type) {
        Training newTraining = new Training();
        newTraining.setTraineeId(1L);
        newTraining.setTrainerId(1L);
        newTraining.setTrainingName("Morning Strength");
        newTraining.setTrainingType(type);
        newTraining.setTrainingDate(LocalDate.now());
        newTraining.setDuration(45);

        when(trainingRepository.save(any(Training.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Training result = trainingService.create(newTraining);

        assertEquals(type, result.getTrainingType());
    }

    @ParameterizedTest
    @ValueSource(ints = {30, 45, 60, 90, 120})
    void testCreate_WithDifferentDurations(int duration) {
        Training newTraining = new Training();
        newTraining.setTraineeId(1L);
        newTraining.setTrainerId(1L);
        newTraining.setTrainingName("Morning Strength");
        newTraining.setTrainingType(TrainingType.FITNESS);
        newTraining.setTrainingDate(LocalDate.now());
        newTraining.setDuration(duration);

        when(trainingRepository.save(any(Training.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Training result = trainingService.create(newTraining);

        assertEquals(duration, result.getDuration());
    }

    @Test
    void testGetById_ReturnsTrainingWhenExists() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(testTraining));

        Optional<Training> result = trainingService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTraining, result.get());
        verify(trainingRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_ReturnsEmptyWhenNotExists() {
        when(trainingRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Training> result = trainingService.getById(999L);

        assertFalse(result.isPresent());
        verify(trainingRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAll_ReturnsAllTrainings() {
        Training training2 = new Training();
        training2.setId(2L);
        training2.setTraineeId(2L);
        training2.setTrainerId(2L);
        training2.setTrainingName("Evening Flex");
        training2.setTrainingType(TrainingType.YOGA);
        training2.setTrainingDate(LocalDate.of(2026, 1, 11));
        training2.setDuration(45);

        List<Training> trainings = Arrays.asList(testTraining, training2);
        when(trainingRepository.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAll();

        assertEquals(2, result.size());
        assertEquals(testTraining, result.get(0));
        assertEquals(training2, result.get(1));
        verify(trainingRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_ReturnsEmptyListWhenNoTrainings() {
        when(trainingRepository.findAll()).thenReturn(List.of());

        List<Training> result = trainingService.getAll();

        assertTrue(result.isEmpty());
        verify(trainingRepository, times(1)).findAll();
    }

    @Test
    void testCreate_WithFutureDate() {
        Training newTraining = new Training();
        newTraining.setTraineeId(1L);
        newTraining.setTrainerId(1L);
        newTraining.setTrainingName("Evening Flex");
        newTraining.setTrainingType(TrainingType.FITNESS);
        newTraining.setTrainingDate(LocalDate.of(2026, 12, 31));
        newTraining.setDuration(60);

        Training savedTraining = new Training();
        savedTraining.setId(1L);
        savedTraining.setTrainingDate(LocalDate.of(2026, 12, 31));

        when(trainingRepository.save(any(Training.class))).thenReturn(savedTraining);

        Training result = trainingService.create(newTraining);

        assertEquals(LocalDate.of(2026, 12, 31), result.getTrainingDate());
    }

    @Test
    void testCreate_WithMultipleTrainingsForSameTraineesAndTrainers() {
        Training training1 = new Training();
        training1.setId(1L);
        training1.setTraineeId(1L);
        training1.setTrainerId(1L);
        training1.setTrainingName("Morning Strength");

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTraineeId(1L);
        training2.setTrainerId(1L);
        training2.setTrainingName("Evening Flex");

        when(trainingRepository.save(any(Training.class))).thenReturn(training1).thenReturn(training2);

        Training result1 = trainingService.create(training1);
        Training result2 = trainingService.create(training2);

        assertEquals(1L, result1.getId());
        assertEquals(2L, result2.getId());
        assertEquals(1L, result1.getTraineeId());
        assertEquals(1L, result2.getTraineeId());
    }

    @Test
    void testGetById_WithDifferentIds() {
        Training training1 = new Training();
        training1.setId(1L);
        training1.setTrainingName("Morning Strength");

        Training training2 = new Training();
        training2.setId(2L);
        training2.setTrainingName("Evening Flex");

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training1));
        when(trainingRepository.findById(2L)).thenReturn(Optional.of(training2));

        Optional<Training> result1 = trainingService.getById(1L);
        Optional<Training> result2 = trainingService.getById(2L);

        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());
        assertEquals("Morning Strength", result1.get().getTrainingName());
        assertEquals("Evening Flex", result2.get().getTrainingName());
    }
}

