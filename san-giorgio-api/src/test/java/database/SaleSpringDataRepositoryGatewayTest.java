package database;

import br.com.desafio.domain.entities.Sale;
import br.com.desafio.domain.model.SaleModel;
import br.com.desafio.domain.repository.SaleRepository;
import br.com.desafio.exception.ErrorToAccessDatabaseException;
import br.com.desafio.exception.PaymentNotFoundException;
import br.com.desafio.infrastructure.database.SaleSpringDataRepositoryGateway;
import br.com.desafio.interfaces.converter.SaleConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleSpringDataRepositoryGatewayTest {

    @InjectMocks
    private SaleSpringDataRepositoryGateway saleSpringDataRepositoryGateway;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private SaleConverter saleConverter;

    private UUID paymentId;
    private Sale saleEntity;
    private SaleModel saleModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentId = UUID.randomUUID();
        saleEntity = new Sale();
        saleModel = SaleModel.builder().build();
    }

    @Test
    void testExistsByIdSuccess() {
        when(saleRepository.existsById(any(UUID.class))).thenReturn(true);

        boolean result = saleSpringDataRepositoryGateway.existsById(paymentId);

        verify(saleRepository).existsById(paymentId);
        assertTrue(result);
    }

    @Test
    void testExistsByIdThrowsErrorToAccessDatabaseException() {
        when(saleRepository.existsById(any(UUID.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ErrorToAccessDatabaseException.class, () -> saleSpringDataRepositoryGateway.existsById(paymentId));

        verify(saleRepository).existsById(paymentId);
    }

    @Test
    void testFindByIdSuccess() {
        when(saleRepository.findById(any(UUID.class))).thenReturn(Optional.of(saleEntity));
        when(saleConverter.convertEntityToModel(any(Sale.class))).thenReturn(saleModel);

        SaleModel result = saleSpringDataRepositoryGateway.findById(paymentId);

        verify(saleRepository).findById(paymentId);
        verify(saleConverter).convertEntityToModel(saleEntity);

        assertNotNull(result);
        assertEquals(saleModel, result);
    }

    @Test
    void testFindByIdThrowsPaymentNotFoundException() {
        when(saleRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> saleSpringDataRepositoryGateway.findById(paymentId));

        verify(saleRepository).findById(paymentId);
        verify(saleConverter, never()).convertEntityToModel(any(Sale.class));
    }

    @Test
    void testFindByIdThrowsErrorToAccessDatabaseException() {
        when(saleRepository.findById(any(UUID.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ErrorToAccessDatabaseException.class, () -> saleSpringDataRepositoryGateway.findById(paymentId));

        verify(saleRepository).findById(paymentId);
    }
}

