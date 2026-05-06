package com.codesoftlabs.umbral.service;

import com.codesoftlabs.umbral.common.enums.AccountRole;
import com.codesoftlabs.umbral.dto.CreateAccountDto;
import com.codesoftlabs.umbral.dto.UpdateAccountDto;
import com.codesoftlabs.umbral.entity.Account;
import com.codesoftlabs.umbral.entity.AccountMember;
import com.codesoftlabs.umbral.repository.AccountMemberRepository;
import com.codesoftlabs.umbral.repository.AccountRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMemberRepository accountMemberRepository;

    public AccountService(AccountRepository accountRepository, AccountMemberRepository accountMemberRepository) {
        this.accountRepository = accountRepository;
        this.accountMemberRepository = accountMemberRepository;
    }

    @Transactional
    @CacheEvict(value = "accountsAll", key = "#userId")
    public Account create(CreateAccountDto dto) {
        Account account = new Account();
        account.setName(dto.getName());
        account.setDescription(dto.getDescription());
        account.setType(dto.getType() != null ? dto.getType() : "SAVINGS");
        account.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "PEN");
        account.setColor(dto.getColor());
        account.setIcon(dto.getIcon());
        account.setOwnerId(dto.getOwnerId());
        account.setIsActive(true);
        account.setOrder(0);
        Account savedAccount = accountRepository.save(account);
        AccountMember membership = new AccountMember();
        membership.setUserId(dto.getOwnerId());
        membership.setAccountId(savedAccount.getId());
        membership.setRole(AccountRole.OWNER.toString());
        accountMemberRepository.save(membership);

        return savedAccount;
    }

    @Cacheable(value = "accountsAll", key = "#userId")
    public List<Account> findAll(UUID userId) {
        return accountRepository.findByOwnerId(userId);
    }

    @Cacheable(value = "accountsOne", key = "#id")
    public Account findOne(UUID userId, UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (!account.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this account");
        }

        return account;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accountsOne", key = "#id"),
            @CacheEvict(value = "accountsAll", allEntries = true)
    })
    public Account update(UUID userId, UUID id, UpdateAccountDto dto) {
        Account account = this.findOne(userId, id);

        if (dto.getName() != null)
            account.setName(dto.getName());
        if (dto.getDescription() != null)
            account.setDescription(dto.getDescription());
        if (dto.getType() != null)
            account.setType(dto.getType());
        if (dto.getCurrency() != null)
            account.setCurrency(dto.getCurrency());
        if (dto.getColor() != null)
            account.setColor(dto.getColor());
        if (dto.getIcon() != null)
            account.setIcon(dto.getIcon());

        return accountRepository.save(account);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "accountsOne", key = "#id"),
            @CacheEvict(value = "accountsAll", allEntries = true)
    })
    public void remove(UUID userId, UUID id) {
        Account account = this.findOne(userId, id);

        if (!account.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the owner can delete this account");
        }

        accountRepository.delete(account);
    }
}
