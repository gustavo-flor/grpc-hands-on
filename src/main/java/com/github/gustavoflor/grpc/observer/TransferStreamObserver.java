package com.github.gustavoflor.grpc.observer;

import com.github.gustavoflor.grpc.protobuf.Account;
import com.github.gustavoflor.grpc.protobuf.TransferRequest;
import com.github.gustavoflor.grpc.protobuf.TransferResponse;
import com.github.gustavoflor.grpc.protobuf.TransferStatus;
import com.github.gustavoflor.grpc.repository.BalanceRepository;
import io.grpc.stub.StreamObserver;

public class TransferStreamObserver implements StreamObserver<TransferRequest> {

    private final StreamObserver<TransferResponse> responseObserver;
    private final BalanceRepository balanceRepository;

    public TransferStreamObserver(StreamObserver<TransferResponse> responseObserver,
                                  BalanceRepository balanceRepository) {
        this.responseObserver = responseObserver;
        this.balanceRepository = balanceRepository;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {
        final var senderAccountNumber = transferRequest.getSenderAccountNumber();
        final var receiverAccountNumber = transferRequest.getReceiverAccountNumber();
        final var amount = transferRequest.getAmount();
        final var senderBalance = balanceRepository.findBalanceByAccountNumber(senderAccountNumber);

        var status = TransferStatus.FAILED;

        if (senderBalance.getValue() >= amount && senderAccountNumber != receiverAccountNumber) {
            balanceRepository.debit(senderAccountNumber, amount);
            balanceRepository.credit(receiverAccountNumber, amount);
            status = TransferStatus.SUCCESS;
        }

        final var senderAccount = Account.newBuilder()
            .setNumber(senderAccountNumber)
            .setBalance(balanceRepository.findBalanceByAccountNumber(senderAccountNumber).getValue())
            .build();
        final var receiverAccount = Account.newBuilder()
            .setNumber(receiverAccountNumber)
            .setBalance(balanceRepository.findBalanceByAccountNumber(receiverAccountNumber).getValue())
            .build();
        final var transferResponse = TransferResponse.newBuilder()
            .setStatus(status)
            .addAccounts(senderAccount)
            .addAccounts(receiverAccount)
            .build();
        responseObserver.onNext(transferResponse);
        System.out.printf("Transfer request for %s to %s with value of %s with %s.%n", senderAccountNumber, receiverAccountNumber, amount, status);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onCompleted() {
        responseObserver.onCompleted();
        System.out.println("Stream completed!");
    }

}
