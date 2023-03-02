package com.aws.serverless.awsserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
	ProductService productService;

	public LambdaHandler() {
		this.productService = new ProductService();
	}

	public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGateWayRequest,
			final Context context) {
		final String httpMethod = apiGateWayRequest.getHttpMethod();
		switch (httpMethod) {
		case "POST": {
			return this.productService.saveProduct(apiGateWayRequest, context);
		}
		case "GET": {
			if (apiGateWayRequest.getPathParameters() != null) {
				return this.productService.getProductById(apiGateWayRequest, context);
			}
			return this.productService.getProducts(apiGateWayRequest, context);
		}
		case "DELETE": {
			if (apiGateWayRequest.getPathParameters() != null) {
				return this.productService.deleteProductById(apiGateWayRequest, context);
			}
			return this.productService.updateProduct(apiGateWayRequest, context);
		}
		case "PUT": {
			return this.productService.updateProduct(apiGateWayRequest, context);
		}
		default: {
			throw new Error("Unsoportd Operation: " + apiGateWayRequest.getHttpMethod());
		}
		}
	}
}
