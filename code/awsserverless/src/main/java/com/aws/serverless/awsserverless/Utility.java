package com.aws.serverless.awsserverless;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.aws.serverless.awsserverless.entity.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {

	public static Map<String, String> createHeader() {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-type", "application/json");
		headers.put("X-amazon-author", "Dipesh");
		headers.put("X-amazon-apiVersion", "v1");
		return headers;
	}

	public static String convertObjectToString(Product product, Context context) {
		String jsonBody = null;

		try {
			jsonBody = new ObjectMapper().writeValueAsString(product);
		} catch (Exception e) {
			context.getLogger().log("Unable to convert product object to string");
			e.printStackTrace();
		}
		return jsonBody;
	}

	public static Product convertStringToObject(String jsonBody, Context context) {
		Product product = null;

		try {
			product = new ObjectMapper().readValue(jsonBody, Product.class);
		} catch (Exception e) {
			context.getLogger().log("Unable to convert product json string to object");
			e.printStackTrace();
		}

		return product;
	}

	public static String convertListOfObjectToString(List<Product> products, Context context) {
		String jsonBody = null;

		try {
			jsonBody = new ObjectMapper().writeValueAsString(products);
		} catch (Exception e) {
			context.getLogger().log("Uable to convert produts list to string.");
			e.printStackTrace();
		}

		return jsonBody;
	}
}
