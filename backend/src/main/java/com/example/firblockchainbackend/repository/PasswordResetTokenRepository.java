package com.example.firblockchainbackend.repository;

import com.example.firblockchainbackend.model.PasswordResetToken;
import com.example.firblockchainbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUser(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user")
    void deleteAllTokensForUser(@Param("user") User user);
}
