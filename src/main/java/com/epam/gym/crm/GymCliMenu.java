package com.epam.gym.crm;

import com.epam.gym.crm.config.ApplicationConfig;
import com.epam.gym.crm.facade.GymFacade;
import com.epam.gym.crm.model.Trainee;
import com.epam.gym.crm.model.Trainer;
import com.epam.gym.crm.model.Training;
import com.epam.gym.crm.model.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class GymCliMenu {

    private static final Logger LOG = LoggerFactory.getLogger(GymCliMenu.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final GymFacade gymFacade;
    private final Scanner scanner;

    public GymCliMenu(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class)) {
            LOG.info("Gym CRM CLI Menu started");

            GymFacade gymFacade = context.getBean(GymFacade.class);
            GymCliMenu menu = new GymCliMenu(gymFacade);

            menu.run();

        } catch (Exception exception) {
            LOG.error("Application error occurred", exception);
        }
    }

    public void run() {
        LOG.info("Welcome to Gym CRM System!");

        while (true) {
            try {
                showMainMenu();
                int choice = readIntInput("Choose an option: ");

                switch (choice) {
                    case 1 -> handleTraineeOperations();
                    case 2 -> handleTrainerOperations();
                    case 3 -> handleTrainingOperations();
                    case 0 -> {
                        LOG.info("Exiting Gym CRM System. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception exception) {
                LOG.error("Error occurred during operation", exception);
                System.out.println("An error occurred. Please try again.");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n=== GYM CRM MAIN MENU ===");
        System.out.println("1. Trainee Operations");
        System.out.println("2. Trainer Operations");
        System.out.println("3. Training Operations");
        System.out.println("0. Exit");
        System.out.println("=========================");
    }

    private void handleTraineeOperations() {
        while (true) {
            showTraineeMenu();
            int choice = readIntInput("Choose trainee operation: ");

            switch (choice) {
                case 1 -> createTrainee();
                case 2 -> updateTrainee();
                case 3 -> deleteTrainee();
                case 4 -> selectTrainee();
                case 5 -> listAllTrainees();
                case 0 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleTrainerOperations() {
        while (true) {
            showTrainerMenu();
            int choice = readIntInput("Choose trainer operation: ");

            switch (choice) {
                case 1 -> createTrainer();
                case 2 -> updateTrainer();
                case 3 -> selectTrainer();
                case 4 -> listAllTrainers();
                case 0 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void handleTrainingOperations() {
        while (true) {
            showTrainingMenu();
            int choice = readIntInput("Choose training operation: ");

            switch (choice) {
                case 1 -> createTraining();
                case 2 -> selectTraining();
                case 3 -> listAllTrainings();
                case 0 -> { return; }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void showTraineeMenu() {
        System.out.println("\n=== TRAINEE OPERATIONS ===");
        System.out.println("1. Create Trainee");
        System.out.println("2. Update Trainee");
        System.out.println("3. Delete Trainee");
        System.out.println("4. Select Trainee");
        System.out.println("5. List All Trainees");
        System.out.println("0. Back to Main Menu");
        System.out.println("=========================");
    }

    private void showTrainerMenu() {
        System.out.println("\n=== TRAINER OPERATIONS ===");
        System.out.println("1. Create Trainer");
        System.out.println("2. Update Trainer");
        System.out.println("3. Select Trainer");
        System.out.println("4. List All Trainers");
        System.out.println("0. Back to Main Menu");
        System.out.println("=========================");
    }

    private void showTrainingMenu() {
        System.out.println("\n=== TRAINING OPERATIONS ===");
        System.out.println("1. Create Training");
        System.out.println("2. Select Training");
        System.out.println("3. List All Trainings");
        System.out.println("0. Back to Main Menu");
        System.out.println("=========================");
    }

    private void createTrainee() {
        System.out.println("\n--- Create New Trainee ---");

        String firstName = readStringInput("First Name: ");
        String lastName = readStringInput("Last Name: ");
        LocalDate dateOfBirth = readDateInput("Date of Birth (yyyy-MM-dd): ");
        String address = readStringInput("Address: ");

        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        try {
            Trainee created = gymFacade.createTrainee(trainee);
            System.out.println("Trainee created successfully!");
            System.out.println("ID: " + created.getId());
            System.out.println("Username: " + created.getUsername());
            LOG.info("Trainee created: username={}, id={}", created.getUsername(), created.getId());
        } catch (Exception e) {
            System.out.println("Error creating trainee: " + e.getMessage());
            LOG.error("Error creating trainee", e);
        }
    }

    private void updateTrainee() {
        System.out.println("\n--- Update Trainee ---");

        Long id = readLongInput("Trainee ID: ");
        Optional<Trainee> existing = gymFacade.getTraineeById(id);

        if (existing.isEmpty()) {
            System.out.println("Trainee with ID " + id + " not found.");
            return;
        }

        Trainee trainee = existing.get();
        System.out.println("Current data - First Name: " + trainee.getFirstName() +
                          ", Last Name: " + trainee.getLastName() +
                          ", Address: " + trainee.getAddress() +
                          ", Active: " + trainee.isActive());

        String firstName = readStringInput("New First Name (press Enter to keep current): ");
        if (!firstName.trim().isEmpty()) {
            trainee.setFirstName(firstName);
        }

        String lastName = readStringInput("New Last Name (press Enter to keep current): ");
        if (!lastName.trim().isEmpty()) {
            trainee.setLastName(lastName);
        }

        String address = readStringInput("New Address (press Enter to keep current): ");
        if (!address.trim().isEmpty()) {
            trainee.setAddress(address);
        }

        try {
            Trainee updated = gymFacade.updateTrainee(trainee);
            System.out.println("Trainee updated successfully!");
            LOG.info("Trainee updated: id={}", updated.getId());
        } catch (Exception e) {
            System.out.println("Error updating trainee: " + e.getMessage());
            LOG.error("Error updating trainee", e);
        }
    }

    private void deleteTrainee() {
        System.out.println("\n--- Delete Trainee ---");

        Long id = readLongInput("Trainee ID: ");

        try {
            gymFacade.deleteTrainee(id);
            System.out.println("Trainee deleted successfully!");
            LOG.info("Trainee deleted: id={}", id);
        } catch (Exception e) {
            System.out.println("Error deleting trainee: " + e.getMessage());
            LOG.error("Error deleting trainee", e);
        }
    }

    private void selectTrainee() {
        System.out.println("\n--- Select Trainee ---");

        Long id = readLongInput("Trainee ID: ");
        Optional<Trainee> trainee = gymFacade.getTraineeById(id);

        if (trainee.isPresent()) {
            Trainee t = trainee.get();
            System.out.println("Trainee found:");
            System.out.println("ID: " + t.getId());
            System.out.println("First Name: " + t.getFirstName());
            System.out.println("Last Name: " + t.getLastName());
            System.out.println("Username: " + t.getUsername());
            System.out.println("Date of Birth: " + t.getDateOfBirth());
            System.out.println("Address: " + t.getAddress());
            System.out.println("Active: " + t.isActive());
            LOG.info("Trainee selected: id={}", id);
        } else {
            System.out.println("Trainee with ID " + id + " not found.");
            LOG.debug("Trainee not found: id={}", id);
        }
    }

    private void listAllTrainees() {
        System.out.println("\n--- All Trainees ---");

        List<Trainee> trainees = gymFacade.getAllTrainees();

        if (trainees.isEmpty()) {
            System.out.println("No trainees found.");
        } else {
            System.out.println("Total trainees: " + trainees.size());
            for (Trainee trainee : trainees) {
                System.out.println("ID: " + trainee.getId() +
                                 ", Name: " + trainee.getFirstName() + " " + trainee.getLastName() +
                                 ", Username: " + trainee.getUsername() +
                                 ", Active: " + trainee.isActive());
            }
            LOG.info("Listed all trainees: count={}", trainees.size());
        }
    }

    private void createTrainer() {
        System.out.println("\n--- Create New Trainer ---");

        String firstName = readStringInput("First Name: ");
        String lastName = readStringInput("Last Name: ");
        String specialization = readStringInput("Specialization: ");

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setSpecialization(specialization);

        try {
            Trainer created = gymFacade.createTrainer(trainer);
            System.out.println("Trainer created successfully!");
            System.out.println("ID: " + created.getId());
            System.out.println("Username: " + created.getUsername());
            LOG.info("Trainer created: username={}, id={}", created.getUsername(), created.getId());
        } catch (Exception e) {
            System.out.println("Error creating trainer: " + e.getMessage());
            LOG.error("Error creating trainer", e);
        }
    }

    private void updateTrainer() {
        System.out.println("\n--- Update Trainer ---");

        Long id = readLongInput("Trainer ID: ");
        Optional<Trainer> existing = gymFacade.getTrainerById(id);

        if (existing.isEmpty()) {
            System.out.println("Trainer with ID " + id + " not found.");
            return;
        }

        Trainer trainer = existing.get();
        System.out.println("Current data - First Name: " + trainer.getFirstName() +
                          ", Last Name: " + trainer.getLastName() +
                          ", Specialization: " + trainer.getSpecialization() +
                          ", Active: " + trainer.isActive());

        String firstName = readStringInput("New First Name (press Enter to keep current): ");
        if (!firstName.trim().isEmpty()) {
            trainer.setFirstName(firstName);
        }

        String lastName = readStringInput("New Last Name (press Enter to keep current): ");
        if (!lastName.trim().isEmpty()) {
            trainer.setLastName(lastName);
        }

        String specialization = readStringInput("New Specialization (press Enter to keep current): ");
        if (!specialization.trim().isEmpty()) {
            trainer.setSpecialization(specialization);
        }

        try {
            Trainer updated = gymFacade.updateTrainer(trainer);
            System.out.println("Trainer updated successfully!");
            LOG.info("Trainer updated: id={}", updated.getId());
        } catch (Exception e) {
            System.out.println("Error updating trainer: " + e.getMessage());
            LOG.error("Error updating trainer", e);
        }
    }

    private void selectTrainer() {
        System.out.println("\n--- Select Trainer ---");

        Long id = readLongInput("Trainer ID: ");
        Optional<Trainer> trainer = gymFacade.getTrainerById(id);

        if (trainer.isPresent()) {
            Trainer t = trainer.get();
            System.out.println("Trainer found:");
            System.out.println("ID: " + t.getId());
            System.out.println("First Name: " + t.getFirstName());
            System.out.println("Last Name: " + t.getLastName());
            System.out.println("Username: " + t.getUsername());
            System.out.println("Specialization: " + t.getSpecialization());
            System.out.println("Active: " + t.isActive());
            LOG.info("Trainer selected: id={}", id);
        } else {
            System.out.println("Trainer with ID " + id + " not found.");
            LOG.debug("Trainer not found: id={}", id);
        }
    }

    private void listAllTrainers() {
        System.out.println("\n--- All Trainers ---");

        List<Trainer> trainers = gymFacade.getAllTrainers();

        if (trainers.isEmpty()) {
            System.out.println("No trainers found.");
        } else {
            System.out.println("Total trainers: " + trainers.size());
            for (Trainer trainer : trainers) {
                System.out.println("ID: " + trainer.getId() +
                                 ", Name: " + trainer.getFirstName() + " " + trainer.getLastName() +
                                 ", Username: " + trainer.getUsername() +
                                 ", Specialization: " + trainer.getSpecialization() +
                                 ", Active: " + trainer.isActive());
            }
            LOG.info("Listed all trainers: count={}", trainers.size());
        }
    }

    private void createTraining() {
        System.out.println("\n--- Create New Training ---");

        Long traineeId = readLongInput("Trainee ID: ");
        Long trainerId = readLongInput("Trainer ID: ");
        String trainingName = readStringInput("Training Name: ");
        TrainingType trainingType = readTrainingTypeInput("Training Type: ");
        LocalDate trainingDate = readDateInput("Training Date (yyyy-MM-dd): ");
        int duration = readIntInput("Duration (minutes): ");

        Training training = new Training();
        training.setTraineeId(traineeId);
        training.setTrainerId(trainerId);
        training.setTrainingName(trainingName);
        training.setTrainingType(trainingType);
        training.setTrainingDate(trainingDate);
        training.setDuration(duration);

        try {
            Training created = gymFacade.createTraining(training);
            System.out.println("Training created successfully!");
            System.out.println("ID: " + created.getId());
            LOG.info("Training created: id={}", created.getId());
        } catch (Exception e) {
            System.out.println("Error creating training: " + e.getMessage());
            LOG.error("Error creating training", e);
        }
    }

    private void selectTraining() {
        System.out.println("\n--- Select Training ---");

        Long id = readLongInput("Training ID: ");
        Optional<Training> training = gymFacade.getTrainingById(id);

        if (training.isPresent()) {
            Training t = training.get();
            System.out.println("Training found:");
            System.out.println("ID: " + t.getId());
            System.out.println("Trainee ID: " + t.getTraineeId());
            System.out.println("Trainer ID: " + t.getTrainerId());
            System.out.println("Training Name: " + t.getTrainingName());
            System.out.println("Training Type: " + t.getTrainingType());
            System.out.println("Training Date: " + t.getTrainingDate());
            System.out.println("Duration: " + t.getDuration() + " minutes");
            LOG.info("Training selected: id={}", id);
        } else {
            System.out.println("Training with ID " + id + " not found.");
            LOG.debug("Training not found: id={}", id);
        }
    }

    private void listAllTrainings() {
        System.out.println("\n--- All Trainings ---");

        List<Training> trainings = gymFacade.getAllTrainings();

        if (trainings.isEmpty()) {
            System.out.println("No trainings found.");
        } else {
            System.out.println("Total trainings: " + trainings.size());
            for (Training training : trainings) {
                System.out.println("ID: " + training.getId() +
                                 ", Trainee ID: " + training.getTraineeId() +
                                 ", Trainer ID: " + training.getTrainerId() +
                                 ", Name: " + training.getTrainingName() +
                                 ", Type: " + training.getTrainingType() +
                                 ", Date: " + training.getTrainingDate() +
                                 ", Duration: " + training.getDuration() + " min");
            }
            LOG.info("Listed all trainings: count={}", trainings.size());
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private long readLongInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private String readStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private LocalDate readDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Please enter date in format yyyy-MM-dd (e.g., 1990-01-15).");
            }
        }
    }

    private TrainingType readTrainingTypeInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt + " (");
                TrainingType[] types = TrainingType.values();
                for (int i = 0; i < types.length; i++) {
                    System.out.print(types[i]);
                    if (i < types.length - 1) System.out.print(", ");
                }
                System.out.print("): ");

                String input = scanner.nextLine().trim().toUpperCase();
                return TrainingType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Please enter a valid training type.");
            }
        }
    }
}

