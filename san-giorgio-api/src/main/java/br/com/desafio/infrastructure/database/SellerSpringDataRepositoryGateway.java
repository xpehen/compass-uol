package br.com.desafio.infrastructure.database;

import br.com.desafio.exception.ErrorToAccessDatabaseException;
import br.com.desafio.infrastructure.gateway.SellerRepositoryGateway;
import br.com.desafio.domain.repository.SellerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class SellerSpringDataRepositoryGateway implements SellerRepositoryGateway {

    private SellerRepository sellerRepository;

    @Override
    public boolean existsById(UUID clientId) {
        try{
            return sellerRepository.existsById(clientId);

        } catch (Exception e){
            log.error(e.getMessage());
            throw new ErrorToAccessDatabaseException("An error occurs when try access database");
        }
    }
}
