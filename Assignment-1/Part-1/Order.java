package com;
import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math; 
import com.*;

class Order
{
	private	int orderId;
	private	char type;
	private	int quantity;

	Order(int orderId,char type,int quantity)
	{
		this.orderId=orderId;
		this.type=type;
		this.quantity=quantity;
	}
	int getOrderId()
	{
		return orderId;
	}
	char getType()
	{
		return type;
	}
	int getQuantity()
	{
		return quantity;
	}
}