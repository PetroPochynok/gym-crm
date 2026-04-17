package com.epam.gym.crm.init;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.model.TrainingType;
import com.epam.gym.crm.service.UsernameRegistryService;
import com.epam.gym.crm.storage.TraineeStorage;
import com.epam.gym.crm.storage.TrainerStorage;
import com.epam.gym.crm.storage.TrainingStorage;
import com.epam.gym.crm.util.CredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@Component
public class StorageDataInitializer implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(StorageDataInitializer.class);

    private final ResourceLoader resourceLoader;

    @Value("${storage.init.file}")
    private String storageInitFile;

    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;
    private TrainingStorage trainingStorage;
    private UsernameRegistryService usernameRegistryService;

    public StorageDataInitializer(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Autowired
    public void setUsernameRegistryService(UsernameRegistryService usernameRegistryService) {
        this.usernameRegistryService = usernameRegistryService;
    }

    @Override
    public void afterPropertiesSet() {
        if (!traineeStorage.getTrainees().isEmpty() || !trainerStorage.getTrainers().isEmpty() || !trainingStorage.getTrainings().isEmpty()) {
            registerExistingUsernames();
            LOG.info("Storages already contain data. Seed initialization skipped.");
            return;
        }

        Properties properties = loadProperties();
        if (properties.isEmpty()) {
            return;
        }

        initializeTrainees(properties);
        initializeTrainers(properties);
        initializeTrainings(properties);

        LOG.info("Storage initialization completed: trainees={}, trainers={}, trainings={}",
                traineeStorage.getTrainees().size(),
                trainerStorage.getTrainers().size(),
                trainingStorage.getTrainings().size());
    }

    @PreDestroy
    public void saveDataToFiles() {
        LOG.info("Saving data to files on shutdown...");

        try {
            saveTraineesToFile("trainees.txt");
            saveTrainersToFile("trainers.txt");
            saveTrainingsToFile("trainings.txt");

            LOG.info("Data persistence completed successfully");
        } catch (IOException exception) {
            LOG.error("Failed to save data to files", exception);
        }
    }

    private void saveTraineesToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("# Trainees\n");

            for (Trainee trainee : traineeStorage.getTrainees().values()) {
                String line = String.format("trainee.%d=%s,%s,%s,%s,%s\n",
                        trainee.getId(),
                        trainee.getFirstName(),
                        trainee.getLastName(),
                        trainee.getDateOfBirth(),
                        trainee.getAddress(),
                        trainee.isActive());
                writer.write(line);
            }
        }
    }

    private void saveTrainersToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("# Trainers\n");

            for (Trainer trainer : trainerStorage.getTrainers().values()) {
                String line = String.format("trainer.%d=%s,%s,%s,%s\n",
                        trainer.getId(),
                        trainer.getFirstName(),
                        trainer.getLastName(),
                        trainer.getSpecialization(),
                        trainer.isActive());
                writer.write(line);
            }
        }
    }

    private void saveTrainingsToFile(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("# Trainings\n");

            for (Training training : trainingStorage.getTrainings().values()) {
                String line = String.format("training.%d=%d,%d,%s,%s,%s,%d\n",
                        training.getId(),
                        training.getTraineeId(),
                        training.getTrainerId(),
                        training.getTrainingName(),
                        training.getTrainingType(),
                        training.getTrainingDate(),
                        training.getDuration());
                writer.write(line);
            }
        }
    }

    private Properties loadProperties() {
        Resource resource = resourceLoader.getResource(storageInitFile);
        if (!resource.exists()) {
            LOG.warn("Storage initialization file '{}' not found. Storages start empty.", storageInitFile);
            return new Properties();
        }

        Properties properties = new Properties();
        try (InputStream inputStream = resource.getInputStream()) {
            properties.load(inputStream);
            LOG.info("Loaded storage seed file '{}'.", storageInitFile);
            return properties;
        } catch (IOException exception) {
            LOG.error("Failed to read storage seed file '{}'. Storages start empty.", storageInitFile, exception);
            return new Properties();
        }
    }

    private void initializeTrainees(Properties properties) {
        List<String> keys = properties.stringPropertyNames().stream()
                .filter(key -> key.startsWith("trainee."))
                .sorted()
                .toList();

        for (String key : keys) {
            String[] fields = readCsvFields(properties.getProperty(key), 5, key);

            Trainee trainee = new Trainee();
            trainee.setId(extractId(key, "trainee."));
            trainee.setFirstName(fields[0].trim());
            trainee.setLastName(fields[1].trim());
            trainee.setDateOfBirth(LocalDate.parse(fields[2].trim()));
            trainee.setAddress(fields[3].trim());
            trainee.setActive(Boolean.parseBoolean(fields[4].trim()));

            String username = usernameRegistryService.reserveUsername(trainee.getFirstName(), trainee.getLastName());
            trainee.setUsername(username);
            trainee.setPassword(CredentialGenerator.generatePassword());

            traineeStorage.getTrainees().put(trainee.getId(), trainee);
        }
    }

    private void initializeTrainers(Properties properties) {
        List<String> keys = properties.stringPropertyNames().stream()
                .filter(key -> key.startsWith("trainer."))
                .sorted()
                .toList();

        for (String key : keys) {
            String[] fields = readCsvFields(properties.getProperty(key), 4, key);

            Trainer trainer = new Trainer();
            trainer.setId(extractId(key, "trainer."));
            trainer.setFirstName(fields[0].trim());
            trainer.setLastName(fields[1].trim());
            trainer.setSpecialization(fields[2].trim());
            trainer.setActive(Boolean.parseBoolean(fields[3].trim()));

            String username = usernameRegistryService.reserveUsername(trainer.getFirstName(), trainer.getLastName());
            trainer.setUsername(username);
            trainer.setPassword(CredentialGenerator.generatePassword());

            trainerStorage.getTrainers().put(trainer.getId(), trainer);
        }
    }

    private void initializeTrainings(Properties properties) {
        List<String> keys = properties.stringPropertyNames().stream()
                .filter(key -> key.startsWith("training."))
                .sorted()
                .toList();

        for (String key : keys) {
            String[] fields = readCsvFields(properties.getProperty(key), 6, key);

            Training training = new Training();
            training.setId(extractId(key, "training."));
            training.setTraineeId(Long.parseLong(fields[0].trim()));
            training.setTrainerId(Long.parseLong(fields[1].trim()));
            training.setTrainingName(fields[2].trim());
            training.setTrainingType(TrainingType.valueOf(fields[3].trim()));
            training.setTrainingDate(LocalDate.parse(fields[4].trim()));
            training.setDuration(Integer.parseInt(fields[5].trim()));

            trainingStorage.getTrainings().put(training.getId(), training);
        }
    }

    private String[] readCsvFields(String value, int expectedSize, String key) {
        String[] fields = value.split(",", -1);
        if (fields.length != expectedSize) {
            throw new IllegalArgumentException(
                    "Invalid format for key '" + key + "'. Expected " + expectedSize + " fields, got " + fields.length);
        }
        return fields;
    }

    private Long extractId(String key, String prefix) {
        return Long.parseLong(key.substring(prefix.length()));
    }

    private void registerExistingUsernames() {
        List<String> existingUsernames = Stream.concat(
                        traineeStorage.getTrainees().values().stream().map(Trainee::getUsername),
                        trainerStorage.getTrainers().values().stream().map(Trainer::getUsername))
                .filter(username -> username != null && !username.isBlank())
                .toList();

        usernameRegistryService.initializeFromExisting(existingUsernames);
    }
}
