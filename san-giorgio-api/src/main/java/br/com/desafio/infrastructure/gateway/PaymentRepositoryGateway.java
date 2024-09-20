package br.com.desafio.infrastructure.gateway;

import br.com.desafio.domain.model.PaymentItemModel;

public interface PaymentRepositoryGateway {
    PaymentItemModel save(PaymentItemModel paymentItem);
}
