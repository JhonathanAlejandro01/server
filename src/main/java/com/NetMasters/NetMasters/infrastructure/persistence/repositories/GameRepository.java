package com.NetMasters.NetMasters.infrastructure.persistence.repositories;

import com.NetMasters.NetMasters.infrastructure.persistence.models.GameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<GameModel, Long> {
}
