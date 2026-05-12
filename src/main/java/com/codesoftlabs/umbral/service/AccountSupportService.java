package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.dto.BankDto;
import com.codesoftlabs.umbral.dto.CountryDto;
import com.codesoftlabs.umbral.dto.TimezoneDto;
import com.codesoftlabs.umbral.mapper.BankMapper;
import com.codesoftlabs.umbral.mapper.CountryMapper;
import com.codesoftlabs.umbral.mapper.TimezoneMapper;
import com.codesoftlabs.umbral.repository.BankRepository;
import com.codesoftlabs.umbral.repository.CountryRepository;
import com.codesoftlabs.umbral.repository.TimezoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountSupportService {

    private final TimezoneRepository timezoneRepository;
    private final CountryRepository countryRepository;
    private final BankRepository bankRepository;
    private final TimezoneMapper timezoneMapper;
    private final CountryMapper countryMapper;
    private final BankMapper bankMapper;

    @Transactional(readOnly = true)
    public List<TimezoneDto> getTimezones() {
        return timezoneRepository.findAll()
                .stream()
                .map(timezoneMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CountryDto> getCountries() {
        return countryRepository.findAll()
                .stream()
                .map(countryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BankDto> getBanks() {
        return bankRepository.findAll()
                .stream()
                .map(bankMapper::toDto)
                .toList();
    }
}
