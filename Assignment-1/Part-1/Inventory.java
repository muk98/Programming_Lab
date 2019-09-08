package com;
import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math;
import com.*; 

class Inventory{

	private	int countS;
	private	int countM;
	private	int countC;
	private	int countL;

	Inventory(int countS,int countM,int countL,int countC){
		this.countS=countS;
		this.countM=countM;
		this.countL=countL;
		this.countC=countC;
	}
	
	synchronized public void displayInventory(){
		System.out.println("************************Inventory************************");
		System.out.println("|    S  	|	M	|	L	|	C	|");
		System.out.println("|    "+ Integer.toString(this.countS)+"  	|	"+ Integer.toString(this.countM) +"	|	"+Integer.toString(this.countL)+"	|	"+Integer.toString(this.countC)+"	|");
		System.out.println("\n");	
	}


	public void processOrder(int orderId,char type,int quantity){
		
		switch (type) {
			case 'S': if(this.countS<quantity){
							System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
							this.displayInventory();
						}
						else{
							
							synchronized((Integer)this.countS){
								this.countS -= quantity;
							}
							System.out.println("Order "+ Integer.toString(orderId)+ " is successful.");
							this.displayInventory();
						}		
				break;
			case 'M': 
			 	if(this.countM<quantity){
					 	System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
						this.displayInventory();
					}
				else{
					synchronized((Integer)this.countM){
						this.countM -= quantity;
					}
					System.out.println("Order "+ Integer.toString(orderId)+ " is successful.");
					this.displayInventory();
				}
			break;
			case 'L': if(this.countL<quantity){
					System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
					this.displayInventory();
				}
				else{
					synchronized((Integer)this.countL){
						this.countL -= quantity;
					}
					System.out.println("Order "+ Integer.toString(orderId)+ " is successful.");
					this.displayInventory();
				}		
			break;
			case 'C': if(this.countC<quantity){
					System.out.println("Order "+ Integer.toString(orderId)+ " is not successful.");
					this.displayInventory();
				}
				else{
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