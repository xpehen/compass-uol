package usecase;

import br.com.desafio.application.usecase.impl.ConfirmPaymentUseCaseImpl;
import br.com.desafio.domain.model.PaymentItemModel;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.model.SaleModel;
import br.com.desafio.exception.SaleNotFoundException;
import br.com.desafio.exception.SellerNotFoundException;
import br.com.desafio.infrastructure.gateway.PaymentRepositoryGateway;
import br.com.desafio.infrastructure.gateway.SaleRepositoryGateway;
import br.com.desafio.infrastructure.gateway.SellerRepositoryGateway;
import br.com.desafio.interfaces.converter.PaymentConverter;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmPaymentUseCaseImplTest {

    @InjectMocks
    private ConfirmPaymentUseCaseImpl confirmPaymentUseCase;

    @Mock
    private SellerRepositoryGateway sellerRepositoryGateway;

    @Mock
    private PaymentRepositoryGateway paymentRepositoryGateway;

    @Mock
    private SaleRepositoryGateway saleRepositoryGateway;

    @Mock
    private AmazonSQS amazonSQS;

    @Mock
    private ExecutorService executorService;

    @Mock
    private PaymentConverter paymentConverter;

    private PaymentModel paymentModel;
    private PaymentItemModel paymentItemModel;
    private SaleModel saleModel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentItemModel = PaymentItemModel.builder()
                .paymentId(UUID.randomUUID())
                .paymentValue(BigDecimal.valueOf(100.00))
                .build();


        paymentModel = PaymentModel.builder()
                .clientId(UUID.randomUUID())
                .paymentItems(List.of(paymentItemModel))
                .build();


        saleModel = SaleModel.builder().value(BigDecimal.valueOf(100.00)).build();
    }

    @Test
    void testExecuteWithValidData() throws JsonProcessingException {
        when(sellerRepositoryGateway.existsById(any(UUID.class))).thenReturn(true);
        when(saleRepositoryGateway.existsById(any(UUID.class))).thenReturn(true);
        when(saleRepositoryGateway.findById(any(UUID.class))).thenReturn(saleModel);
        when(paymentRepositoryGateway.save(any(PaymentItemModel.class))).thenReturn(paymentItemModel);

        PaymentModel result = confirmPaymentUseCase.execute(paymentModel);

        verify(sellerRepositoryGateway).existsById(paymentModel.getClientId());
        verify(saleRepositoryGateway).existsById(paymentItemModel.getPaymentId());
        verify(saleRepositoryGateway).findById(paymentItemModel.getPaymentId());
        verify(paymentRepositoryGateway).save(paymentItemModel);

        assertNotNull(result);
        assertEquals(1, result.getPaymentItems().size());
    }

    @Test
    void testExecuteWhenSellerNotFound() {
        when(sellerRepositoryGateway.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(SellerNotFoundException.class, () -> confirmPaymentUseCase.execute(paymentModel));

        verify(saleRepositoryGateway, never()).existsById(any(UUID.class));
        verify(paymentRepositoryGateway, never()).save(any(PaymentItemModel.class));
        verify(amazonSQS, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void testExecuteWhenSaleNotFound() {
        when(sellerRepositoryGateway.existsById(any(UUID.class))).thenReturn(true);
        when(saleRepositoryGateway.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(SaleNotFoundException.class, () -> confirmPaymentUseCase.execute(paymentModel));

        verify(paymentRepositoryGateway, never()).save(any(PaymentItemModel.class));
        verify(amazonSQS, never()).sendMessage(any(SendMessageRequest.class));
    }

    @Test
    void testExecuteWithValidDataAndQueue() throws JsonProcessingException {
        when(sellerRepositoryGateway.existsById(any(UUID.class))).thenReturn(true);
        when(saleRepositoryGateway.existsById(any(UUID.class))).thenReturn(true);
        when(saleRepositoryGateway.findById(any(UUID.class))).thenReturn(saleModel);
        when(paymentRepositoryGateway.save(any(PaymentItemModel.class))).thenReturn(paymentItemModel);

        when(paymentConverter.convertToString(any(PaymentItemModel.class))).thenReturn("message-body");

        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executorService).execute(any(Runnable.class));

        PaymentModel result = confirmPaymentUseCase.execute(paymentModel);

        verify(sellerRepositoryGateway).existsById(paymentModel.getClientId());
        verify(saleRepositoryGateway).existsById(paymentItemModel.getPaymentId());
        verify(saleRepositoryGateway).findById(paymentItemModel.getPaymentId());
        verify(paymentRepositoryGateway).save(paymentItemModel);

        ArgumentCaptor<SendMessageRequest> captor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(amazonSQS).sendMessage(captor.capture());
        SendMessageRequest capturedRequest = captor.getValue();
        assertEquals("aws.url.total", capturedRequest.getQueueUrl());
        assertEquals("message-body", capturedRequest.getMessageBody());

        assertNotNull(result);
        assertEquals(1, result.getPaymentItems().size());
    }
}
