package com.textseries.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
 
import com.textseries.config.JwtUtil;
import com.textseries.dto.LoginRequestDTO;
import com.textseries.model.User;
import com.textseries.repository.UserRepository;
import com.textseries.store.OtpStore;

@Service
public class AuthService {
 

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo, JwtUtil jwtUtil, EmailService emailService) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
   
    }

    // 🔥 SEND OTP
    public String sendOtp(String email) {
    	SecureRandom random = new SecureRandom();
    	String otp = String.format("%06d", random.nextInt(1000000));
        // save OTP in memory (simple) OR DB
        OtpStore.save(email, otp);

        emailService.sendOtp(email, otp);

        return "OTP sent";
    }

    // 🔥 REGISTER (after OTP verify)
    public String register(User user, String otp) {
 
        if (!OtpStore.verify(user.getEmail(), otp)) {
            throw new RuntimeException("Invalid OTP");
        }
       	if (repo.findByEmail(user.getEmail()).isPresent()) {
    	    throw new RuntimeException("Email already registered");
    	}

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setVerified(true);
        repo.save(user);
        return "Registered successfully";
    }

    public User getUserByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    // 🔥 LOGIN (EMAIL BASED)
    public String login(LoginRequestDTO request) {

        User user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified");
        }

        return jwtUtil.generateToken(user.getEmail(), user.getRole());
    }
}