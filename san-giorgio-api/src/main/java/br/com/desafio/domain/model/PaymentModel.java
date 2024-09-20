package br.com.desafio.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
public class PaymentModel {
    private UUID clientId;
    private List<PaymentItemModel> paymentItems;
}
