package com.example.firblockchainbackend.service;

import com.example.firblockchainbackend.model.Evidence;
import com.example.firblockchainbackend.model.FIR;
import com.example.firblockchainbackend.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final String uploadDir = "uploads/evidence";

    @Autowired
    public EvidenceService(EvidenceRepository evidenceRepository) {
        this.evidenceRepository = evidenceRepository;
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            System.err.println("Could not create upload directory: " + e.getMessage());
        }
    }

    public Evidence storeEvidence(FIR fir, MultipartFile file, String uploadedBy) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetPath = Paths.get(uploadDir).resolve(fileName);
        Files.copy(file.getInputStream(), targetPath);

        Evidence evidence = new Evidence();
        evidence.setFir(fir);
        evidence.setFileName(file.getOriginalFilename());
        evidence.setFileType(file.getContentType());
        evidence.setFilePath(targetPath.toString());
        evidence.setUploadedBy(uploadedBy);

        return evidenceRepository.save(evidence);
    }

    public List<Evidence> getEvidenceByFIR(FIR fir) {
        return evidenceRepository.findByFir(fir);
    }

    public java.util.Optional<Evidence> getEvidenceById(Long id) {
        return evidenceRepository.findById(id);
    }
}
