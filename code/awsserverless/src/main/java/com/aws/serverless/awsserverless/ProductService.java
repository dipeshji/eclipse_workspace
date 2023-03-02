package com.aws.serverless.awsserverless;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.aws.serverless.awsserverless.entity.Product;

public class ProductService {
	private DynamoDBMapper dynamoDbMapper;
	private String jsonBody;

	public ProductService() {
		this.jsonBody = null;
	}

	public APIGatewayProxyResponseEvent saveProduct(final APIGatewayProxyRequestEvent apiGatewayRequest,
			final Context context) {
		this.initDynamoDb();
		final Product product = Utility.convertStringToObject(apiGatewayRequest.getBody(), context);
		this.dynamoDbMapper.save((Object) product);
		this.jsonBody = Utility.convertObjectToString(product, context);
		context.getLogger().log("Product data saved to database: " + this.jsonBody);
		return this.createApiResponse(this.jsonBody, 201, Utility.createHeader());
	}

	public APIGatewayProxyResponseEvent updateProduct(final APIGatewayProxyRequestEvent apiGatewayRequest,
			final Context context) {
		this.initDynamoDb();
		final int id = Integer.parseInt(apiGatewayRequest.getPathParameters().get("id"));
		final Product product = (Product) this.dynamoDbMapper.load((Class) Product.class, (Object) id);
		this.jsonBody = Utility.convertObjectToString(product, context);
		if (product != null) {
			final DynamoDBMapperConfig dynamoDBMapperConfig = new DynamoDBMapperConfig.Builder()
					.withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
					.withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE).build();
			this.dynamoDbMapper.save((Object) product, dynamoDBMapperConfig);
			context.getLogger().log("Product have been updated: " + this.jsonBody);
			return this.createApiResponse(this.jsonBody, 204, Utility.createHeader());
		}
		this.dynamoDbMapper.save((Object) product);
		context.getLogger().log("Product data saved to database: " + this.jsonBody);
		return this.createApiResponse(this.jsonBody, 201, Utility.createHeader());
	}

	public APIGatewayProxyResponseEvent getProductById(final APIGatewayProxyRequestEvent apiGatewayRequest,
			final Context context) {
		this.initDynamoDb();
		final int id = Integer.parseInt(apiGatewayRequest.getPathParameters().get("id"));
		final Product product = (Product) this.dynamoDbMapper.load((Class) Product.class, (Object) id);
		if (product != null) {
			this.jsonBody = Utility.convertObjectToString(product, context);
			context.getLogger().log("Get employee by id: " + this.jsonBody);
			return this.createApiResponse(this.jsonBody, 200, Utility.createHeader());
		}
		context.getLogger().log("Product not found with product id: " + id);
		this.jsonBody = "Product not found with product id: " + id;
		return this.createApiResponse(this.jsonBody, 400, Utility.createHeader());
	}

	public APIGatewayProxyResponseEvent getProducts(final APIGatewayProxyRequestEvent apiGatewayRequest,
			final Context context) {
		this.initDynamoDb();
		final List<Product> product = (List<Product>) this.dynamoDbMapper.scan((Class) Product.class,
				new DynamoDBScanExpression());
		this.jsonBody = Utility.convertListOfObjectToString((List) product, context);
		context.getLogger().log("List of products: " + this.jsonBody);
		return this.createApiResponse(this.jsonBody, 200, Utility.createHeader());
	}

	public APIGatewayProxyResponseEvent deleteProductById(final APIGatewayProxyRequestEvent apiGatewayRequest,
			final Context context) {
		this.initDynamoDb();
		final int id = Integer.parseInt(apiGatewayRequest.getPathParameters().get("id"));
		final Product product = (Product) this.dynamoDbMapper.load((Class) Product.class, (Object) id);
		if (product != null) {
			this.dynamoDbMapper.delete((Object) product);
			context.getLogger().log("Product with product id :" + id + "have been deleted");
			this.jsonBody = "Product with product id :" + id + "have been deleted";
			return this.createApiResponse(this.jsonBody, 200, Utility.createHeader());
		}
		this.jsonBody = "No product found with product id: " + id;
		return this.createApiResponse(this.jsonBody, 400, Utility.createHeader());
	}

	private void initDynamoDb() {
		final AmazonDynamoDB client = (AmazonDynamoDB) AmazonDynamoDBClientBuilder.standard().build();
		this.dynamoDbMapper = new DynamoDBMapper(client);
	}

	private APIGatewayProxyResponseEvent createApiResponse(final String body, final int statusCode,
			final Map<String, String> headers) {
		final APIGatewayProxyResponseEvent event = new APIGatewayProxyResponseEvent();
		event.setBody(body);
		event.setHeaders((Map) headers);
		event.setStatusCode(Integer.valueOf(statusCode));
		return event;
	}
}
