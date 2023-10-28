package com.github.gustavoflor.grpc;

import com.github.gustavoflor.grpc.observer.TransferResponseStreamObserver;
import com.github.gustavoflor.grpc.protobuf.TransferRequest;
import com.github.gustavoflor.grpc.protobuf.TransferServiceGrpc;
import com.github.gustavoflor.grpc.protobuf.TransferStatus;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

import static com.github.gustavoflor.grpc.util.MetadataUtil.getMetadata;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransferClientTest {

    private TransferServiceGrpc.TransferServiceStub transferServiceStub;

    @BeforeAll
    public void setUp() {
        final var managedChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
            .intercept(MetadataUtils.newAttachHeadersInterceptor(getMetadata()))
            .usePlaintext()
            .build();

        transferServiceStub = TransferServiceGrpc.newStub(managedChannel);
    }

    @Test
    void testTransfer() throws InterruptedException {
        final var countDownLatch = new CountDownLatch(1);
        final var responseObserver = new TransferResponseStreamObserver(countDownLatch);

        final var requestObserver = transferServiceStub.transfer(responseObserver);

        for (int i = 0; i < 4; i++) {
            final var transferRequest = TransferRequest.newBuilder()
                .setAmount(30)
                .setSenderAccountNumber(10)
                .setReceiverAccountNumber(1)
                .build();
            requestObserver.onNext(transferRequest);
        }
        requestObserver.onCompleted();

        countDownLatch.await();
        final var statuses = responseObserver.getStatuses();
        assertEquals(4, statuses.size());
        assertEquals(TransferStatus.SUCCESS, statuses.get(0));
        assertEquals(TransferStatus.SUCCESS, statuses.get(1));
        assertEquals(TransferStatus.SUCCESS, statuses.get(2));
        assertEquals(TransferStatus.FAILED, statuses.get(3));
    }

}
