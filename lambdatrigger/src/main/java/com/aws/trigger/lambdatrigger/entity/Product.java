package com.aws.trigger.lambdatrigger.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "s3_event_data")
public class Product {
	@DynamoDBHashKey(attributeName = "id")
	private int id;
	@DynamoDBAttribute(attributeName = "name")
	private String name;
	@DynamoDBAttribute(attributeName = "description")
	private String description;
	@DynamoDBAttribute(attributeName = "price")
	private String price;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getPrice() {
		return this.price;
	}

	public void setPrice(final String price) {
		this.price = price;
	}
}
