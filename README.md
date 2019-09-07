## GRPC Demo

This project provides a basic demo of GRPC. You can find more details at the [official website](https://grpc.io). 

#### Presentation

Note that this project was part of a tech talk I gave to my team end of 2018. The code and slides used are found within this repositry.

* Slide Deck:
    * [Keynote](https://github.com/imberda/grpc-demo/blob/master/GRPC_Talk.key?raw=true)
    * [PowerPoint](https://github.com/imberda/grpc-demo/blob/master/GRPC_Talk.pptx?raw=true)
    * [PDF](https://github.com/imberda/grpc-demo/blob/master/GRPC_Talk.pptx?raw=true)

#### Project Structure

There are three modules:

* stock-price-api
* stock-price-service
* consumer-service

The API module, provides the proto API specification (messages and service interface) for requesting stock prices for a given ticker symbol. This module has dependencies on and configuration for compiling the proto and generating the GRPC client/server implementations. 

The stock price service module is the implementation of the service and provides an implementations of the service method to return stock prices upon request. So this module is the GRPC server.

The consumer module uses the GRPC client to make stock price requests to the stock price service.

#### Branches

The `master` branch contains the basic demo including a unary client/server example. This demonstrates the three different client implementations (blocking, futures, and callback). It also provides a simple example of error generation and handling. 

The `deadline` branch shows how deadlines for requests can be set and handled.

The `streaming` branch shows an example of bi-directional streaming support. In this example, the client issues a number of stock price requests within the same stream, and the server sends price ticks for the subscribed stocks every few seconds.

#### Instructions

The demo is based on JDK/JRE 10, so you will need that installed to build and run.

To build the project you will need Maven installed (`brew install maven`).

Just use the following command from the project's top-level directory:
```
mvn clean install
```

The best way to run the project is in the IDE, by running the following classes which are the entry points to the client and server respectively:

* `ConsumerServiceApplication`
* `StockPriceServiceApplication`
