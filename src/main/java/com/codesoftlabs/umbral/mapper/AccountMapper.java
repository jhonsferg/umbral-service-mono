package com.codesoftlabs.umbral.mapper;

import com.codesoftlabs.umbral.dto.AccountDto;
import com.codesoftlabs.umbral.dto.BankSummaryDto;
import com.codesoftlabs.umbral.dto.UserSummaryDto;
import com.codesoftlabs.umbral.entity.Account;
import com.codesoftlabs.umbral.entity.Bank;
import com.codesoftlabs.umbral.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper implements EntityMapper<Account, AccountDto> {

    @Override
    public AccountDto toDto(Account entity) {
        if (entity == null) {
            return null;
        }

        return AccountDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type(entity.getType())
                .currency(entity.getCurrency())
                .balance(entity.getBalance())
                .isActive(entity.getIsActive())
                .order(entity.getOrder())
                .bank(toBankSummary(entity.getBank()))
                .owner(toUserSummary(entity.getOwner()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public Account toEntity(AccountDto dto) {
        if (dto == null) {
            return null;
        }

        return Account.builder()
                .id(dto.getId())
                .name(dto.getName())
                .type(dto.getType())
                .currency(dto.getCurrency())
                .balance(dto.getBalance())
                .isActive(dto.getIsActive())
                .order(dto.getOrder())
                .build();
    }

    private UserSummaryDto toUserSummary(User user) {
        if (user == null) return null;
        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private BankSummaryDto toBankSummary(Bank bank) {
        if (bank == null) return null;
        return BankSummaryDto.builder()
                .id(bank.getId())
                .name(bank.getName())
                .shortcut(bank.getShortcut())
                .logoUrl(bank.getLogoUrl())
                .build();
    }
}
