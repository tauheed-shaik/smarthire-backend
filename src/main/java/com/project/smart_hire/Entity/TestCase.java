package com.project.smart_hire.Entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class TestCase {
    private String input;       // e.g. "2\n3"  (multi-line ok)
    private String expectedOutput;
    private boolean hidden;     // hidden = true → not shown to candidate
}