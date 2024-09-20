package br.com.desafio.infrastructure.database;

import br.com.desafio.interfaces.converter.PaymentConverter;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.entities.Payment;
import br.com.desafio.exception.ErrorToAccessDatabaseException;
import br.com.desafio.infrastructure.gateway.PaymentRepositoryGateway;
import br.com.desafio.domain.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class PaymentSpringDataRepositoryGateway implements PaymentRepositoryGateway {

    private PaymentRepository paymentRepository;
    private PaymentConverter paymentConverter;

    @Override
    public PaymentItemModel save(PaymentItemModel paymentItem) {
        try{
            Payment paymentRequest = paymentConverter.convertPaymentItemModelToEntity(paymentItem);
            Payment paymentSaved = paymentRepository.save(paymentRequest);
            return paymentConverter.convertToModel(paymentSaved);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ErrorToAccessDatabaseException("An error occurs when try access database");
        }
    }
}
