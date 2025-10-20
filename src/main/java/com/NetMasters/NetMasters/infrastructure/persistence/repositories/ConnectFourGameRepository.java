package com.NetMasters.NetMasters.infrastructure.persistence.repositories;

import com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourGameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectFourGameRepository extends JpaRepository<ConnectFourGameModel, Long> {
	java.util.Optional<ConnectFourGameModel> findByMatchId(Long matchId);
}
