package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.CreateGoalDto;
import com.codesoftlabs.umbral.dto.response.GoalResponseDto;
import com.codesoftlabs.umbral.entity.Goal;
import com.codesoftlabs.umbral.mapper.GoalMapper;
import com.codesoftlabs.umbral.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalsService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Transactional
    public GoalResponseDto create(UUID userId, CreateGoalDto dto) {
        Goal goal = new Goal();
        goal.setUserId(userId);
        goal.setName(dto.getName());
        goal.setTargetAmount(dto.getTargetAmount() != null ? BigDecimal.valueOf(dto.getTargetAmount()) : BigDecimal.ZERO);
        goal.setCurrentAmount(dto.getCurrentAmount() != null ? BigDecimal.valueOf(dto.getCurrentAmount()) : BigDecimal.ZERO);
        goal.setCurrency(dto.getCurrencyCode() != null ? dto.getCurrencyCode() : "PEN");
        goal.setTargetDate(dto.getDeadline() != null ? dto.getDeadline().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null);

        return goalMapper.toDto(goalRepository.save(goal));
    }

    public List<Map<String, Object>> findAll(UUID userId) {
        List<Goal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return goals.stream().map(this::computeGoal).collect(Collectors.toList());
    }

    public GoalResponseDto findOneDto(UUID userId, UUID id) {
        return goalMapper.toDto(findOne(userId, id));
    }

    public Goal findOne(UUID userId, UUID id) {
        return goalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Goal not found"));
    }

    @Transactional
    public GoalResponseDto update(UUID userId, UUID id, CreateGoalDto dto) {
        Goal goal = findOne(userId, id);

        if (dto.getName() != null) goal.setName(dto.getName());
        if (dto.getTargetAmount() != null) goal.setTargetAmount(BigDecimal.valueOf(dto.getTargetAmount()));
        if (dto.getCurrentAmount() != null) goal.setCurrentAmount(BigDecimal.valueOf(dto.getCurrentAmount()));
        if (dto.getCurrencyCode() != null) goal.setCurrency(dto.getCurrencyCode());
        if (dto.getDeadline() != null)
            goal.setTargetDate(dto.getDeadline().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());

        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        Goal goal = findOne(userId, id);
        goalRepository.delete(goal);
    }

    private Map<String, Object> computeGoal(Goal goal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", goal.getId());
        map.put("name", goal.getName());
        map.put("targetAmount", goal.getTargetAmount());
        map.put("currentAmount", goal.getCurrentAmount());
        map.put("currency", goal.getCurrency());
        map.put("targetDate", goal.getTargetDate());
        map.put("status", goal.getStatus());
        map.put("priority", goal.getPriority());
        map.put("userId", goal.getUserId());
        map.put("createdAt", goal.getCreatedAt());
        map.put("updatedAt", goal.getUpdatedAt());
        map.put("deletedAt", goal.getDeletedAt());

        double target = goal.getTargetAmount() != null ? goal.getTargetAmount().doubleValue() : 0;
        double current = goal.getCurrentAmount() != null ? goal.getCurrentAmount().doubleValue() : 0;

        map.put("percentage", target > 0 ? Math.min((current / target) * 100, 100) : 0);
        map.put("remaining", Math.max(target - current, 0));

        return map;
    }
}
