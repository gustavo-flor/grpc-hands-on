package com.github.gustavoflor.grpc.service;

import com.github.gustavoflor.grpc.protobuf.Balance;
import com.github.gustavoflor.grpc.protobuf.BalanceCheckRequest;
import com.github.gustavoflor.grpc.protobuf.BankServiceGrpc;
import com.github.gustavoflor.grpc.repository.BalanceRepository;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private final BalanceRepository balanceRepository = new BalanceRepository();

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {
        final var accountNumber = request.getAccountNumber();

        balanceRepository.findBalanceByAccountNumber(accountNumber)
            .ifPresent(balance -> {
                responseObserver.onNext(balance);
                responseObserver.onCompleted();
            });
    }
}
