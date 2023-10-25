package com.github.gustavoflor.grpc;

import com.github.gustavoflor.grpc.protobuf.*;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testGetBalance() {
        List.of(1L, 6L, 11L, 45L).forEach(accountNumber -> {
            final var expectedValue = 10.0 * accountNumber;
            final var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .build();

            final var balance = blockingStub.getBalance(request);

            assertEquals(Currency.USD, balance.getCurrency());
            assertEquals(expectedValue, balance.getValue());
        });
    }

    @Test
    void testWithdraw() {
        final var request = WithdrawRequest.newBuilder()
            .setAccountNumber(7)
            .setAmount(40)
            .build();

        final var responses = new AtomicInteger();
        blockingStub.withdraw(request).forEachRemaining(money -> {
            responses.getAndIncrement();
            assertEquals(10, money.getValue());
        });
        assertEquals(request.getAmount() / 10, responses.get());
    }

}
