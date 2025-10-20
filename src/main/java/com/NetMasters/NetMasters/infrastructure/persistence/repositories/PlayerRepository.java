package com.NetMasters.NetMasters.infrastructure.persistence.repositories;

import com.NetMasters.NetMasters.infrastructure.persistence.models.PlayerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerModel, Long> {
    Optional<PlayerModel> findByEmail(String email);
}