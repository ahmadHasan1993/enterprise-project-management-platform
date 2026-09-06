package com.ahmadhassan.projectmanagement.identity;

import com.ahmadhassan.projectmanagement.PostgreSqlIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class RegistrationServiceIntegrationTest extends PostgreSqlIntegrationTestSupport {

    @Autowired
    RegistrationService registrationService;

    @Autowired
    EmailAddressRepository emailAddressRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void registerCreatesUnverifiedUserWithEncodedPassword() {
        String rawPassword = "this-is-a-secure-password";

        User user = registrationService.register("New.User@Example.com", rawPassword);

        assertThat(user.getId()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(AccountStatus.UNVERIFIED);
        assertThat(user.getPasswordHash()).isNotBlank();
        assertThat(user.getPasswordHash()).isNotEqualTo(rawPassword);
        assertThat(passwordEncoder.matches(rawPassword, user.getPasswordHash())).isTrue();
    }

    @Test
    void registerStoresCleanedAndNormalizedUnverifiedEmailAddress() {
        User user = registrationService.register("  Mixed.Case@Example.com  ", "this-is-a-secure-password");

        EmailAddress emailAddress = emailAddressRepository.findAll().stream()
                .filter(address -> address.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(emailAddress.getEmail()).isEqualTo("Mixed.Case@Example.com");
        assertThat(emailAddress.getNormalizedEmail()).isEqualTo("mixed.case@example.com");
        assertThat(emailAddress.getVerifiedAt()).isNull();
    }

    @Test
    void registerRejectsEmailAlreadyInUseIgnoringCase() {
        registrationService.register("Existing.User@Example.com", "this-is-a-secure-password");

        assertThatThrownBy(() -> registrationService.register("existing.user@example.com", "another-secure-password"))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessage("Email address is already in use");
    }

    @Test
    void registerRejectsPasswordExceedingBCryptInputLimit() {
        String password = "€".repeat(40);

        assertThatThrownBy(() -> registrationService.register("bcrypt-limit@example.com", password))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Password exceeds BCrypt input limit");
    }

    @Test
    void databaseRejectsDuplicateNormalizedEmail() {
        User firstUser = registrationService.register("db-unique@example.com", "this-is-a-secure-password");
        User secondUser = registrationService.register("second-db-user@example.com", "this-is-a-secure-password");
        EmailAddress duplicateEmailAddress = new EmailAddress(secondUser, "DB-Unique@example.com", "db-unique@example.com");

        assertThatThrownBy(() -> emailAddressRepository.saveAndFlush(duplicateEmailAddress))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}