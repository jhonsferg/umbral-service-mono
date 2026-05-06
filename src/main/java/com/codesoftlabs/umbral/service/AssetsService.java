package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.CreateAssetDto;
import com.codesoftlabs.umbral.entity.Asset;
import com.codesoftlabs.umbral.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetsService {

    private final AssetRepository assetRepository;

    @Transactional
    public Asset create(UUID userId, CreateAssetDto dto) {
        Asset asset = new Asset();
        asset.setUserId(userId);
        asset.setName(dto.getName());
        asset.setDescription(dto.getDescription());
        if (dto.getType() != null) {
            asset.setType(dto.getType().toUpperCase());
        }
        asset.setValue(dto.getValue() != null ? dto.getValue() : java.math.BigDecimal.ZERO);
        asset.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "PEN");
        asset.setCondition(dto.getCondition());
        asset.setIsActive(true);

        return assetRepository.save(asset);
    }

    public List<Map<String, Object>> findAll(UUID userId) {
        List<Asset> assets = assetRepository.findByUserIdOrderByNameAsc(userId);
        return assets.stream().map(this::computeAsset).collect(Collectors.toList());
    }

    public Asset findOne(UUID userId, UUID id) {
        return assetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    @Transactional
    public Asset update(UUID userId, UUID id, CreateAssetDto dto) {
        Asset asset = findOne(userId, id);

        if (dto.getName() != null) asset.setName(dto.getName());
        if (dto.getDescription() != null) asset.setDescription(dto.getDescription());
        if (dto.getType() != null) asset.setType(dto.getType().toUpperCase());
        if (dto.getValue() != null) asset.setValue(dto.getValue());
        if (dto.getCurrency() != null) asset.setCurrency(dto.getCurrency());
        if (dto.getCondition() != null) asset.setCondition(dto.getCondition());

        return assetRepository.save(asset);
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        Asset asset = findOne(userId, id);
        assetRepository.delete(asset);
    }

    public Map<String, Object> getPortfolioSummary(UUID userId) {
        List<Map<String, Object>> assets = findAll(userId);

        double totalValue = assets.stream().mapToDouble(a -> (double) a.get("currentValue")).sum();
        double totalCost = assets.stream().mapToDouble(a -> (double) a.get("totalCost")).sum();

        Map<String, Double> allocation = new HashMap<>();
        for (Map<String, Object> asset : assets) {
            String type = asset.get("type").toString();
            double currentValue = (double) asset.get("currentValue");
            allocation.put(type, allocation.getOrDefault(type, 0.0) + currentValue);
        }

        List<Map<String, Object>> allocationList = allocation.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("type", e.getKey());
                    map.put("value", e.getValue());
                    map.put("percentage", totalValue > 0 ? (e.getValue() / totalValue) * 100 : 0);
                    return map;
                }).collect(Collectors.toList());

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalValue", totalValue);
        summary.put("totalProfitLoss", totalValue - totalCost);
        summary.put("profitLossPercentage", totalCost > 0 ? ((totalValue - totalCost) / totalCost) * 100 : 0);
        summary.put("allocation", allocationList);

        return summary;
    }

    private Map<String, Object> computeAsset(Asset asset) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", asset.getId());
        map.put("name", asset.getName());
        map.put("type", asset.getType());
        map.put("value", asset.getValue());
        map.put("currency", asset.getCurrency());
        map.put("condition", asset.getCondition());
        map.put("purchaseDate", asset.getPurchaseDate());
        map.put("userId", asset.getUserId());
        map.put("createdAt", asset.getCreatedAt());
        map.put("updatedAt", asset.getUpdatedAt());
        map.put("currentValue", asset.getValue() != null ? asset.getValue().doubleValue() : 0);
        map.put("totalCost", asset.getPurchasePrice() != null ? asset.getPurchasePrice().doubleValue() : 0);
        return map;
    }
}
