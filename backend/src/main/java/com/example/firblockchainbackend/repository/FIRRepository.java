package com.example.firblockchainbackend.repository;

import com.example.firblockchainbackend.model.FIR;
import com.example.firblockchainbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FIRRepository extends JpaRepository<FIR, Long> {
    List<FIR> findByFiledBy(User user);

    @org.springframework.data.jpa.repository.Query("SELECT f.category, COUNT(f) FROM FIR f GROUP BY f.category")
    List<Object[]> countByCategory();

    @org.springframework.data.jpa.repository.Query("SELECT f.status, COUNT(f) FROM FIR f GROUP BY f.status")
    List<Object[]> countByStatus();
}
