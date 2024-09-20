package br.com.desafio.interfaces.converter;

import br.com.desafio.domain.model.SaleModel;
import br.com.desafio.domain.entities.Sale;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class SaleConverter {

    private final ObjectMapper objectMapper;

    public SaleConverter(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public SaleModel convertEntityToModel(Sale saleEntity){
        return SaleModel.builder()
                .id(saleEntity.getId())
                .value(saleEntity.getSaleValue())
                .build();
    }
}
