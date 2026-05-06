package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.AccountMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountMemberRepository extends JpaRepository<AccountMember, UUID> {
}
