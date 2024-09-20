package br.com.desafio.infrastructure.gateway;

import br.com.desafio.domain.model.SaleModel;

import java.util.UUID;

public interface SaleRepositoryGateway {
    boolean existsById(UUID paymentId);
    SaleModel findById(UUID saleId);
}
