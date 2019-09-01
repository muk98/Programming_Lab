import java.util.concurrent.Phaser;
// import com.sun.tools.javac.code.Attribute.Array;
import java.util.Scanner;



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
    boolean initial;
    Bottles currentBottle;
    Tray sealingTray;
    Tray B1PackagingTray;
    Tray B2PackagingTray;
    UnfinishedTray unfinishedTray;
    int processingTime;
    GoDown goDown;
    int currBottleToDraw;
    boolean empty;
    int b1;
    int b2;
    PackagingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray
                            ,UnfinishedTray unfinishedTray,GoDown goDown)
	{ 
		phsr = p; 
        this.name = name; 
        this.currentBottle = bottle;
        this.initial = false;
        this.B1PackagingTray = B1Tray;
        this.B2PackagingTray = B2Tray;
        this.sealingTray = sealingTray;
        this.unfinishedTray = unfinishedTray;
        this.processingTime = 0;
        this.goDown = goDown;
        this.currBottleToDraw = 1;
        this.empty=true;
        b1=0;
        b2=0;
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
            this.processingTime++;
            if(this.empty==false && this.processingTime<3){
                System.out.println("Packaging Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{
                if(this.processingTime>=3 && this.empty==false){
                    this.currentBottle.packagingStatus =true;
                    if(this.currentBottle.type==1) b1++;
                    else b2++;
                    System.out.println("Packaging DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                    if(this.currentBottle.sealingStatus == true){
                        if(this.currentBottle.type == 1){
                            this.goDown.totalB1++;
                        }
                        else this.goDown.totalB2++;
                        this.empty=true;
                        System.out.println("DONE!!" + Integer.toString(this.currentBottle.type));
                    }
                    else if(this.sealingTray.currentBottlesNum < this.sealingTray.size){
                        int curr = this.sealingTray.currentBottlesNum;
                        this.sealingTray.bottlesId[curr] = this.currentBottle;
                        this.sealingTray.currentBottlesNum++;
                        System.out.println("transferred to sealing Tray "+ Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                        this.empty=true;
                    }
                }
                else if(this.empty==true){
                    if(this.B1PackagingTray.currentBottlesNum==0 && this.B2PackagingTray.currentBottlesNum==0){
                        if(unfinishedTray.currBottleToDraw ==-1){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;
                            System.out.println("Bottle drawn by Packaging Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
                            if(this.currentBottle.type==1) {
                                unfinishedTray.totalB1--;
                                if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                else if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                            }
                            else{
                                unfinishedTray.totalB2--;
                                if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                else if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                            }
                            
                            if(unfinishedTray.totalB1+unfinishedTray.totalB2<=0) {
                                unfinishedTray.currBottleToDraw = -1; 
                            }
                            this.empty=false;
                        }
                    }
                    else{
                        if(this.B1PackagingTray.currentBottlesNum>0 && this.B2PackagingTray.currentBottlesNum>0){
                            if(this.initial==false){
                                this.currentBottle = this.B1PackagingTray.bottlesId[0];
                                this.B1PackagingTray.bottlesId[0] = this.B1PackagingTray.bottlesId[1];
                                this.B1PackagingTray.bottlesId[1] = null;
                                this.B1PackagingTray.currentBottlesNum--;  
                                this.initial = true;
                            }
                            else{
                                if(this.currentBottle.type==1){
                                    this.currentBottle = this.B2PackagingTray.bottlesId[0];
                                    this.B2PackagingTray.bottlesId[0] = this.B2PackagingTray.bottlesId[1];
                                    this.B2PackagingTray.bottlesId[1] = this.B2PackagingTray.bottlesId[2];
                                    this.B2PackagingTray.bottlesId[1] = null;
                                    this.B2PackagingTray.bottlesId[2] = null;
                                    this.B2PackagingTray.currentBottlesNum--;  
                                }
                                else{
                                    this.currentBottle = this.B1PackagingTray.bottlesId[0];
                                    this.B1PackagingTray.bottlesId[0] = this.B1PackagingTray.bottlesId[1];
                                    this.B1PackagingTray.bottlesId[1] = null;
                                    this.B1PackagingTray.currentBottlesNum--;  
                                }
                            }
                        }
                        else if(this.B1PackagingTray.currentBottlesNum>0){
                            this.currentBottle = this.B1PackagingTray.bottlesId[0];
                            this.B1PackagingTray.bottlesId[0] = this.B1PackagingTray.bottlesId[1];
                            this.B1PackagingTray.bottlesId[1] = null;
                            this.B1PackagingTray.currentBottlesNum--;
                        }
                        else if(this.B2PackagingTray.currentBottlesNum>0){
                            this.currentBottle = this.B2PackagingTray.bottlesId[0];
                            this.B2PackagingTray.bottlesId[0] = this.B2PackagingTray.bottlesId[1];
                            this.B2PackagingTray.bottlesId[1] = this.B2PackagingTray.bottlesId[2];
                            this.B2PackagingTray.bottlesId[1] = null;
                            this.B2PackagingTray.bottlesId[2] = null;
                            this.B2PackagingTray.currentBottlesNum--;
                        }
                        this.empty=false;
                        System.out.println("Bottle drawn by Packaging Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
                    }
                    this.processingTime = 1;
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
    UnfinishedTray unfinishedTray;
    int processingTime;
    boolean empty;
    int b1;
    int b2;
    GoDown goDown;
    int currBottleToDraw;

    SealingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray
                            ,UnfinishedTray unfinishedTray,GoDown goDown)
	{ 
		phsr = p; 
        this.name = name; 
        this.currentBottle = bottle;
        this.B1PackagingTray = B1Tray;
        this.B2PackagingTray = B2Tray;
        this.sealingTray = sealingTray;
        this.unfinishedTray = unfinishedTray;
        this.processingTime = 0;
        this.goDown = goDown;
        this.currBottleToDraw =  2;
        this.empty=true;
        b1 = 0;
        b2=0;
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
            
            this.processingTime++;
            if(this.empty==false && this.processingTime<2){
                System.out.println("Sealing Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{
                if(this.processingTime>=2 && this.empty==false){
                    if(this.currentBottle.type==1) b1++;
                    else b2++;
                    System.out.println("Sealing DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                    this.currentBottle.sealingStatus= true;
                    if(this.currentBottle.packagingStatus == true){
                        if(this.currentBottle.type == 1){
                            this.goDown.totalB1++;
                        }
                        else this.goDown.totalB2++;
                        this.empty = true;
                        System.out.println("DONE!!" + Integer.toString(this.currentBottle.type));
                    }
                    else if(this.currentBottle.type == 1 && this.B1PackagingTray.currentBottlesNum < this.B1PackagingTray.size){
                        int curr = this.B1PackagingTray.currentBottlesNum;
                        this.B1PackagingTray.bottlesId[curr] = this.currentBottle;
                        this.B1PackagingTray.currentBottlesNum++;
                        System.out.println("transferred to B1 Tray "+ Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                       
                        this.empty = true;
                    }
                    else if(this.currentBottle.type == 2 && this.B2PackagingTray.currentBottlesNum < this.B2PackagingTray.size){
                        int curr = this.B2PackagingTray.currentBottlesNum;
                        this.B2PackagingTray.bottlesId[curr] = this.currentBottle;
                        this.B2PackagingTray.currentBottlesNum++;
                        System.out.println("transferred to B2 Tray "+ Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                        this.empty = true;
                    }
                }
                else if(this.empty ==true){
                    if(this.sealingTray.currentBottlesNum==0){
                        if(unfinishedTray.currBottleToDraw ==-1){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;
                            if(this.currentBottle.type==1) {
                                unfinishedTray.totalB1--;
                                if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                else if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                            }
                            else{
                                unfinishedTray.totalB2--;
                                if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                else if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                            }

                            if(unfinishedTray.totalB1+unfinishedTray.totalB2<=0) {
                                unfinishedTray.currBottleToDraw = -1; 
                            }
                            this.empty=false;
                            System.out.println("Bottle drawn by Sealing Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
                        }
                    }
                    else{
                        this.currentBottle = this.sealingTray.bottlesId[0];
                        this.sealingTray.bottlesId[0] = this.sealingTray.bottlesId[1];
                        this.sealingTray.bottlesId[1] = null;
                        this.sealingTray.currentBottlesNum--;
                        this.empty=false;
                        System.out.println("Bottle drawn by Sealing Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
  
                    }
                    this.processingTime = 1;
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

class GoDown{
    int totalB1;
    int totalB2;

    GoDown(){
        totalB1 = 0;
        totalB2 = 0;
    }   
}

class UnfinishedTray{
    int id;
    int currBottleToDraw;
    int totalB1;
    int totalB2;

    UnfinishedTray(int totalB1,int totalB2){
        this.totalB1 = totalB1;
        this.totalB2 = totalB2;
        id=1;
        currBottleToDraw=1;
    }
}

public class Manufacturing{
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        System.out.println("Input: <No. of bottle type B1, No. of bottle type B2, Time duration for observation (in sec.)>");
        int totalB1 = input.nextInt();
        int totalB2 = input.nextInt();
        int time = input.nextInt();
        input.close();
        Phaser phsr = new CustomPhaser(1, time);
        
        Globals globalvar = new Globals();
        // int id=1;
        // Bottles b1 = new Bottles(id++,1);
        // Bottles b2 = new Bottles(id++,2);
        UnfinishedTray unfinishedTray = new UnfinishedTray(totalB1,totalB2);
        Tray B1trayPackaging = new Tray(1,2);
        Tray B2trayPackaging = new Tray(2, 3);
        Tray traySealing = new Tray(3,2);
        GoDown goDown = new GoDown(); 

        PackagingUnitThread pkg  =new PackagingUnitThread(phsr,"Packaging Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown);
        SealingUnitThread slg =  new SealingUnitThread(phsr,"Sealing Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown);
        
        while (!phsr.isTerminated()) { 
			phsr.arriveAndAwaitAdvance(); 
        }
        System.out.println(Integer.toString(pkg.b1));
        System.out.println(Integer.toString(pkg.b2));
        System.out.println(Integer.toString(slg.b1));
        System.out.println(Integer.toString(slg.b2));
        
		System.out.println("The phaser is terminated\n");

    }
}