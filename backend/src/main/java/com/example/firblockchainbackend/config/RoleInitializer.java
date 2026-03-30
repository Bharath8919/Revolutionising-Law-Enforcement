package com.example.firblockchainbackend.config;

import com.example.firblockchainbackend.model.Role;
import com.example.firblockchainbackend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName("ROLE_USER") == null) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }
        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
    }
}
