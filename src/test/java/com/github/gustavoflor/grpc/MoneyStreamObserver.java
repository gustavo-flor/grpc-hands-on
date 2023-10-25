package com.github.gustavoflor.grpc;

import com.github.gustavoflor.grpc.protobuf.Money;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class MoneyStreamObserver implements StreamObserver<Money> {

    private final CountDownLatch countDownLatch;

    private double moneyReceived;

    public MoneyStreamObserver(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onNext(final Money money) {
        final var message = String.format("Money received, an amount of %s", money.getValue());
        System.out.println(message);
        moneyReceived += money.getValue();
    }

    @Override
    public void onError(final Throwable throwable) {
        throwable.printStackTrace();
        countDownLatch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Stream completed!");
        countDownLatch.countDown();
    }

    public double getReceived() {
        return moneyReceived;
    }
}
