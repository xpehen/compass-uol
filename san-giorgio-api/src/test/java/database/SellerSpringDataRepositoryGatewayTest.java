package database;

import br.com.desafio.domain.repository.SellerRepository;
import br.com.desafio.exception.ErrorToAccessDatabaseException;
import br.com.desafio.infrastructure.database.SellerSpringDataRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerSpringDataRepositoryGatewayTest {

    @InjectMocks
    private SellerSpringDataRepositoryGateway sellerSpringDataRepositoryGateway;

    @Mock
    private SellerRepository sellerRepository;

    private UUID clientId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientId = UUID.randomUUID();
    }

    @Test
    void testExistsByIdSuccess() {
        when(sellerRepository.existsById(any(UUID.class))).thenReturn(true);

        boolean result = sellerSpringDataRepositoryGateway.existsById(clientId);

        verify(sellerRepository).existsById(clientId);
        assertTrue(result);
    }

    @Test
    void testExistsByIdThrowsErrorToAccessDatabaseException() {
        when(sellerRepository.existsById(any(UUID.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(ErrorToAccessDatabaseException.class, () -> sellerSpringDataRepositoryGateway.existsById(clientId));

        verify(sellerRepository).existsById(clientId);
    }
}
