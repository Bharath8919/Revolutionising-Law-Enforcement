package com.example.firblockchainbackend.controller;

import com.example.firblockchainbackend.model.FIR;
import com.example.firblockchainbackend.service.FIRService;
import com.example.firblockchainbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/firs")
public class FIRController {

    @Autowired
    private FIRService firService;

    @Autowired
    private com.example.firblockchainbackend.repository.UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    // Filter FIRs: Users see only their own, Admins see all
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<FIR> getAllFIRs(java.security.Principal principal, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return firService.getAllFIRs();
        }
        
        if (principal != null) {
            com.example.firblockchainbackend.model.User user = userRepository.findByUsername(principal.getName());
            if (user != null) {
                return firService.getFIRsByUser(user);
            }
        }
        return java.util.Collections.emptyList();
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FIR> createFIR(@RequestBody FIR fir, java.security.Principal principal) {
        if (principal != null) {
            com.example.firblockchainbackend.model.User user = userRepository.findByUsername(principal.getName());
            fir.setFiledBy(user);
        }
        fir.setFiledAt(LocalDateTime.now());
        fir.setStatus("OPEN");
        FIR created = firService.createFIR(fir);
        return ResponseEntity.ok(created);
    }

    // Only Admin can update FIR status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FIR> updateFIRStatus(@PathVariable Long id, @RequestBody String newStatus) {
        FIR updated = firService.updateStatus(id, newStatus);
        // Trigger Email Notification
        emailService.sendStatusUpdateEmail(updated);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FIR> updateFIR(@PathVariable Long id, @RequestBody FIR fir) {
        FIR updated = firService.updateFIR(id, fir);
        // Trigger Email Notification
        emailService.sendStatusUpdateEmail(updated);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<FIR> getFIRById(@PathVariable Long id) {
        return firService.getFIRById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFIR(@PathVariable Long id) {
        firService.getFIRById(id).ifPresent(fir -> {
            // Logic for deletion if needed
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, Object>> getStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("byCategory", firService.getStatsByCategory());
        stats.put("byStatus", firService.getStatsByStatus());
        return ResponseEntity.ok(stats);
    }
}
