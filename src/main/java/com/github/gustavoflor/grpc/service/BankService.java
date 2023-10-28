package com.github.gustavoflor.grpc.service;

import com.github.gustavoflor.grpc.observer.DepositStreamObserver;
import com.github.gustavoflor.grpc.protobuf.*;
import com.github.gustavoflor.grpc.repository.BalanceRepository;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private final BalanceRepository balanceRepository;

    public BankService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        final var accountNumber = request.getAccountNumber();
        System.out.printf("Received request for %s.%n", accountNumber);
        setTimeout(1000);

        final var balance = balanceRepository.findBalanceByAccountNumber(accountNumber);

        responseObserver.onNext(balance);
        responseObserver.onCompleted();
    }

    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
        final var accountNumber = request.getAccountNumber();
        final var amount = request.getAmount();
        final var balance = balanceRepository.findBalanceByAccountNumber(accountNumber);

        if (amount < 10 || amount % 10 != 0) {
            final var message = "Invalid input, amount must be a multiple of 10 and greater than 10";
            final var status = Status.INVALID_ARGUMENT.withDescription(message);
            responseObserver.onError(status.asException());
            return;
        }

        if (balance.getValue() < amount) {
            final var message = String.format("Insufficient balance. You have only %s %s.", balance.getValue(), balance.getCurrency());
            final var status = Status.FAILED_PRECONDITION.withDescription(message);
            responseObserver.onError(status.asException());
            return;
        }

        for (int index = 0; index < amount / 10; index++) {
            setTimeout(3000);
            if (Context.current().isCancelled()) {
                break;
            }
            final var money = Money.newBuilder()
                .setValue(10)
                .build();
            balanceRepository.debit(accountNumber, money.getValue());
            responseObserver.onNext(money);
        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<DepositRequest> deposit(StreamObserver<Balance> responseObserver) {
        return new DepositStreamObserver(balanceRepository, responseObserver);
    }

    private void setTimeout(long millis) {
        sleepUninterruptibly(millis, MILLISECONDS);
    }

}
