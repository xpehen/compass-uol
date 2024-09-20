package br.com.desafio.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
public class SaleModel {
    private UUID id;
    private BigDecimal value;
}
