package com.NetMasters.NetMasters.infrastructure.persistence.repositories;

import com.NetMasters.NetMasters.infrastructure.persistence.models.ConnectFourMoveModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConnectFourMoveRepository extends JpaRepository<ConnectFourMoveModel, Long> {
}
