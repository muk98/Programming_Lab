import java.util.*; 
import java.nio.charset.StandardCharsets; 
import java.nio.file.*; 
import java.io.*; 
import java.lang.Math; 

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