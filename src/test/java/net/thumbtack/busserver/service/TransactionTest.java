package net.thumbtack.busserver.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import net.thumbtack.busserver.MaintaskApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MaintaskApplication.class)
public class TransactionTest {

    @Test
    @Transactional
    public void givenTransactional_whenCheckingForActiveTransaction_thenReceiveTrue() {
        assertTrue(TransactionSynchronizationManager.isActualTransactionActive());
    }

    @Test
    public void givenTransactional_whenCheckingForActiveTransaction_thenReceiveFalse() {
        assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
    }


    

}
