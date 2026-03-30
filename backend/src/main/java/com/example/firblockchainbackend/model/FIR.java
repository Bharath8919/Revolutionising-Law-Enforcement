package com.example.firblockchainbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "firs")
public class FIR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime filedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User filedBy;

    @Column(nullable = false)
    private String status; // e.g., OPEN, IN_PROGRESS, CLOSED

    @Column
    private String category;

    @Column
    private String assignedOfficer;

    @Column(columnDefinition = "TEXT")
    private String investigationNotes;

    @Column
    private String blockchainTxHash; // transaction hash stored on blockchain
}
