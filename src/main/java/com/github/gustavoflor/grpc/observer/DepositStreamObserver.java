package com.github.gustavoflor.grpc.observer;

import com.github.gustavoflor.grpc.protobuf.Balance;
import com.github.gustavoflor.grpc.protobuf.DepositRequest;
import com.github.gustavoflor.grpc.repository.BalanceRepository;
import io.grpc.stub.StreamObserver;

public class DepositStreamObserver implements StreamObserver<DepositRequest> {

    private final BalanceRepository balanceRepository;
    private final StreamObserver<Balance> balanceStreamObserver;

    private long accountNumber;

    public DepositStreamObserver(final BalanceRepository balanceRepository,
                                 final StreamObserver<Balance> balanceStreamObserver) {
        this.balanceRepository = balanceRepository;
        this.balanceStreamObserver = balanceStreamObserver;
    }

    @Override
    public void onNext(DepositRequest depositRequest) {
        final var amount = depositRequest.getAmount();

        balanceRepository.credit(depositRequest.getAccountNumber(), amount);
        accountNumber = depositRequest.getAccountNumber();
        System.out.printf("Received a deposit of %s for %s.%n", amount, accountNumber);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        final var balance = balanceRepository.findBalanceByAccountNumber(accountNumber);
        balanceStreamObserver.onNext(balance);
        balanceStreamObserver.onCompleted();
    }

}
