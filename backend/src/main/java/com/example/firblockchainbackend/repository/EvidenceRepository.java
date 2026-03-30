package com.example.firblockchainbackend.repository;

import com.example.firblockchainbackend.model.Evidence;
import com.example.firblockchainbackend.model.FIR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByFir(FIR fir);
}
