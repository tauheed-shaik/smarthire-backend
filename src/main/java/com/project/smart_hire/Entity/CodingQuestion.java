package com.project.smart_hire.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CodingQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String functionSignature;   // e.g. "int add(int a, int b)"
    private String starterCode;         // what candidate sees initially
    private Long recruiterId;

    @ElementCollection
    private List<TestCase> testCases = new ArrayList<>();
}