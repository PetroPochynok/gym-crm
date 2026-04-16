package com.epam.gym.crm.model;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Trainee extends User {

    private LocalDate dateOfBirth;
    private String address;

}
