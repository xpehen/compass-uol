package br.com.desafio.interfaces.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentJson {
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("payment_items")
    private List<PaymentItemJson> paymentItemJsons;
}
