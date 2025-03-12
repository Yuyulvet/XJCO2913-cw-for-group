package com.scooter.repository;

import com.scooter.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByUserId(Long userId);
    
    List<Issue> findByScooterId(Long scooterId);
    
    List<Issue> findByStatus(Issue.IssueStatus status);
    
    List<Issue> findByPriority(Issue.IssuePriority priority);
    
    @Query("SELECT i FROM Issue i WHERE i.status IN ('PENDING', 'IN_PROGRESS') " +
           "ORDER BY CASE i.priority " +
           "WHEN 'CRITICAL' THEN 1 " +
           "WHEN 'HIGH' THEN 2 " +
           "WHEN 'MEDIUM' THEN 3 " +
           "WHEN 'LOW' THEN 4 END, i.createdAt ASC")
    List<Issue> findActiveIssuesOrderByPriority();
    
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.status = :status")
    long countByStatus(@Param("status") Issue.IssueStatus status);
} 