package com.github.gustavoflor.grpc.observer;

import com.github.gustavoflor.grpc.protobuf.Balance;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceStreamObserver implements StreamObserver<Balance> {

    private final CountDownLatch countDownLatch;
    private Balance balance;

    public BalanceStreamObserver(final CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onNext(final Balance balance) {
        this.balance = balance;
        System.out.printf("Total balance equals to %s %s.%n", balance.getValue(), balance.getCurrency());
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("Deposit completed!");
        countDownLatch.countDown();
    }

    public Balance getBalance() {
        return balance;
    }
}
