package com;
import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math; 
import com.*;

class salesThread extends Thread 
{ 
	ArrayList<Order> orders;
	Inventory inventory;

	salesThread(ArrayList<Order>orders,Inventory inventory)
	{
		this.orders=orders;
		this.inventory=inventory;
	}
    public void run() 
    { 
		for(int i=0;i<orders.size();i++){
			synchronized(inventory){
				inventory.processOrder(orders.get(i).getOrderId(),orders.get(i).getType(),orders.get(i).getQuantity());
				
			}
		}
    } 
} 
