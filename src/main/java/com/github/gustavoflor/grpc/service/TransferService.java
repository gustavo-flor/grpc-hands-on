package com.github.gustavoflor.grpc.service;

import com.github.gustavoflor.grpc.observer.TransferStreamObserver;
import com.github.gustavoflor.grpc.protobuf.TransferRequest;
import com.github.gustavoflor.grpc.protobuf.TransferResponse;
import com.github.gustavoflor.grpc.protobuf.TransferServiceGrpc;
import com.github.gustavoflor.grpc.repository.BalanceRepository;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {

    private final BalanceRepository balanceRepository;

    public TransferService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
        return new TransferStreamObserver(responseObserver, balanceRepository);
    }

}
