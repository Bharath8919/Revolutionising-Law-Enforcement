package com.example.firblockchainbackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence")
@Data
public class Evidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;
    private String filePath;
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fir_id")
    private FIR fir;

    private String uploadedBy; // Username of the uploader

    public Evidence() {
        this.uploadedAt = LocalDateTime.now();
    }
}
