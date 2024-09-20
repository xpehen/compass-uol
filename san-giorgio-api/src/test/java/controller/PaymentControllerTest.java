package controller;

import br.com.desafio.application.usecase.ConfirmPaymentUseCase;
import br.com.desafio.interfaces.controller.PaymentController;
import br.com.desafio.interfaces.converter.PaymentConverter;
import br.com.desafio.interfaces.request.PaymentJson;
import br.com.desafio.interfaces.request.PaymentItemJson;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.domain.model.PaymentItemModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private ConfirmPaymentUseCase confirmPaymentUseCase;

    @Mock
    private PaymentConverter paymentConverter;

    @InjectMocks
    private PaymentController paymentController;

    @Autowired
    private MockMvc mockMvc;

    private PaymentJson paymentJsonRequest;
    private PaymentModel paymentModel;
    private PaymentJson paymentJsonResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();

        String validClientId = UUID.randomUUID().toString();

        PaymentItemJson paymentItemJson = PaymentItemJson.builder()
                .paymentId(UUID.randomUUID().toString())
                .paymentValue(BigDecimal.valueOf(100.0))
                .build();

        paymentJsonRequest = PaymentJson.builder()
                .clientId(validClientId)
                .paymentItemJsons(Collections.singletonList(paymentItemJson))
                .build();

        PaymentItemModel paymentItemModel = PaymentItemModel.builder()
                .paymentId(UUID.fromString(paymentItemJson.getPaymentId()))
                .paymentValue(BigDecimal.valueOf(100.0))
                .build();

        paymentModel = PaymentModel.builder()
                .clientId(UUID.fromString(validClientId))
                .paymentItems(List.of(paymentItemModel))
                .build();

        paymentJsonResponse = PaymentJson.builder()
                .clientId(validClientId)
                .paymentItemJsons(List.of(paymentItemJson))
                .build();
    }

    @Test
    void testSetPaymentSuccess() throws Exception {
        when(paymentConverter.convertRequestToModel(any(PaymentJson.class))).thenReturn(paymentModel);
        when(confirmPaymentUseCase.execute(any(PaymentModel.class))).thenReturn(paymentModel);
        when(paymentConverter.convertModelToResponse(any(PaymentModel.class))).thenReturn(paymentJsonResponse);

        String requestJson = "{ \"client_id\": \"" + paymentJsonRequest.getClientId() + "\", " +
                "\"payment_items\": [ { \"payment_id\": \"" + paymentJsonRequest.getPaymentItemJsons().get(0).getPaymentId() + "\", " +
                "\"payment_value\": 100.0 } ] }";

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.client_id").value(paymentJsonRequest.getClientId()))
                .andExpect(jsonPath("$.payment_items[0].payment_id").value(paymentJsonRequest.getPaymentItemJsons().get(0).getPaymentId()))
                .andExpect(jsonPath("$.payment_items[0].payment_value").value(100.0));

        verify(paymentConverter).convertRequestToModel(any(PaymentJson.class));
        verify(confirmPaymentUseCase).execute(any(PaymentModel.class));
        verify(paymentConverter).convertModelToResponse(any(PaymentModel.class));
    }
}
