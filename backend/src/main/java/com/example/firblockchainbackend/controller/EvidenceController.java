package com.example.firblockchainbackend.controller;

import com.example.firblockchainbackend.model.Evidence;
import com.example.firblockchainbackend.model.FIR;
import com.example.firblockchainbackend.service.EvidenceService;
import com.example.firblockchainbackend.service.FIRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/evidence")
public class EvidenceController {

    private final EvidenceService evidenceService;
    private final FIRService firService;

    @Autowired
    public EvidenceController(EvidenceService evidenceService, FIRService firService) {
        this.evidenceService = evidenceService;
        this.firService = firService;
    }

    @PostMapping("/upload/{firId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadEvidence(@PathVariable Long firId, @RequestParam("file") MultipartFile file, Principal principal) {
        try {
            FIR fir = firService.getFIRById(firId).orElseThrow(() -> new IllegalArgumentException("FIR not found"));
            
            // Check authorization if user: Only allow uploader of FIR or Admin
            // (Wait, uploader is 'filedBy' user object, getUsername() is email)
            // String currentUser = principal.getName();
            // Boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            // if (!isAdmin && !fir.getFiledBy().getEmail().equals(currentUser)) {
            //      return ResponseEntity.status(403).body("Unauthorized to upload evidence for this case.");
            // }

            Evidence saved = evidenceService.storeEvidence(fir, file, principal.getName());
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to upload: " + e.getMessage());
        }
    }

    @GetMapping("/fir/{firId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Evidence>> getEvidenceForFIR(@PathVariable Long firId) {
        FIR fir = firService.getFIRById(firId).orElseThrow(() -> new IllegalArgumentException("FIR not found"));
        return ResponseEntity.ok(evidenceService.getEvidenceByFIR(fir));
    }

    @GetMapping("/download/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadEvidence(@PathVariable Long id) {
        try {
            Evidence evidence = evidenceService.getEvidenceById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Evidence record not found"));
            
            Path filePath = Paths.get(evidence.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(evidence.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + evidence.getFileName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
