package com.stocksense.service;

import com.stocksense.dto.AuthRequestDto;
import com.stocksense.dto.AuthResponseDto;
import com.stocksense.exception.ConflictException;
import com.stocksense.exception.UnauthorizedException;
import com.stocksense.model.User;
import com.stocksense.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponseDto signup(AuthRequestDto dto) {
        String email = dto.getEmail();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("An account with email '" + email + "' already exists.");
        }

        String passwordHash = passwordEncoder.encode(dto.getPassword());
        User user = new User(email, passwordHash);
        User saved = userRepository.save(user);

        String token = jwtService.generateToken(saved.getEmail(), saved.getId());
        return new AuthResponseDto(token, saved.getEmail(), saved.getId(), "Account created successfully");
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthRequestDto dto) {
        String email = dto.getEmail();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password.");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponseDto(token, user.getEmail(), user.getId(), "Login successful");
    }

    @Transactional(readOnly = true)
    public User getCurrentUser(String authHeader) {
        String email = jwtService.validateTokenAndGetEmail(authHeader);
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("User not found for token."));
    }
}
