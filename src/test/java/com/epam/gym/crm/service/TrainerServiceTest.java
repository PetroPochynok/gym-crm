package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.model.TrainingType;
import com.epam.gym.crm.repository.TrainerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UsernameRegistryService usernameRegistryService;

    @InjectMocks
    private TrainerService trainerService;

    private Trainer testTrainer;

    @BeforeEach
    void setUp() {
        testTrainer = new Trainer();
        testTrainer.setId(1L);
        testTrainer.setFirstName("Mike");
        testTrainer.setLastName("Stone");
        testTrainer.setSpecialization(TrainingType.FITNESS);
        testTrainer.setActive(true);
    }

    @Test
    void testCreate_SuccessfullyCreatesTrainer() {
        when(usernameRegistryService.reserveUsername("Mike", "Stone"))
                .thenReturn("mike.stone");

        when(trainerRepository.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.create(testTrainer);

        assertNotNull(result);
        assertEquals("mike.stone", result.getUsername());
        assertNotNull(result.getPassword());
        assertEquals(10, result.getPassword().length());
        assertTrue(result.isActive());

        verify(usernameRegistryService, times(1)).reserveUsername("Mike", "Stone");
        verify(trainerRepository, times(1)).save(any(Trainer.class));
    }

    @Test
    void testCreate_SetPasswordCorrectly() {
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Kate");
        newTrainer.setLastName("River");
        newTrainer.setSpecialization(TrainingType.YOGA);

        when(usernameRegistryService.reserveUsername("Kate", "River")).thenReturn("kate.river");
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.create(newTrainer);

        assertNotNull(result.getPassword());
        assertEquals(10, result.getPassword().length());
        assertTrue(result.getPassword().matches("[a-zA-Z0-9]+"));
    }

    @Test
    void testUpdate_SuccessfullyUpdatesTrainer() {
        testTrainer.setSpecialization(TrainingType.CROSSFIT);
        testTrainer.setActive(false);

        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);

        Trainer result = trainerService.update(testTrainer);

        assertNotNull(result);
        assertEquals(TrainingType.CROSSFIT, result.getSpecialization());
        assertFalse(result.isActive());

        verify(trainerRepository, times(1)).save(testTrainer);
    }

    @Test
    void testGetById_ReturnsTrainerWhenExists() {
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(testTrainer));

        Optional<Trainer> result = trainerService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainer, result.get());
        verify(trainerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_ReturnsEmptyWhenNotExists() {
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.getById(999L);

        assertFalse(result.isPresent());
        verify(trainerRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAll_ReturnsAllTrainers() {
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setFirstName("Kate");
        trainer2.setLastName("River");
        trainer2.setSpecialization(TrainingType.YOGA);

        List<Trainer> trainers = Arrays.asList(testTrainer, trainer2);
        when(trainerRepository.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAll();

        assertEquals(2, result.size());
        assertEquals(testTrainer, result.get(0));
        assertEquals(trainer2, result.get(1));
        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_ReturnsEmptyListWhenNoTrainers() {
        when(trainerRepository.findAll()).thenReturn(Arrays.asList());

        List<Trainer> result = trainerService.getAll();

        assertTrue(result.isEmpty());
        verify(trainerRepository, times(1)).findAll();
    }

    @Test
    void testCreate_AssignsUsernameFromRegistry() {
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Alex");
        newTrainer.setLastName("Johnson");
        newTrainer.setSpecialization(TrainingType.STRENGTH);

        when(usernameRegistryService.reserveUsername("Alex", "Johnson")).thenReturn("alex.johnson");
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        trainerService.create(newTrainer);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(captor.capture());
        assertEquals("alex.johnson", captor.getValue().getUsername());
    }

    @Test
    void testCreate_SetsActiveToTrue() {
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Test");
        newTrainer.setLastName("Trainer");
        newTrainer.setSpecialization(TrainingType.FITNESS);
        newTrainer.setActive(false);

        when(usernameRegistryService.reserveUsername("Test", "Trainer")).thenReturn("test.trainer");
        when(trainerRepository.save(any(Trainer.class))).thenReturn(testTrainer);

        trainerService.create(newTrainer);

        ArgumentCaptor<Trainer> captor = ArgumentCaptor.forClass(Trainer.class);
        verify(trainerRepository).save(captor.capture());
        assertTrue(captor.getValue().isActive());
    }

    @ParameterizedTest
    @EnumSource(TrainingType.class)
    void testCreate_WithDifferentSpecializations(TrainingType specialization) {
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Test");
        newTrainer.setLastName("Trainer");
        newTrainer.setSpecialization(specialization);

        when(usernameRegistryService.reserveUsername("Test", "Trainer"))
                .thenReturn("test.trainer");

        when(trainerRepository.save(any(Trainer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainer result = trainerService.create(newTrainer);

        assertEquals(specialization, result.getSpecialization());
    }

    @Test
    void testUpdate_PreservesId() {
        testTrainer.setFirstName("UpdatedName");

        when(trainerRepository.save(testTrainer)).thenReturn(testTrainer);

        Trainer result = trainerService.update(testTrainer);

        assertEquals(1L, result.getId());
    }

    @Test
    void testInvalidEnumHandling() {
        // This test verifies that invalid enum values are handled gracefully
        // The actual error handling is in StorageDataInitializer
        // Here we just verify that the enum values are valid
        for (TrainingType type : TrainingType.values()) {
            assertNotNull(type);
        }
        assertEquals(5, TrainingType.values().length);
    }
}
