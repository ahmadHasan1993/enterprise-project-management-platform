package com.ahmadhassan.projectmanagement.identity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAddressRepository extends JpaRepository<EmailAddress, UUID> {

    boolean existsByNormalizedEmail(String normalizedEmail);
}