package com.github.gustavoflor.grpc;

import com.github.gustavoflor.grpc.repository.BalanceRepository;
import com.github.gustavoflor.grpc.service.BankService;
import com.github.gustavoflor.grpc.service.TransferService;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        final var balanceRepository = new BalanceRepository();
        final var port = 9090;
        final var server = ServerBuilder.forPort(port)
            .addService(new BankService(balanceRepository))
            .addService(new TransferService(balanceRepository))
            .build();

        server.start();
        System.out.printf("gRPC server is running at port %s.%n", port);

        server.awaitTermination();
    }

}
