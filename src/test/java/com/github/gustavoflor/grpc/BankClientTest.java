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

    private BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setUp() {
        final var managedChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
            .usePlaintext()
            .build();

        bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        bankServiceStub = BankServiceGrpc.newStub(managedChannel);
    }

    @Test
    void testBlockingGetBalance() {
        List.of(1L, 6L, 11L, 45L).forEach(accountNumber -> {
            final var expectedValue = 10.0 * accountNumber;
            final var request = BalanceCheckRequest.newBuilder()
                .setAccountNumber(accountNumber)
                .build();

            final var balance = bankServiceBlockingStub.getBalance(request);

            assertEquals(Currency.USD, balance.getCurrency());
            assertEquals(expectedValue, balance.getValue());
        });
    }

    @Test
    void testBlockingWithdraw() {
        final var request = WithdrawRequest.newBuilder()
            .setAccountNumber(7)
            .setAmount(40)
            .build();

        final var responses = new AtomicInteger();
        bankServiceBlockingStub.withdraw(request).forEachRemaining(money -> {
            responses.getAndIncrement();
            assertEquals(10, money.getValue());
        });
        assertEquals(request.getAmount() / 10, responses.get());
    }

    @Test
    void testWithdraw() {
        final var request = WithdrawRequest.newBuilder()
            .setAccountNumber(7)
            .setAmount(40)
            .build();

        bankServiceStub.withdraw(request, new MoneyStreamObserver());
    }

}
