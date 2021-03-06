/**
 * Author: Mukul Verma
 * Summary: This Module process the order and display the current inventory status.
**/

package com;
import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math;
import com.*; 

/*Inventory Class*/
class Inventory{
	private	int countS;
	private	int countM;
	private	int countC;
	private	int countL;

	/*Initialize the inventory*/
	Inventory(int countS,int countM,int countL,int countC){
		this.countS=countS;
		this.countM=countM;
		this.countL=countL;
		this.countC=countC;
	}
	
	/*Function to display the inventory status.*/
	synchronized public void displayInventory(){
		System.out.println("************************Inventory************************");
		System.out.println("|    S  	|	M	|	L	|	C	|");
		System.out.println("|    "+ Integer.toString(this.countS)+"  	|	"+ Integer.toString(this.countM) +"	|	"+Integer.toString(this.countL)+"	|	"+Integer.toString(this.countC)+"	|");
		System.out.println("\n");	
	}


	/**Function to process the order
	 * @param orderId is the order ID
	 * @param type is the type of the order
	 * @param quantity is the quantity of the order
	 */
	public void processOrder(int orderId,char type,int quantity){
		
		if(quantity<0)
		{
			System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
			this.displayInventory();
			return;
		}
		switch (type) {	
			case 'S': 
						/*Check if inventory has sufficient amount to accept the order.*/
						if(this.countS<quantity){
							System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
							this.displayInventory();
						}
						else{
							
							/*Decrease the count according to the order and need to be synchronized*/
							synchronized((Integer)this.countS){
								this.countS -= quantity;
							}
							System.out.println("Order "+ Integer.toString(orderId)+ " is successful.");
							this.displayInventory();
						}		
				break;
			case 'M': 
						/*Check if inventory has sufficient amount to accept the order.*/
						if(this.countM<quantity){
								System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
								this.displayInventory();
							}
						else{
							/*Decrease the count according to the order and need to be synchronized*/
							synchronized((Integer)this.countM){
								this.countM -= quantity;
							}
							System.out.println("Order "+ Integer.toString(orderId)+ " is successful.");
							this.displayInventory();
						}
				break;
			case 'L': 	
						/*Decrease the count according to the order and need to be synchronized*/
						if(this.countL<quantity){
							System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
							this.displayInventory();
						}
						else{
							/*Decrease the count according to the order and need to be synchronized*/
							synchronized((Integer)this.countL){
								this.countL -= quantity;
							}
							System.out.println("Order "+ Integer.toString(orderId)+ " is successful.");
							this.displayInventory();
						}		
				break;
			case 'C': 
						/*Decrease the count according to the order and need to be synchronized*/
						if(this.countC<quantity){
							System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
							this.displayInventory();
						}
						else{
							/*Decrease the count according to the order and need to be synchronized*/
							synchronized((Integer)this.countC){
								this.countC -= quantity;
							}
							System.out.println("Order "+ Integer.toString(orderId)+ " is successful.");
							this.displayInventory();
						}		
				break;					
			default:
					System.out.println("Invalid Order Details");
				break;
		}
	}
}