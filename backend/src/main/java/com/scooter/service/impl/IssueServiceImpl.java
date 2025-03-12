package com.scooter.service.impl;

import com.scooter.dto.IssueDTO;
import com.scooter.entity.Issue;
import com.scooter.entity.Scooter;
import com.scooter.entity.User;
import com.scooter.repository.IssueRepository;
import com.scooter.repository.ScooterRepository;
import com.scooter.repository.UserRepository;
import com.scooter.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final ScooterRepository scooterRepository;

    @Override
    @Transactional
    public IssueDTO createIssue(IssueDTO issueDTO) {
        User user = userRepository.findById(issueDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Scooter scooter = scooterRepository.findById(issueDTO.getScooterId())
                .orElseThrow(() -> new RuntimeException("滑板车不存在"));

        Issue issue = new Issue();
        issue.setUser(user);
        issue.setScooter(scooter);
        issue.setDescription(issueDTO.getDescription());
        issue.setStatus(Issue.IssueStatus.PENDING);
        issue.setPriority(Issue.IssuePriority.MEDIUM);

        // 如果滑板车有问题，将其状态更新为维护中
        scooter.setStatus(Scooter.ScooterStatus.MAINTENANCE);
        scooterRepository.save(scooter);

        return IssueDTO.fromEntity(issueRepository.save(issue));
    }

    @Override
    public IssueDTO findById(Long id) {
        return issueRepository.findById(id)
                .map(IssueDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("问题不存在"));
    }

    @Override
    public List<IssueDTO> findByUserId(Long userId) {
        return issueRepository.findByUserId(userId).stream()
                .map(IssueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> findByScooterId(Long scooterId) {
        return issueRepository.findByScooterId(scooterId).stream()
                .map(IssueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> findByStatus(Issue.IssueStatus status) {
        return issueRepository.findByStatus(status).stream()
                .map(IssueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> findByPriority(Issue.IssuePriority priority) {
        return issueRepository.findByPriority(priority).stream()
                .map(IssueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<IssueDTO> findActiveIssuesOrderByPriority() {
        return issueRepository.findActiveIssuesOrderByPriority().stream()
                .map(IssueDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public IssueDTO updateStatus(Long id, Issue.IssueStatus status) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("问题不存在"));

        issue.setStatus(status);

        // 如果问题已解决，更新解决时间和滑板车状态
        if (status == Issue.IssueStatus.RESOLVED || status == Issue.IssueStatus.CLOSED) {
            issue.setResolvedAt(LocalDateTime.now());
            
            // 将滑板车状态改回可用
            Scooter scooter = issue.getScooter();
            if (scooter.getStatus() == Scooter.ScooterStatus.MAINTENANCE) {
                scooter.setStatus(Scooter.ScooterStatus.AVAILABLE);
                scooterRepository.save(scooter);
            }
        }

        return IssueDTO.fromEntity(issueRepository.save(issue));
    }

    @Override
    @Transactional
    public IssueDTO updatePriority(Long id, Issue.IssuePriority priority) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("问题不存在"));

        issue.setPriority(priority);
        return IssueDTO.fromEntity(issueRepository.save(issue));
    }

    @Override
    @Transactional
    public IssueDTO provideFeedback(Long id, String feedback) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("问题不存在"));

        issue.setFeedback(feedback);
        return IssueDTO.fromEntity(issueRepository.save(issue));
    }

    @Override
    public long countByStatus(Issue.IssueStatus status) {
        return issueRepository.countByStatus(status);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!issueRepository.existsById(id)) {
            throw new RuntimeException("问题不存在");
        }
        issueRepository.deleteById(id);
    }
} 