package com.github.gustavoflor.grpc.repository;

import com.github.gustavoflor.grpc.protobuf.Balance;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BalanceRepository {

    private static final Map<Long, Balance> BALANCES = new HashMap<>();

    public Balance findBalanceByAccountNumber(final long accountNumber) {
        final var balance = BALANCES.get(accountNumber);
        if (balance == null) {
            return register(accountNumber);
        }
        return balance;
    }

    public void debit(long accountNumber, double value) {
        final var balance = findBalanceByAccountNumber(accountNumber);
        final var newBalance = Balance.newBuilder()
            .setValue(balance.getValue() - value)
            .build();
        BALANCES.put(accountNumber, newBalance);
    }

    private Balance register(long accountNumber) {
        final var balance = Balance.newBuilder()
            .setValue(accountNumber * 10.0)
            .build();
        BALANCES.put(accountNumber, balance);
        return balance;
    }

}
