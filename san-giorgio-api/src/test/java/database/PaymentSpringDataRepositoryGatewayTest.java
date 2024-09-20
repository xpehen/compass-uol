package database;

import br.com.desafio.domain.entities.Payment;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.repository.PaymentRepository;
import br.com.desafio.exception.ErrorToAccessDatabaseException;
import br.com.desafio.infrastructure.database.PaymentSpringDataRepositoryGateway;
import br.com.desafio.interfaces.converter.PaymentConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentSpringDataRepositoryGatewayTest {

    @InjectMocks
    private PaymentSpringDataRepositoryGateway paymentSpringDataRepositoryGateway;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentConverter paymentConverter;

    private PaymentItemModel paymentItemModel;
    private Payment paymentEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentItemModel = PaymentItemModel.builder().build();
        paymentEntity = Payment.builder().build();
    }

    @Test
    void testSavePaymentSuccess() {
        when(paymentConverter.convertPaymentItemModelToEntity(any(PaymentItemModel.class))).thenReturn(paymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenReturn(paymentEntity);
        when(paymentConverter.convertToModel(any(Payment.class))).thenReturn(paymentItemModel);

        PaymentItemModel result = paymentSpringDataRepositoryGateway.save(paymentItemModel);

        verify(paymentConverter).convertPaymentItemModelToEntity(paymentItemModel);
        verify(paymentRepository).save(paymentEntity);
        verify(paymentConverter).convertToModel(paymentEntity);

        assertNotNull(result);
        assertEquals(paymentItemModel, result);
    }

    @Test
    void testSavePaymentThrowsErrorToAccessDatabaseException() {
        when(paymentConverter.convertPaymentItemModelToEntity(any(PaymentItemModel.class))).thenReturn(paymentEntity);
        when(paymentRepository.save(any(Payment.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ErrorToAccessDatabaseException.class, () -> paymentSpringDataRepositoryGateway.save(paymentItemModel));

        verify(paymentConverter).convertPaymentItemModelToEntity(paymentItemModel);
        verify(paymentRepository).save(paymentEntity);
    }
}
