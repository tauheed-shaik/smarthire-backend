// src/main/java/com/project/smart_hire/Service/IMCQService.java
package com.project.smart_hire.Service;

import com.project.smart_hire.Entity.MCQ;
import java.util.List;

public interface IMCQService {
    List<MCQ> getAllMCQs();
    List<MCQ> generateMCQs(String topic, int count);
    List<MCQ> findByRecruiterId(Long recruiterId);
}