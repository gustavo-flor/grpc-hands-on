package com.github.gustavoflor.grpc;

import com.github.gustavoflor.grpc.protobuf.Money;
import io.grpc.stub.StreamObserver;

public class MoneyStreamObserver implements StreamObserver<Money> {

    @Override
    public void onNext(final Money money) {
        final var message = String.format("Money received, an amount of %s", money.getValue());
        System.out.println(message);
    }

    @Override
    public void onError(final Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        System.out.println("Stream completed!");
    }

}
