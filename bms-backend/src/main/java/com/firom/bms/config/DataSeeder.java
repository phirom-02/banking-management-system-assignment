package com.firom.bms.config;

import com.firom.bms.entity.Admin;
import com.firom.bms.enums.AdminRole;
import com.firom.bms.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a single SUPER_ADMIN account on first boot if the admins table is
 * empty, so the API is usable immediately after deployment. Change the
 * seeded password via SEED_ADMIN_USERNAME / SEED_ADMIN_PASSWORD env vars,
 * and rotate it immediately in any real environment.
 */
@NullMarked
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-username}")
    private String seedUsername;

    @Value("${app.seed.admin-password}")
    private String seedPassword;

    @Override
    public void run(String... args) {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername(seedUsername);
            admin.setPassword(passwordEncoder.encode(seedPassword));
            admin.setFullName("System Administrator");
            admin.setEmail("admin@corebanking.local");
            admin.setRole(AdminRole.SUPER_ADMIN);
            admin.setEnabled(true);
            adminRepository.save(admin);
            System.out.println("Seeded default admin user: " + seedUsername);
        }
    }
}
