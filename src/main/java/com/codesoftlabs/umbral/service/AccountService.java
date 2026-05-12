package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.AccountRole;
import com.codesoftlabs.umbral.dto.AccountDto;
import com.codesoftlabs.umbral.dto.CreateAccountDto;
import com.codesoftlabs.umbral.dto.UpdateAccountDto;
import com.codesoftlabs.umbral.entity.Account;
import com.codesoftlabs.umbral.entity.AccountMember;
import com.codesoftlabs.umbral.mapper.AccountMapper;
import com.codesoftlabs.umbral.repository.AccountMemberRepository;
import com.codesoftlabs.umbral.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMemberRepository accountMemberRepository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository accountRepository, AccountMemberRepository accountMemberRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMemberRepository = accountMemberRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional
    public AccountDto create(UUID userId, CreateAccountDto dto) {
        Account account = new Account();
        account.setName(dto.getName());
        account.setDescription(dto.getDescription());
        account.setType(dto.getType() != null ? dto.getType() : "SAVINGS");
        account.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "PEN");
        account.setBalance(dto.getBalance() != null ? java.math.BigDecimal.valueOf(dto.getBalance()) : java.math.BigDecimal.ZERO);
        account.setBankId(UUID.fromString(dto.getBankId()));
        account.setOwnerId(userId);
        account.setIsActive(true);
        account.setOrder(0);
        Account savedAccount = accountRepository.save(account);
        AccountMember membership = new AccountMember();
        membership.setUserId(userId);
        membership.setAccountId(savedAccount.getId());
        membership.setRole(AccountRole.OWNER.toString());
        accountMemberRepository.save(membership);

        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    public List<AccountDto> findAll(UUID userId) {
        return accountRepository.findByOwnerId(userId)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional
    public AccountDto findOne(UUID userId, UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (!account.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this account");
        }

        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto update(UUID userId, UUID id, UpdateAccountDto dto) {
        Account account = this.getAccountEntity(userId, id);

        if (dto.getName() != null) account.setName(dto.getName());
        if (dto.getType() != null) account.setType(dto.getType());
        if (dto.getCurrency() != null) account.setCurrency(dto.getCurrency());
        if (dto.getBalance() != null) account.setBalance(BigDecimal.valueOf(dto.getBalance()));
        if (dto.getColor() != null) account.setColor(dto.getColor());
        if (dto.getIcon() != null) account.setIcon(dto.getIcon());
        if (dto.getBankId() != null) account.setBankId(dto.getBankId());

        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    public void remove(UUID userId, UUID id) {
        Account account = this.getAccountEntity(userId, id);

        if (!account.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can delete this account");
        }

        accountRepository.delete(account);
    }

    private Account getAccountEntity(UUID userId, UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (!account.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this account");
        }

        return account;
    }
}
