package com.aws.trigger.lambdatrigger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.aws.trigger.lambdatrigger.entity.Product;
import com.google.gson.Gson;

public class S3EventHandler implements RequestHandler<S3Event, String> {
	private static final AmazonS3 s3Client = AmazonS3Client.builder()
			.withCredentials(new DefaultAWSCredentialsProviderChain()).build();

	private DynamoDBMapper dynamoDbMapper;

	@Override
	public String handleRequest(S3Event s3Event, Context context) {
		if (s3Event.getRecords().isEmpty()) {
			context.getLogger().log("No records found");
			return "No records found";
		}
		for (S3EventNotification.S3EventNotificationRecord record : s3Event.getRecords()) {
			String bucketName = record.getS3().getBucket().getName();
			String objectKey = record.getS3().getObject().getKey();

			// 1. we create S3 client
			// 2. invoking GetObject
			// 3. processing the InputStream from S3

			S3Object s3Object = s3Client.getObject(bucketName, objectKey);
			S3ObjectInputStream inputStream = s3Object.getObjectContent();
			ArrayList<Product> products = new ArrayList<>();
			// processing CSV - open CSV, apache CSV
			try (final BufferedReader br = new BufferedReader(
					new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				br.lines().skip(1).forEach((line) -> {
					try {
						JSONObject item = new JSONObject();
						String[] content = line.split(",");
						try {
							item.put("id", Integer.parseInt(content[0]));
							item.put("name", isValidIndex(content, 1) ? content[1] : "");
							item.put("description", isValidIndex(content, 2) ? content[2] : "");
							item.put("price", isValidIndex(content, 3) ? content[3] : "100");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						context.getLogger().log("content: " + item);
						Product product = new Gson().fromJson(item.toString(), Product.class);

						products.add(product);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				initDynamoDB();
				products.forEach((Product p) -> {
					dynamoDbMapper.save(p);
				});
			} catch (IOException e) {
				context.getLogger().log("Error occurred in Lambda:" + e.getMessage());
				return "Error occurred in Lambda";
			}
		}

		return "successfully save data to dynamoDB";
	}

	public static boolean isValidIndex(String[] arr, int index) {
		return index >= 0 && index < arr.length;
	}

	private void initDynamoDB() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
		dynamoDbMapper = new DynamoDBMapper(client);
	}
}
