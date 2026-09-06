package com.ahmadhassan.projectmanagement.identity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;


@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final EmailAddressRepository emailAddressRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
            EmailAddressRepository emailAddressRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.emailAddressRepository = emailAddressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String email, String rawPassword) {

        String cleanedEmail = email.trim();
        String normalizedEmail = cleanedEmail.toLowerCase(Locale.ROOT);

        if (emailAddressRepository.existsByNormalizedEmail(normalizedEmail)) {
            throw new EmailAlreadyInUseException();
        }

        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new InvalidPasswordException("Password exceeds BCrypt input limit");
        }
        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(passwordHash);
        userRepository.save(user);

        EmailAddress emailAddress = new EmailAddress(user, cleanedEmail, normalizedEmail);

        emailAddressRepository.save(emailAddress);

        return user;
    }
}