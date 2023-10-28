package com.github.gustavoflor.grpc.util;

import io.grpc.Context;

public class ContextUtil {

    public static final Context.Key<Integer> USER_ID_KEY = Context.key("user-id-key");

    private ContextUtil() {
    }

}
