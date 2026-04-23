package com.epam.gym.crm.init;

import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.model.TrainingType;
import com.epam.gym.crm.repository.TraineeRepository;
import com.epam.gym.crm.repository.TrainerRepository;
import com.epam.gym.crm.repository.TrainingRepository;
import com.epam.gym.crm.service.UsernameRegistryService;
import com.epam.gym.crm.storage.TraineeStorage;
import com.epam.gym.crm.storage.TrainerStorage;
import com.epam.gym.crm.storage.TrainingStorage;
import com.epam.gym.crm.util.CredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

@Component
public class StorageDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(StorageDataInitializer.class);

    private final ResourceLoader resourceLoader;

    @Value("${storage.init.file}")
    private String storageInitFile;

    private TraineeStorage traineeStorage;
    private TrainerStorage trainerStorage;
    private TrainingStorage trainingStorage;
    private UsernameRegistryService usernameRegistryService;
    private TraineeRepository traineeRepository;
    private TrainerRepository trainerRepository;
    private TrainingRepository trainingRepository;

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

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setTrainingRepository(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @PostConstruct
    public void initializeData() {
        LOG.info("Starting data initialization...");

        if (loadSavedData()) {
            LOG.info("Successfully loaded saved data from files");
            registerExistingUsernames();
            updateNextIds();
            return;
        }

        LOG.info("No saved data files found. Loading seed data from properties...");
        Properties properties = loadProperties();
        if (properties.isEmpty()) {
            LOG.warn("No seed data found. Storages initialized as empty.");
            return;
        }

        initializeTrainees(properties);
        initializeTrainers(properties);
        initializeTrainings(properties);

        updateNextIds();

        LOG.info("Storage initialization completed: trainees={}, trainers={}, trainings={}",
                traineeStorage.getTrainees().size(),
                trainerStorage.getTrainers().size(),
                trainingStorage.getTrainings().size());
    }

    private boolean loadSavedData() {
        File traineesFile = new File("trainees.txt");
        File trainersFile = new File("trainers.txt");
        File trainingsFile = new File("trainings.txt");

        if (!traineesFile.exists() && !trainersFile.exists() && !trainingsFile.exists()) {
            LOG.debug("No saved data files found");
            return false;
        }

        try {
            if (traineesFile.exists()) {
                loadTraineesFromFile("trainees.txt");
                LOG.info("Loaded {} trainees from file", traineeStorage.getTrainees().size());
            }

            if (trainersFile.exists()) {
                loadTrainersFromFile("trainers.txt");
                LOG.info("Loaded {} trainers from file", trainerStorage.getTrainers().size());
            }

            if (trainingsFile.exists()) {
                loadTrainingsFromFile("trainings.txt");
                LOG.info("Loaded {} trainings from file", trainingStorage.getTrainings().size());
            }

            return true;
        } catch (IOException exception) {
            LOG.error("Failed to load saved data from files", exception);
            return false;
        }
    }

    private void loadTraineesFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;

                String[] fields = parts[1].split(",");
                if (fields.length < 5) continue;

                Trainee trainee = new Trainee();
                trainee.setFirstName(fields[0].trim());
                trainee.setLastName(fields[1].trim());
                trainee.setDateOfBirth(LocalDate.parse(fields[2].trim()));
                trainee.setAddress(fields[3].trim());
                trainee.setActive(Boolean.parseBoolean(fields[4].trim()));

                String username = usernameRegistryService.reserveUsername(trainee.getFirstName(), trainee.getLastName());
                trainee.setUsername(username);
                trainee.setPassword(CredentialGenerator.generatePassword());

                traineeRepository.save(trainee);
            }
        }
    }

    private void loadTrainersFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;

                String[] fields = parts[1].split(",");
                if (fields.length < 4) continue;

                Trainer trainer = new Trainer();
                trainer.setFirstName(fields[0].trim());
                trainer.setLastName(fields[1].trim());
                trainer.setSpecialization(fields[2].trim());
                trainer.setActive(Boolean.parseBoolean(fields[3].trim()));

                String username = usernameRegistryService.reserveUsername(trainer.getFirstName(), trainer.getLastName());
                trainer.setUsername(username);
                trainer.setPassword(CredentialGenerator.generatePassword());

                trainerRepository.save(trainer);
            }
        }
    }

    private void loadTrainingsFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;

                String[] fields = parts[1].split(",");
                if (fields.length < 7) continue;

                Training training = new Training();
                training.setTraineeId(Long.parseLong(fields[0].trim()));
                training.setTrainerId(Long.parseLong(fields[1].trim()));
                training.setTrainingName(fields[2].trim());
                training.setTrainingType(TrainingType.valueOf(fields[3].trim()));
                training.setTrainingDate(LocalDate.parse(fields[4].trim()));
                training.setDuration(Integer.parseInt(fields[5].trim()));

                trainingRepository.save(training);
            }
        }
    }

    private void updateNextIds() {
        long maxTraineeId = traineeStorage.getTrainees().keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        traineeStorage.setNextId(maxTraineeId + 1);

        long maxTrainerId = trainerStorage.getTrainers().keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        trainerStorage.setNextId(maxTrainerId + 1);

        long maxTrainingId = trainingStorage.getTrainings().keySet().stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
        trainingStorage.setNextId(maxTrainingId + 1);
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
