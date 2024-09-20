package br.com.desafio.domain.model;

import br.com.desafio.domain.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
public class PaymentItemModel {
    private UUID paymentId;
    private BigDecimal paymentValue;
    private PaymentStatus paymentStatus;
}
