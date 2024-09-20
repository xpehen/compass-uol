package br.com.desafio.interfaces.converter;

import br.com.desafio.interfaces.request.PaymentItemJson;
import br.com.desafio.interfaces.request.PaymentJson;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.entities.Payment;
import br.com.desafio.domain.enums.PaymentStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PaymentConverter {

    private final ObjectMapper objectMapper;

    public PaymentConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String  convertToString(PaymentItemModel payment) throws JsonProcessingException {
        return objectMapper.writeValueAsString(payment);
    }

    public PaymentModel convertRequestToModel(PaymentJson paymentJson) {
        List<PaymentItemModel> paymentItemModels = paymentJson.getPaymentItemJsons().stream()
                .map(this::convertPaymentItemToModel)
                .toList();

        return PaymentModel.builder()
                .clientId(UUID.fromString(paymentJson.getClientId()))
                .paymentItems(paymentItemModels)
                .build();
    }

    public PaymentJson convertModelToResponse(PaymentModel paymentModel){
        List<PaymentItemJson> paymentItensJson = paymentModel.getPaymentItems().stream()
                .map(this::convertPaymentItemModelToResponse)
                .toList();

        return PaymentJson.builder()
                .clientId(String.valueOf(paymentModel.getClientId()))
                .paymentItemJsons(paymentItensJson)
                .build();
    }

    public PaymentItemModel convertToModel(Payment payment) {
        return PaymentItemModel.builder()
                .paymentId(payment.getId())
                .paymentValue(payment.getPaymentValue())
                .build();
    }

    private PaymentItemModel convertPaymentItemToModel(PaymentItemJson paymentItemJson) {
        return PaymentItemModel.builder()
                .paymentId(UUID.fromString(paymentItemJson.getPaymentId()))
                .paymentValue(paymentItemJson.getPaymentValue())
                .paymentStatus(PaymentStatus.valueOf(paymentItemJson.getPaymentStatus()))
                .build();
    }

    private PaymentItemJson convertPaymentItemModelToResponse(PaymentItemModel paymentItemModel) {
        return PaymentItemJson.builder()
                .paymentId(String.valueOf(paymentItemModel.getPaymentId()))
                .paymentValue(paymentItemModel.getPaymentValue())
                .paymentStatus(paymentItemModel.getPaymentStatus().name())
                .build();
    }

    public Payment convertPaymentItemModelToEntity(PaymentItemModel paymentItemModel) {
        return Payment.builder()
                .id(paymentItemModel.getPaymentId())
                .paymentValue(paymentItemModel.getPaymentValue())
                .paymentStatus(String.valueOf(paymentItemModel.getPaymentStatus()))
                .build();
    }
}