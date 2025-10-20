package com.NetMasters.NetMasters.infrastructure.persistence.repositories;

import com.NetMasters.NetMasters.infrastructure.persistence.models.MatchModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<MatchModel, Long> {
}
