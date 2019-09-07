package com.imberda.stockprice.service;

import com.google.rpc.Code;
import com.google.rpc.Status;
import com.imberda.stockprices.v1.StockPrice;
import com.imberda.stockprices.v1.StockPriceRequest;
import com.imberda.stockprices.v1.StockPriceResponse;
import com.imberda.stockprices.v1.StockPricesGrpc;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.StatusProto;
import io.grpc.protobuf.services.ProtoReflectionService;
import io.grpc.services.HealthStatusManager;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.protobuf.util.Timestamps.fromMillis;
import static java.lang.System.currentTimeMillis;

public class StockPriceServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(StockPriceServiceApplication.class);

    public static void main(String[] args) throws InterruptedException, IOException {

        HealthStatusManager manager = new HealthStatusManager();

        var server = ServerBuilder.forPort(8081)
                .addService(new StockPriceService())
                .addService(ProtoReflectionService.newInstance())
                .addService(manager.getHealthService())
                .build();

        server.start();

        server.awaitTermination();
    }

    public static class StockPriceService extends StockPricesGrpc.StockPricesImplBase {

        private final Map<String, Double> currentStockPrices = new HashMap<>() {{
            put("AAA", 123.45);
            put("BBB", 79.29);
            put("CCC", 57.45);
        }};

        @Override
        public void requestPrice(StockPriceRequest request, StreamObserver<StockPriceResponse> responseObserver) {

            logger.info("Received request for price of stock with symbol: {}", request.getSymbol());

            var currentPrice = currentStockPrices.get(request.getSymbol());

            if(currentPrice != null){

                logger.info("Returning price: {}", currentPrice);

                var stockPriceResponse = StockPriceResponse.newBuilder()
                        .setTimestamp(fromMillis(currentTimeMillis()))
                        .setStockPrice(StockPrice.newBuilder()
                                .setSymbol(request.getSymbol())
                                .setPrice(currentPrice)
                                .build())
                        .build();

                responseObserver.onNext(stockPriceResponse);
                responseObserver.onCompleted();

            } else {

                logger.warn("No price found. Returning error.");

                var status = Status.newBuilder()
                        .setCode(Code.NOT_FOUND.getNumber())
                        .setMessage("Stock price not found for symbol: " + request.getSymbol())
                        .build();

                responseObserver.onError(StatusProto.toStatusRuntimeException(status));
            }

        }
    }
}
