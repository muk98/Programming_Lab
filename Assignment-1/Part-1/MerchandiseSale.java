/***
 * Author:  B.Tushara Langulya
 * Summary: Merchandise class(main module) reads input from file and distributes the orders among 5 threads.
 ***/
package com;
import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math; 
import com.*;


/*MerchandiseSale Class*/
public class MerchandiseSale{
	public static void main(String[] args){

		/* The input is read from the file */
		List<String> fileInput = readFileInList("input.txt"); 
		ArrayList<salesThread>threads=new ArrayList<>();

		/* From the input file the inventory is stored */
		String[] initialInventory=fileInput.get(0).split(" ",4);
		Inventory inventory = new Inventory(Integer.parseInt(initialInventory[0]),Integer.parseInt(initialInventory[1]),Integer.parseInt(initialInventory[2]),
									Integer.parseInt(initialInventory[3]));

		/* Initial Inventory is displayed */
		inventory.displayInventory();
		
		/* Number of orders which should be assigned per thread is calculated */
		int batchSize = (int)Math.ceil((fileInput.size()-1)/5.0);

		/* New thread is created in each loop and the orders are assigned to each thread */
		for(int i=1;i<fileInput.size();i+=batchSize){
			ArrayList<Order> orderList = getOrders(fileInput,i,Math.min(i+batchSize,fileInput.size()));
			salesThread newThread = new salesThread(orderList,inventory);
			threads.add(newThread);
		}
		for(int i=0;i<threads.size();i++)
		{
			/* All threads start running */
			threads.get(i).start();
		}
	}	

	/** This function returns an array of orders between start to end index in the batch data from the 
	 * file input
	 * @param fileInput is the list of strings read from file
	 * @param start is the starting index of the list which we want to include in array
	 * @param end is the ending index of the list which we want to include in array
	 * */
	static	ArrayList<Order> getOrders(List<String> fileInput,int start,int end){
			ArrayList<Order> orders=new ArrayList<>();

			for(int i=start;i<end;i++)
			{
				/* From each line using order details Order object is created */
				String orderDetail = (String)(fileInput.get(i));
				String[] orderDetailList = orderDetail.split(" ",3);
				orders.add(new Order(Integer.parseInt(orderDetailList[0]),orderDetailList[1].charAt(0),Integer.parseInt(orderDetailList[2])));	  
			}
			return orders;
		}
	
	/**File is read line by line and list of string is returned 
	 * @param fileName is the input filename 
	*/
	static List<String> readFileInList(String fileName){ 
	    List<String> lines = Collections.emptyList(); 
		try{ 
		/* Entire file is read line wise */
	      lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8); 
	    } 
		catch (IOException e){ 
	      	e.printStackTrace(); 
	    } 
	    return lines; 
	}
}


