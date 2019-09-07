package com.imberda.consumer.service;

import com.imberda.stockprices.v1.StockPriceRequest;
import com.imberda.stockprices.v1.StockPriceResponse;
import com.imberda.stockprices.v1.StockPricesGrpc;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class ConsumerServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerServiceApplication.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        var channel = ManagedChannelBuilder.forAddress("localhost", 8081)
                .usePlaintext()
                .build();


        // BLOCKING CLIENT EXAMPLE
        var blockingClient = StockPricesGrpc.newBlockingStub(channel);

        var response = blockingClient.requestPrice(StockPriceRequest.newBuilder()
                .setSymbol("AAA")
                .build());

        logger.info("Price of {} is {}", response.getStockPrice().getSymbol(), response.getStockPrice().getPrice());


        // DEAL WITH AN ERROR
        try {
            blockingClient.requestPrice(StockPriceRequest.newBuilder()
                    .setSymbol("BAD")
                    .build());

        } catch (StatusRuntimeException e) {
            logger.error("Got error looking for BAD stock price with status {} and message {}", e.getStatus().getCode(), e.getMessage());
        }


        // FUTURES CLIENT EXAMPLE
        var futureClient = StockPricesGrpc.newFutureStub(channel);

        var futureResponse = futureClient.requestPrice(StockPriceRequest.newBuilder()
                .setSymbol("BBB")
                .build());

        logger.info("Price of {} is {}", futureResponse.get().getStockPrice().getSymbol(), futureResponse.get().getStockPrice().getPrice());


        // CALLBACK STYLE CLIENT EXAMPLE
        var client = StockPricesGrpc.newStub(channel);

        var responseObserver = new StreamObserver<StockPriceResponse>() {

            @Override
            public void onNext(StockPriceResponse stockPriceResponse) {
                logger.info("Price of {} is {}", stockPriceResponse.getStockPrice().getSymbol(), stockPriceResponse.getStockPrice().getPrice());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("Received error", throwable);
            }

            @Override
            public void onCompleted() {
                logger.info("onCompleted");
            }
        };

        client.requestPrice(
                StockPriceRequest.newBuilder().setSymbol("CCC").build(),
                responseObserver);

        // Don't exit immediately
        Thread.sleep(2000);
    }
}
