package br.com.desafio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PaymentExceptionHandler extends ResponseEntityExceptionHandler {
    private final static String SELLER_NOT_FOUND_EXCEPTION_MESSAGE = "Seller not found";
    private final static String DATABASE_EXCEPTION_MESSAGE = "An error occurs when try access database";
    private final static String PAYMENTE_NOT_FOUND_EXCEPTION_MESSAGE = "Payment not found";
    private final static String SALE_NOT_FOUND_EXCEPTION_MESSAGE = "Sale not found";

    @ExceptionHandler(ErrorToAccessDatabaseException.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        return new ResponseEntity<>(DATABASE_EXCEPTION_MESSAGE + ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public final ResponseEntity<Object> handlePaymentException(PaymentNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(PAYMENTE_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SellerNotFoundException.class)
    public final ResponseEntity<Object> handleSellerException(SellerNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(SELLER_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SaleNotFoundException.class)
    public final ResponseEntity<Object> handleSaleException(SaleNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(SALE_NOT_FOUND_EXCEPTION_MESSAGE, HttpStatus.BAD_REQUEST);
    }


}
