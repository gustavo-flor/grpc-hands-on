package com.github.gustavoflor.grpc.interceptor;

import io.grpc.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class DeadlineInterceptor implements ClientInterceptor {

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> methodDescriptor, CallOptions callOptions, Channel channel) {
        final var deadline = Deadline.after(10000, MILLISECONDS);
        return channel.newCall(methodDescriptor, callOptions.withDeadline(deadline));
    }

}
