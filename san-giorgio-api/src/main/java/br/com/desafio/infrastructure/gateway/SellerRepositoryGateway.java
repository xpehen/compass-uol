package br.com.desafio.infrastructure.gateway;

import java.util.UUID;

public interface SellerRepositoryGateway {
    boolean existsById(UUID clientId);
}
