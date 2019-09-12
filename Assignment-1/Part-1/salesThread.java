/**
 * Author: B.Tushara Langulya
 * Summary: This Module contains sales class which processes the orders one by one which are assigned to it.
**/

package com;
import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math; 
import com.*;

/*Sales class*/
class salesThread extends Thread 
{ 
	ArrayList<Order> orders;
	Inventory inventory;
	
	/* Orders and inventory is initialised for this thread */
	salesThread(ArrayList<Order>orders,Inventory inventory)
	{

		this.orders=orders;
		this.inventory=inventory;
	}
    public void run() 
    { 
		for(int i=0;i<orders.size();i++){
			synchronized(inventory){
				/* For every order the required request is sent to the inventory */
				inventory.processOrder(orders.get(i).getOrderId(),orders.get(i).getType(),orders.get(i).getQuantity());

			}
		}
    } 
} 
