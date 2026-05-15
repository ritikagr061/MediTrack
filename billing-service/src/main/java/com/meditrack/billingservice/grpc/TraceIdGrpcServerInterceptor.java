package com.meditrack.billingservice.grpc;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@GrpcGlobalServerInterceptor
public class TraceIdGrpcServerInterceptor implements ServerInterceptor {

    private static final String TRACE_ID_HEADER = "x-trace-id";
    private static final String TRACE_ID_MDC_KEY = "traceId";
    private static final Metadata.Key<String> TRACE_ID_METADATA_KEY =
            Metadata.Key.of(TRACE_ID_HEADER, Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {
        String traceId = headers.get(TRACE_ID_METADATA_KEY);
        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onMessage(ReqT message) {
                runWithTraceId(traceId, () -> super.onMessage(message));
            }

            @Override
            public void onHalfClose() {
                runWithTraceId(traceId, super::onHalfClose);
            }

            @Override
            public void onCancel() {
                runWithTraceId(traceId, super::onCancel);
            }

            @Override
            public void onComplete() {
                runWithTraceId(traceId, super::onComplete);
            }

            @Override
            public void onReady() {
                runWithTraceId(traceId, super::onReady);
            }
        };
    }

    private void runWithTraceId(String traceId, Runnable runnable) {
        if (traceId != null && !traceId.isBlank()) {
            MDC.put(TRACE_ID_MDC_KEY, traceId);
        }

        try {
            runnable.run();
        } finally {
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }
}
