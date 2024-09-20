package br.com.desafio.application.usecase.impl;

import br.com.desafio.interfaces.converter.PaymentConverter;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.model.SaleModel;
import br.com.desafio.application.usecase.ConfirmPaymentUseCase;
import br.com.desafio.domain.enums.PaymentStatus;
import br.com.desafio.exception.SaleNotFoundException;
import br.com.desafio.exception.SellerNotFoundException;
import br.com.desafio.infrastructure.gateway.PaymentRepositoryGateway;
import br.com.desafio.infrastructure.gateway.SaleRepositoryGateway;
import br.com.desafio.infrastructure.gateway.SellerRepositoryGateway;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@AllArgsConstructor
public class ConfirmPaymentUseCaseImpl implements ConfirmPaymentUseCase {

    private SellerRepositoryGateway sellerRepositoryGateway;
    private PaymentRepositoryGateway paymentRepositoryGateway;
    private SaleRepositoryGateway saleRepositoryGateway;

    @Autowired
    private AmazonSQS amazonSQS;
    private ExecutorService executorService;
    private PaymentConverter paymentConverter;

    @Override
    public PaymentModel execute(PaymentModel paymentModel) {
        validateClientExist(paymentModel.getClientId());

        List<PaymentItemModel> savedPayments = new ArrayList<>();
        paymentModel.getPaymentItems().forEach(paymentItemModel -> {
            validateSaleExist(paymentItemModel.getPaymentId());
            processPaymentStatus(paymentItemModel);
            savedPayments.add(paymentRepositoryGateway.save(paymentItemModel));
        });

        paymentModel.setPaymentItems(savedPayments);

        paymentModel.getPaymentItems().forEach(this::sendToQueue);

        return paymentModel;
    }


    private void sendToQueue(PaymentItemModel paymentItemModel) {
        String queueUrl = switch (paymentItemModel.getPaymentStatus()) {
            case PARTIAL -> "aws.url.partial";
            case TOTAL -> "aws.url.total";
            case SURPLUS -> "aws.url.surplus";
        };

        executorService.execute(() -> {
            try {
                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMessageBody(paymentConverter.convertToString(paymentItemModel));
                amazonSQS.sendMessage(sendMessageRequest);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                //TODO: REPROCESS MESSAGE OR REDIRECT TO ANOTHER QUEUE
            }
        });
    }

    private void validateClientExist(UUID clientId) {
        if(!sellerRepositoryGateway.existsById(clientId)){
            throw new SellerNotFoundException("Seller not found");
        }
    }

    private void validateSaleExist(UUID paymentId) {
        if(!saleRepositoryGateway.existsById(paymentId)){
            throw new SaleNotFoundException("Sale not found");
        }
    }

    private void processPaymentStatus(PaymentItemModel paymentItem){
        SaleModel sale = saleRepositoryGateway.findById(paymentItem.getPaymentId());
        paymentItem.setPaymentStatus(calculateStatus(paymentItem, sale));
    }

    private static PaymentStatus calculateStatus(PaymentItemModel paymentItem, SaleModel sale) {
        BigDecimal saleValue = sale.getValue();
        BigDecimal paymentValue = paymentItem.getPaymentValue();

        if (paymentValue.compareTo(saleValue) < 0) {
            return PaymentStatus.PARTIAL;
        } else if (paymentValue.compareTo(saleValue) == 0) {
            return PaymentStatus.TOTAL;
        } else {
            return PaymentStatus.SURPLUS;
        }
    }
}

