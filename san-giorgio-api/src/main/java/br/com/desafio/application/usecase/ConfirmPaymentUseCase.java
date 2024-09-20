package br.com.desafio.application.usecase;

import br.com.desafio.domain.model.PaymentModel;

public interface ConfirmPaymentUseCase {
    PaymentModel execute(PaymentModel paymentModel);
}
