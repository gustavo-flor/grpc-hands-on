package com.github.gustavoflor.grpc.service;

import com.github.gustavoflor.grpc.protobuf.*;
import com.github.gustavoflor.grpc.repository.BalanceRepository;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private final BalanceRepository balanceRepository = new BalanceRepository();

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        final var accountNumber = request.getAccountNumber();

        final var balance = balanceRepository.findBalanceByAccountNumber(accountNumber);

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        final var accountNumber = request.getAccountNumber();
        final var amount = request.getAmount();

        for (int index = 0; index < amount / 10; index++) {
            final var money = Money.newBuilder()
                .setValue(10)
                .build();
            balanceRepository.debit(accountNumber, money.getValue());
            responseObserver.onNext(money);
            setTimeout(1000);
        }

        responseObserver.onCompleted();
    }

    private void setTimeout(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
