package com.project.smart_hire.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phone;
    private String skills;        // JSON string: "[\"Java\",\"Spring\"]"
    private String experience;   // "3 years at ABC"
    private String education;
    private String resumePath;

}