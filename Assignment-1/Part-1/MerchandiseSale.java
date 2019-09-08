package com;
import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math; 
import com.*;


public class MerchandiseSale{
	public static void main(String[] args){
		List<String> fileInput = readFileInList("input.txt"); 
		ArrayList<salesThread>threads=new ArrayList<>();
		
		String[] initialInventory=fileInput.get(0).split(" ",4);

		int batchSize = (int)Math.ceil((fileInput.size()-1)/5.0);
		Inventory inventory = new Inventory(Integer.parseInt(initialInventory[0]),Integer.parseInt(initialInventory[1]),Integer.parseInt(initialInventory[2]),
									Integer.parseInt(initialInventory[3]));
		inventory.displayInventory();
		
		for(int i=1;i<fileInput.size();i+=batchSize){
			ArrayList<Order> orderList = getOrders(fileInput,i,Math.min(i+batchSize,fileInput.size()));
			salesThread newThread = new salesThread(orderList,inventory);
			threads.add(newThread);
		}
		for(int i=0;i<threads.size();i++)
		{
			threads.get(i).start();
		}
	}	

static	ArrayList<Order> getOrders(List<String> fileInput,int start,int end){
		ArrayList<Order> orders=new ArrayList<>();
		for(int i=start;i<end;i++)
		{
			String orderDetail = (String)(fileInput.get(i));
			String[] orderDetailList = orderDetail.split(" ",3);
			orders.add(new Order(Integer.parseInt(orderDetailList[0]),orderDetailList[1].charAt(0),Integer.parseInt(orderDetailList[2])));	  
		}
		return orders;
	}
	

	static List<String> readFileInList(String fileName){ 
	    List<String> lines = Collections.emptyList(); 
	    try{ 
	      lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8); 
	    } 
		catch (IOException e){ 
	      	e.printStackTrace(); 
	    } 
	    return lines; 
	}
}


