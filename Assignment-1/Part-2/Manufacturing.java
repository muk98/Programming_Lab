import java.util.concurrent.Phaser;
// import com.sun.tools.javac.code.Attribute.Array;
import java.util.Scanner;

class Globals{
    int id;
    int currBottleToDraw;

    Globals(){
        id=1;
        currBottleToDraw=1;
    }
}

class CustomPhaser extends Phaser { 
	int numPhases; 
	CustomPhaser(int parties, int phaseCount) 
	{ 
		super(parties); 
		numPhases = phaseCount - 1; 
	} 

	@Override
	protected boolean onAdvance(int phase, 
								int registeredParties) 
	{ 
		System.out.println("Phase "
						+ phase 
						+ " completed.\n"); 

		// If all phases have completed, return true. 
		if (phase == numPhases 
			|| registeredParties == 0) { 
			return true; 
		} 
		// otherwise, return false 
		return false; 
	} 
} 



class PackagingUnitThread implements Runnable { 
	Phaser phsr; 
	String name; 
    Bottles currentBottle;
    Tray sealingTray;
    Tray B1PackagingTray;
    Tray B2PackagingTray;
    Globals globals;

	PackagingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray,Globals globalvar)
	{ 
		phsr = p; 
        this.name = name; 
        this.currentBottle = bottle;
        this.B1PackagingTray = B1Tray;
        this.B2PackagingTray = B2Tray;
        this.sealingTray = sealingTray;
        this.globals = globalvar;

		phsr.register(); 
		new Thread(this).start(); 
	} 

	@Override
	public void run() 
	{ 
		while (!phsr.isTerminated()) { 
			System.out.println("Thread " + name 
							+ " Beginning Phase "
                            + phsr.getPhase());
            
            // System.out.println("Currently Processing Bottle :" + Integer.toString(this.currentBottle.id));
            if(this.currentBottle != null){
                System.out.println("PackagingUnit Currently Processing Bottle :" + Integer.toString(this.currentBottle.id));
            }
            else{
                if(this.sealingTray.currentBottlesNum==0){
                    Bottles bottle = new Bottles(globals.id++, globals.currBottleToDraw);
                    System.out.println("tooo");
                    if(globals.currBottleToDraw==1) globals.currBottleToDraw=2;
                    this.currentBottle = bottle;
                }
            }

			phsr.arriveAndAwaitAdvance(); 

			try { 
				Thread.sleep(10); 
			} 
			catch (InterruptedException e) { 
				System.out.println(e); 
			} 
		} 
	} 
} 

class SealingUnitThread implements Runnable { 
	Phaser phsr; 
	String name; 
    Bottles currentBottle;
    Tray sealingTray;
    Tray B1PackagingTray;
    Tray B2PackagingTray;
    Globals globals;

	SealingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray,Globals globalvar)
	{ 
		phsr = p; 
        this.name = name; 
        this.currentBottle = bottle;
        this.B1PackagingTray = B1Tray;
        this.B2PackagingTray = B2Tray;
        this.sealingTray = sealingTray;
        this.globals = globalvar;

		phsr.register(); 
		new Thread(this).start(); 
	}  

	@Override
	public void run() 
	{ 
		while (!phsr.isTerminated()) { 
			System.out.println("Thread " + name 
							+ " Beginning Phase "
							+ phsr.getPhase()); 
            
            
            if(this.currentBottle != null){
                System.out.println("SealingUnit Currently Processing Bottle :" + Integer.toString(this.currentBottle.id));
            }
            else{
                if(this.sealingTray.currentBottlesNum==0){
                    Bottles bottle = new Bottles(globals.id++, globals.currBottleToDraw);
                    System.out.println("tooo");
                    if(globals.currBottleToDraw==1) globals.currBottleToDraw=2;
                    this.currentBottle = bottle;
                }
            }
            
            phsr.arriveAndAwaitAdvance(); 


			try { 
				Thread.sleep(10); 
			} 
			catch (InterruptedException e) { 
				System.out.println(e); 
			} 
		} 
	} 
} 

class Bottles{
    int id;
    int type;
    boolean packagingStatus;
    boolean sealingStatus;

    Bottles(int id,int type){
        this.id = id;
        this.type = type;
        packagingStatus = false;
        sealingStatus = false;
    }
}

class Tray{
    int id;
    int size;
    int currentBottlesNum;
    Bottles bottlesId[];

    Tray(int id,int size){
        this.id = id;
        this.size = size;
        currentBottlesNum = 0;
        bottlesId = new Bottles[size]; 
    }

}

public class Manufacturing{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the time at which you want to get the status of the bottles.");
        int time = input.nextInt();
        input.close();
        Phaser phsr = new CustomPhaser(1, time);
        
        Globals globalvar = new Globals();
        // int id=1;
        // Bottles b1 = new Bottles(id++,1);
        // Bottles b2 = new Bottles(id++,2);

        Tray B1trayPackaging = new Tray(1,2);
        Tray B2trayPackaging = new Tray(2, 3);
        Tray traySealing = new Tray(3,2);

        new PackagingUnitThread(phsr,"Packaging Unit",null,traySealing,B1trayPackaging,B2trayPackaging,globalvar);
        new SealingUnitThread(phsr,"Sealing Unit",null,traySealing,B1trayPackaging,B2trayPackaging,globalvar);
        
        while (!phsr.isTerminated()) { 
			phsr.arriveAndAwaitAdvance(); 
        }
         
		System.out.println("The phaser is terminated\n");

    }
}