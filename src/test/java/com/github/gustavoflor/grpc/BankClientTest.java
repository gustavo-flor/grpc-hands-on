package com.github.gustavoflor.grpc;

import com.github.gustavoflor.grpc.unary.protobuf.BalanceCheckRequest;
import com.github.gustavoflor.grpc.unary.protobuf.BankServiceGrpc;
import com.github.gustavoflor.grpc.unary.protobuf.Currency;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BankClientTest {

    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    @BeforeAll
    public void setUp() {
        final var managedChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
            .usePlaintext()
            .build();

        blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
    }

    @Test
    void test() {
        List.of(1L, 6L, 11L, 45L).forEach(accountNumber -> {
            final var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .build();

            final var balance = blockingStub.getBalance(request);

            assertEquals(Currency.USD, balance.getCurrency());
            System.out.println(balance);
        });
    }

}
