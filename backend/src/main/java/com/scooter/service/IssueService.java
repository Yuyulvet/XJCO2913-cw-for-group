package com.scooter.service;

import com.scooter.dto.IssueDTO;
import com.scooter.entity.Issue;
import java.util.List;

public interface IssueService {
    IssueDTO createIssue(IssueDTO issueDTO);
    
    IssueDTO findById(Long id);
    
    List<IssueDTO> findByUserId(Long userId);
    
    List<IssueDTO> findByScooterId(Long scooterId);
    
    List<IssueDTO> findByStatus(Issue.IssueStatus status);
    
    List<IssueDTO> findByPriority(Issue.IssuePriority priority);
    
    List<IssueDTO> findActiveIssuesOrderByPriority();
    
    IssueDTO updateStatus(Long id, Issue.IssueStatus status);
    
    IssueDTO updatePriority(Long id, Issue.IssuePriority priority);
    
    IssueDTO provideFeedback(Long id, String feedback);
    
    long countByStatus(Issue.IssueStatus status);
    
    void delete(Long id);
} 