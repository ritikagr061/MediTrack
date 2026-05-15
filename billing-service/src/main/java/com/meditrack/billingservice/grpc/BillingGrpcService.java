package com.meditrack.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import com.meditrack.billingservice.dto.BillingAccountCreateRequestDTO;
import com.meditrack.billingservice.dto.BillingAccountResponseDTO;
import com.meditrack.billingservice.service.BillingService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);
    private final BillingService billingService;

    public BillingGrpcService(BillingService billingService) {
        this.billingService = billingService;
    }

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseObserver){
        //StreamObserver is a powerfull concept in grpc which enables to and fro communication between client and server, also multiple responses could be sent and recieved
        log.info(
                "Received create billing account gRPC request",
                kv("patient.id", billingRequest.getPatientId()),
                kv("rpc.system", "grpc"),
                kv("rpc.method", "CreateBillingAccount")
        );
        
        BillingAccountCreateRequestDTO request = new BillingAccountCreateRequestDTO();
        request.setPatientId(UUID.fromString(billingRequest.getPatientId()));
        request.setPatientName(billingRequest.getName());
        request.setPatientEmail(billingRequest.getEmail());
        BillingAccountResponseDTO account = billingService.createBillingAccount(request);

        BillingResponse response =  BillingResponse.newBuilder()
                .setStatus("SUCCESS")
                .setAccountId(account.getId().toString()).build();

        log.info(
                "Completed create billing account gRPC request",
                kv("patient.id", billingRequest.getPatientId()),
                kv("billing.account.id", account.getId()),
                kv("rpc.system", "grpc"),
                kv("rpc.method", "CreateBillingAccount"),
                kv("rpc.grpc.status_code", response.getStatus())
        );

        responseObserver.onNext(response); //sends the first response to the client
        responseObserver.onCompleted(); // ends the conversation
    }
}
