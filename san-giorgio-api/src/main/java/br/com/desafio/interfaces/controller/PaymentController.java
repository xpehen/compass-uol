package br.com.desafio.interfaces.controller;

import br.com.desafio.interfaces.request.PaymentJson;
import br.com.desafio.interfaces.converter.PaymentConverter;
import br.com.desafio.domain.model.PaymentModel;
import br.com.desafio.application.usecase.ConfirmPaymentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/payment")
public class PaymentController {

    private final PaymentConverter paymentConverter;

    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public PaymentJson setPayment(@RequestBody final PaymentJson paymentJsonRequest) {
        PaymentModel paymentModelConverted = paymentConverter.convertRequestToModel(paymentJsonRequest);

        PaymentModel paymentModelResult = confirmPaymentUseCase.execute(paymentModelConverted);

        return paymentConverter.convertModelToResponse(paymentModelResult);
    }
}
