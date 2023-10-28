package com.github.gustavoflor.grpc.interceptor;

import io.grpc.*;

import java.util.concurrent.ThreadLocalRandom;

import static com.github.gustavoflor.grpc.util.ContextUtil.USER_ID_KEY;

public class AuthInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        final var auth = metadata.get(AUTHORIZATION_KEY);
        if (auth == null || !auth.equals("Basic user:pass")) {
            denyCall(serverCall, metadata);
            return serverCallHandler.startCall(serverCall, metadata);
        }
        final var userId = ThreadLocalRandom.current().nextInt(1, 10);
        final var context = Context.current().withValue(USER_ID_KEY, userId);
        System.out.printf("Authorized call for user with ID equals to %s.%n", userId);
        return Contexts.interceptCall(context, serverCall, metadata, serverCallHandler);
    }

    private <ReqT, RespT> void denyCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
        final var status = Status.UNAUTHENTICATED.withDescription("Invalid token");
        serverCall.close(status, metadata);
    }

}
