package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.*;
import com.codesoftlabs.umbral.dto.CurrencyDto;
import com.codesoftlabs.umbral.entity.Currency;
import com.codesoftlabs.umbral.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MetadataService {

    private final CurrencyRepository currencyRepository;

    public MetadataService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    @PostConstruct
    @Transactional
    public void seedCurrencies() {
        if (currencyRepository.count() == 0) {
            Currency currency = new Currency();
            currency.setCode("PEN");
            currency.setSymbol("S/");
            currency.setName("Sol Peruano");
            currency.setNamePlural("Soles Peruanos");
            currency.setIsBase(true);
            currency.setDecimalPlaces(2);

            currencyRepository.save(currency);
        }
    }

    @Transactional(readOnly = true)
    public List<CurrencyDto> getCurrencies() {
        return currencyRepository.findAllByOrderByCodeAsc()
                .stream()
                .map(c -> CurrencyDto.builder()
                        .id(c.getId())
                        .code(c.getCode())
                        .symbol(c.getSymbol())
                        .name(c.getName())
                        .namePlural(c.getNamePlural())
                        .decimalPlaces(c.getDecimalPlaces())
                        .build())
                .toList();
    }

    public Map<String, Object> getEnums() {
        return Map.of(
                "accountTypes", AccountType.values(),
                "transactionTypes", TransactionType.values(),
                "frequencies", Frequency.values(),
                "assetTypes", AssetType.values(),
                "accountRoles", AccountRole.values()
        );
    }
}
