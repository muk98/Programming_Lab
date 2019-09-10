/**
 * Author: Mukul Verma
 * Summary: This Module contains the Order class which stores order details
**/

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
		/* Initialising order details */
		this.orderId=orderId;
		this.type=type;
		this.quantity=quantity;
	}
	int getOrderId()
	{
		/* Returning id of the order */
		return orderId;
	}
	char getType()
	{
		/* Returning type of the order(L/M/S) */
		return type;
	}
	int getQuantity()
	{
		/* Returning quantity of the order */
		return quantity;
	}
}