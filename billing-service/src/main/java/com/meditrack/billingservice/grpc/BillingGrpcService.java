package com.meditrack.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceGrpc.BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest,
                                     StreamObserver<BillingResponse> responseObserver){
        //StreamObserver is a powerfull concept in grpc which enables to and fro communication between client and server, also multiple responses could be sent and recieved
        log.info("createBillingAccount request recieved {}", billingRequest.toString());
        
        //business logic + save to db
        BillingResponse response =  BillingResponse.newBuilder()
                .setStatus("SUCCESS")
                .setAccountId("AGC123").build();

        responseObserver.onNext(response); //sends the first response to the client
        responseObserver.onCompleted(); // ends the conversation
    }
}
