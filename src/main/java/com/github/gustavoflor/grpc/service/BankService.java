package com.github.gustavoflor.grpc.service;

import com.github.gustavoflor.grpc.observer.DepositStreamObserver;
import com.github.gustavoflor.grpc.protobuf.*;
import com.github.gustavoflor.grpc.repository.BalanceRepository;
import io.grpc.Context;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;

import static com.github.gustavoflor.grpc.util.ContextUtil.USER_ID_KEY;
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
        final var userId = USER_ID_KEY.get();
        System.out.printf("Received request for %s by %s.%n", accountNumber, userId);
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

        if (amount <= 0) {
            final var metadata = new Metadata();
            final var errorKey = ProtoUtils.keyForProto(WithdrawError.getDefaultInstance());
            final var error = WithdrawError.newBuilder()
                .setBalance(balance)
                .setStatus(ErrorStatus.AMOUNT_SHOULD_BE_POSITIVE)
                .build();
            metadata.put(errorKey, error);
            final var message = "Invalid input, amount must be a positive value";
            final var exception = Status.INVALID_ARGUMENT
                .withDescription(message)
                .asRuntimeException(metadata);
            responseObserver.onError(exception);
            return;
        }

        if (amount < 10 || amount % 10 != 0) {
            final var metadata = new Metadata();
            final var errorKey = ProtoUtils.keyForProto(WithdrawError.getDefaultInstance());
            final var error = WithdrawError.newBuilder()
                .setBalance(balance)
                .setStatus(ErrorStatus.ONLY_TEN_MULTIPLES)
                .build();
            metadata.put(errorKey, error);
            final var message = "Invalid input, amount must be a multiple of 10";
            final var exception = Status.INVALID_ARGUMENT
                .withDescription(message)
                .asRuntimeException(metadata);
            responseObserver.onError(exception);
            return;
        }

        if (balance.getValue() < amount) {
            final var metadata = new Metadata();
            final var errorKey = ProtoUtils.keyForProto(WithdrawError.getDefaultInstance());
            final var error = WithdrawError.newBuilder()
                .setBalance(balance)
                .setStatus(ErrorStatus.INSUFFICIENT_BALANCE)
                .build();
            metadata.put(errorKey, error);
            final var message = String.format("Insufficient balance. You have only %s %s.", balance.getValue(), balance.getCurrency());
            final var exception = Status.FAILED_PRECONDITION
                .withDescription(message)
                .asRuntimeException(metadata);
            responseObserver.onError(exception);
            return;
        }

        for (int index = 0; index < amount / 10; index++) {
            setTimeout(1000);
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
