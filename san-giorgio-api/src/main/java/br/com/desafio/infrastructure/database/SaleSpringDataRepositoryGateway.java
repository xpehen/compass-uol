package br.com.desafio.infrastructure.database;

import br.com.desafio.interfaces.converter.SaleConverter;
import br.com.desafio.domain.model.SaleModel;
import br.com.desafio.domain.entities.Sale;
import br.com.desafio.exception.ErrorToAccessDatabaseException;
import br.com.desafio.exception.PaymentNotFoundException;
import br.com.desafio.infrastructure.gateway.SaleRepositoryGateway;
import br.com.desafio.domain.repository.SaleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class SaleSpringDataRepositoryGateway implements SaleRepositoryGateway {

    private SaleRepository saleRepository;
    private SaleConverter saleConverter;

    @Override
    public boolean existsById(UUID clientId) {
        try{
            return saleRepository.existsById(clientId);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ErrorToAccessDatabaseException("An error occurs when try access database");
        }
    }

    @Override
    public SaleModel findById(UUID paymentId) {
        try{
            Sale saleFound = saleRepository.findById(paymentId).orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
            return saleConverter.convertEntityToModel(saleFound);
        } catch (PaymentNotFoundException e){
            log.warn(e.getMessage());
            throw e;
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ErrorToAccessDatabaseException("An error occurs when try access database");
        }
    }
}
