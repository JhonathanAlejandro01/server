package com.NetMasters.NetMasters.infrastructure.persistence.repositories;

import com.NetMasters.NetMasters.infrastructure.persistence.models.TriquiGameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TriquiGameRepository extends JpaRepository<TriquiGameModel, Long> {
    Optional<TriquiGameModel> findByMatchId(Long matchId);
}
