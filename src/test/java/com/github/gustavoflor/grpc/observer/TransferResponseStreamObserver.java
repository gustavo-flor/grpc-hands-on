package com.github.gustavoflor.grpc.observer;

import com.github.gustavoflor.grpc.protobuf.TransferResponse;
import com.github.gustavoflor.grpc.protobuf.TransferStatus;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TransferResponseStreamObserver implements StreamObserver<TransferResponse> {

    private final CountDownLatch countDownLatch;
    private final List<TransferStatus> statuses = new ArrayList<>();

    public TransferResponseStreamObserver(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onNext(TransferResponse transferResponse) {
        final var sender = transferResponse.getAccounts(0);
        final var receiver = transferResponse.getAccounts(1);
        final var status = transferResponse.getStatus();
        final var message = "Transfer from %s to %s with status equals to %s.%n";
        System.out.printf(message, sender.getNumber(), receiver.getNumber(), status);
        statuses.add(status);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        countDownLatch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println("Stream completed!");
        countDownLatch.countDown();
    }

    public List<TransferStatus> getStatuses() {
        return statuses;
    }
}
