package com.epam.gym.crm.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Trainer extends User{

    private TrainingType specialization;

}
