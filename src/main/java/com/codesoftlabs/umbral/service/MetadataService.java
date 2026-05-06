package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.*;
import com.codesoftlabs.umbral.entity.Currency;
import com.codesoftlabs.umbral.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "currencies")
    public List<Currency> getCurrencies() {
        return currencyRepository.findAllByOrderByCodeAsc();
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