package com.epam.gym.crm.service;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.repository.TraineeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UsernameRegistryService usernameRegistryService;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee testTrainee;

    @BeforeEach
    void setUp() {
        testTrainee = new Trainee();
        testTrainee.setId(1L);
        testTrainee.setFirstName("John");
        testTrainee.setLastName("Doe");
        testTrainee.setDateOfBirth(LocalDate.of(1990, 1, 15));
        testTrainee.setAddress("New York");
        testTrainee.setActive(true);
    }

    @Test
    void testCreate_SuccessfullyCreatesTrainee() {
        when(usernameRegistryService.reserveUsername("John", "Doe"))
                .thenReturn("john.doe");

        when(traineeRepository.save(any(Trainee.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.create(testTrainee);

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertNotNull(result.getPassword());
        assertEquals(10, result.getPassword().length());
        assertTrue(result.isActive());

        verify(usernameRegistryService, times(1)).reserveUsername("John", "Doe");
        verify(traineeRepository, times(1)).save(any(Trainee.class));
    }

    @Test
    void testCreate_SetsPasswordCorrectly() {
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("Jane");
        newTrainee.setLastName("Smith");

        when(usernameRegistryService.reserveUsername("Jane", "Smith")).thenReturn("jane.smith");
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.create(newTrainee);

        assertNotNull(result.getPassword());
        assertEquals(10, result.getPassword().length());
        assertTrue(result.getPassword().matches("[a-zA-Z0-9]+"));
    }

    @Test
    void testUpdate_SuccessfullyUpdatesTrainee() {
        testTrainee.setFirstName("Updated");
        testTrainee.setAddress("Updated Address");

        when(traineeRepository.save(testTrainee)).thenReturn(testTrainee);

        Trainee result = traineeService.update(testTrainee);

        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Updated Address", result.getAddress());

        verify(traineeRepository, times(1)).save(testTrainee);
    }

    @Test
    void testGetById_ReturnsTraineeWhenExists() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));

        Optional<Trainee> result = traineeService.getById(1L);

        assertTrue(result.isPresent());
        assertEquals(testTrainee, result.get());
        verify(traineeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_ReturnsEmptyWhenNotExists() {
        when(traineeRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.getById(999L);

        assertFalse(result.isPresent());
        verify(traineeRepository, times(1)).findById(999L);
    }

    @Test
    void testGetAll_ReturnsAllTrainees() {
        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        trainee2.setFirstName("Jane");
        trainee2.setLastName("Smith");

        List<Trainee> trainees = Arrays.asList(testTrainee, trainee2);
        when(traineeRepository.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAll();

        assertEquals(2, result.size());
        assertEquals(testTrainee, result.get(0));
        assertEquals(trainee2, result.get(1));
        verify(traineeRepository, times(1)).findAll();
    }

    @Test
    void testGetAll_ReturnsEmptyListWhenNoTrainees() {
        when(traineeRepository.findAll()).thenReturn(List.of());

        List<Trainee> result = traineeService.getAll();

        assertTrue(result.isEmpty());
        verify(traineeRepository, times(1)).findAll();
    }

    @Test
    void testDelete_SuccessfullyDeletesTrainee() {
        testTrainee.setUsername("john.doe");
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));

        traineeService.delete(1L);

        verify(traineeRepository, times(1)).findById(1L);
        verify(usernameRegistryService, times(1)).releaseUsername("john.doe");
        verify(traineeRepository, times(1)).delete(1L);
    }

    @Test
    void testDelete_DoesNothingWhenTraineeNotExists() {
        when(traineeRepository.findById(999L)).thenReturn(Optional.empty());

        traineeService.delete(999L);

        verify(traineeRepository, times(1)).findById(999L);
        verify(usernameRegistryService, never()).releaseUsername(any());
        verify(traineeRepository, never()).delete(any());
    }

    @Test
    void testDelete_ReleasesUsernameBeforeDeletion() {
        testTrainee.setUsername("john.doe");
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(testTrainee));

        traineeService.delete(1L);

        // Verify that releaseUsername was called with correct username
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(usernameRegistryService).releaseUsername(captor.capture());
        assertEquals("john.doe", captor.getValue());
    }

    @Test
    void testCreate_AssignsUsernameFromRegistry() {
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("Mary");
        newTrainee.setLastName("Dou");

        when(usernameRegistryService.reserveUsername("Mary", "Dou")).thenReturn("mary.dou");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        traineeService.create(newTrainee);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).save(captor.capture());
        assertEquals("mary.dou", captor.getValue().getUsername());
    }

    @Test
    void testCreate_SetsActiveToTrue() {
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("Test");
        newTrainee.setLastName("User");
        newTrainee.setActive(false);

        when(usernameRegistryService.reserveUsername("Test", "User")).thenReturn("test.user");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(testTrainee);

        traineeService.create(newTrainee);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeRepository).save(captor.capture());
        assertTrue(captor.getValue().isActive());
    }
}




