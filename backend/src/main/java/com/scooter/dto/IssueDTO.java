package com.scooter.dto;

import com.scooter.entity.Issue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IssueDTO {
    private Long id;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "滑板车ID不能为空")
    private Long scooterId;
    
    @NotBlank(message = "问题描述不能为空")
    private String description;
    
    private Issue.IssueStatus status;
    private String feedback;
    private Issue.IssuePriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    
    public static IssueDTO fromEntity(Issue issue) {
        IssueDTO dto = new IssueDTO();
        dto.setId(issue.getId());
        dto.setUserId(issue.getUser().getId());
        dto.setScooterId(issue.getScooter().getId());
        dto.setDescription(issue.getDescription());
        dto.setStatus(issue.getStatus());
        dto.setFeedback(issue.getFeedback());
        dto.setPriority(issue.getPriority());
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setResolvedAt(issue.getResolvedAt());
        return dto;
    }
} 