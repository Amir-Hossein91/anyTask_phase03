package com.example.phase_03.entity;

import com.example.phase_03.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(name = "id_generator", sequenceName = "person_sequence")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Person_Roll",discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("No roll")

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Person extends BaseEntity {
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    public String toString() {
        return  "\tfirstName = " + this.getFirstName() +
                "\n\tlastName = " + this.getLastName() +
                "\n\t" + super.toString() +
                "\n\temail = " + this.getEmail() +
                "\n\tusername = " + this.getUsername() +
                "\n\tregistrationDate = " + BaseEntity.getPersianDateTime(this.getRegistrationDate());
    }
}
