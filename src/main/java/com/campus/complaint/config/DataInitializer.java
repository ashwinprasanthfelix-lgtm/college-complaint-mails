package com.campus.complaint.config;

import com.campus.complaint.dao.AdminDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminDao adminDao;
    private final PasswordEncoder encoder;

    public DataInitializer(AdminDao adminDao, PasswordEncoder encoder) {
        this.adminDao = adminDao;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        if (adminDao.count() == 0) {
            adminDao.save("admin", encoder.encode("admin123"));
            System.out.println(">>> Default admin created: username=admin  password=admin123");
        }
    }
}
