package com.github.gustavoflor.grpc.unary.repository;

import com.github.gustavoflor.grpc.unary.protobuf.Balance;

import java.util.Map;
import java.util.Optional;

public class BalanceRepository {

    private static final Map<Long, Balance> BALANCES = Map.of(
        1L, Balance.newBuilder().setValue(100.00).build(),
        6L, Balance.newBuilder().setValue(37.50).build(),
        11L, Balance.newBuilder().setValue(0.57).build(),
        45L, Balance.newBuilder().setValue(0.00).build()
    );

    public Optional<Balance> findBalanceByAccountNumber(long accountNumber) {
        return Optional.ofNullable(BALANCES.get(accountNumber));
    }

}
