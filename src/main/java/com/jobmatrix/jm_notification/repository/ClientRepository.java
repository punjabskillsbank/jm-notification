package com.jobmatrix.jm_notification.repository;

import com.common.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    Client findEmailByClientId(UUID clientId);
}
