package com.github.gustavoflor.grpc;

import com.github.gustavoflor.grpc.repository.BalanceRepository;
import com.github.gustavoflor.grpc.service.BankService;
import com.github.gustavoflor.grpc.service.TransferService;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class Application {

    private static final int PORT = Integer.parseInt(System.getProperty("server.port", "9090"));

    public static void main(String[] args) throws IOException, InterruptedException {
        final var balanceRepository = new BalanceRepository();
        final var server = ServerBuilder.forPort(PORT)
            .addService(new BankService(balanceRepository))
            .addService(new TransferService(balanceRepository))
            .build();

        server.start();
        System.out.printf("gRPC server is running at port %s.%n", PORT);

        server.awaitTermination();
    }

}
