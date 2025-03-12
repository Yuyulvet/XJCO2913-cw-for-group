package com.scooter.controller;

import com.scooter.dto.IssueDTO;
import com.scooter.entity.Issue;
import com.scooter.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;

    @PostMapping
    public ResponseEntity<IssueDTO> createIssue(@Valid @RequestBody IssueDTO issueDTO) {
        return ResponseEntity.ok(issueService.createIssue(issueDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueDTO> getIssue(@PathVariable Long id) {
        return ResponseEntity.ok(issueService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<IssueDTO>> getUserIssues(@PathVariable Long userId) {
        return ResponseEntity.ok(issueService.findByUserId(userId));
    }

    @GetMapping("/scooter/{scooterId}")
    public ResponseEntity<List<IssueDTO>> getScooterIssues(@PathVariable Long scooterId) {
        return ResponseEntity.ok(issueService.findByScooterId(scooterId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<IssueDTO>> getIssuesByStatus(@PathVariable Issue.IssueStatus status) {
        return ResponseEntity.ok(issueService.findByStatus(status));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<IssueDTO>> getIssuesByPriority(@PathVariable Issue.IssuePriority priority) {
        return ResponseEntity.ok(issueService.findByPriority(priority));
    }

    @GetMapping("/active")
    public ResponseEntity<List<IssueDTO>> getActiveIssues() {
        return ResponseEntity.ok(issueService.findActiveIssuesOrderByPriority());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<IssueDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam Issue.IssueStatus status) {
        return ResponseEntity.ok(issueService.updateStatus(id, status));
    }

    @PutMapping("/{id}/priority")
    public ResponseEntity<IssueDTO> updatePriority(
            @PathVariable Long id,
            @RequestParam Issue.IssuePriority priority) {
        return ResponseEntity.ok(issueService.updatePriority(id, priority));
    }

    @PutMapping("/{id}/feedback")
    public ResponseEntity<IssueDTO> provideFeedback(
            @PathVariable Long id,
            @RequestParam String feedback) {
        return ResponseEntity.ok(issueService.provideFeedback(id, feedback));
    }

    @GetMapping("/count/{status}")
    public ResponseEntity<Long> countByStatus(@PathVariable Issue.IssueStatus status) {
        return ResponseEntity.ok(issueService.countByStatus(status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIssue(@PathVariable Long id) {
        issueService.delete(id);
        return ResponseEntity.ok().build();
    }
} 