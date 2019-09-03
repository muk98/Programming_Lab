import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  

class CustomPhaser extends Phaser { 
    int numPhases;
    PackagingUnitThread pkg;
    SealingUnitThread slg;
    ReentrantLock rel;
    
	CustomPhaser(int parties, int phaseCount,ReentrantLock rel) 
	{ 
		super(parties); 
        numPhases = phaseCount - 1; 
        this.rel = rel;
	} 
    
    void set(PackagingUnitThread pkg,SealingUnitThread slg){
        this.pkg = pkg;
        this.slg = slg;
        
    }
      
	@Override
	protected boolean onAdvance(int phase, 
								int registeredParties) 
	{ 
        
        if(pkg.pending){
            rel.lock();
            pkg.sealingTray.q.add(pkg.currentBottle);
            System.out.println("transferred to sealing Tray "+ Integer.toString(pkg.currentBottle.type) + " "+  Integer.toString(pkg.currentBottle.id));
            pkg.pending=false;
            rel.unlock();
        }
 
        if(slg.pending)
        {
            rel.lock();
            slg.pending=false;
            if(slg.currentBottle.type == 1){
                slg.B1PackagingTray.q.add(slg.currentBottle);
                System.out.println("transferred to B1 Tray "+ Integer.toString(slg.currentBottle.type) + " "+  Integer.toString(slg.currentBottle.id));
            }
            else if(slg.currentBottle.type == 2){
                slg.B2PackagingTray.q.add(slg.currentBottle);
                System.out.println("transferred to B2 Tray "+ Integer.toString(slg.currentBottle.type) + " "+  Integer.toString(slg.currentBottle.id));
            }
            rel.unlock();
        }          
        
        System.out.println("Phase "
						+ phase 
						+ " completed.\n"); 

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
    boolean pending;
    ReentrantLock rel;
    PackagingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray
                            ,UnfinishedTray unfinishedTray,GoDown goDown,ReentrantLock rel)
	{ 
		phsr = p; 
        this.rel=rel;
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
        this.pending=false;
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
            if(this.empty==false && this.processingTime<2){
                System.out.println("Packaging Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{
                if(this.processingTime>=2 && this.empty==false){
                    if(this.currentBottle.packagingStatus==false){
                        this.currentBottle.packagingStatus =true;
                        if(this.currentBottle.type==1) b1++;
                        else b2++;
                        System.out.println("Packaging DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                    }
                    else{
                        System.out.println("IDLEEEEEEEEE");
                    }
                    if(this.currentBottle.sealingStatus == true){
                        if(this.currentBottle.type == 1){
                            rel.lock();
                            this.goDown.totalB1++;
                            rel.unlock();
                        }
                        else {
                            rel.lock();
                            this.goDown.totalB2++;
                            rel.unlock();
                        }
                        this.empty=true;
                        System.out.println("DONE!!" + Integer.toString(this.currentBottle.type));
                    }
                    else if(this.sealingTray.q.size() < this.sealingTray.size){
                        this.pending=true;
                        this.empty=true;
                    }
                   
                }
                else if(this.empty==true){
                    if(this.B1PackagingTray.q.size()==0 && this.B2PackagingTray.q.size()==0){
                        if(unfinishedTray.totalB1 ==0 && unfinishedTray.totalB2==0 ){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;
                            System.out.println("Bottle drawn by Packaging Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
                            if(this.currentBottle.type==1) {
                                rel.lock();
                                unfinishedTray.totalB1--;
                                rel.unlock();
                                if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                else if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                            }
                            else{
                                rel.lock();
                                unfinishedTray.totalB2--;
                                rel.unlock();
                                if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                else if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                            }
                            
                            
                            this.empty=false;
                        }
                    }
                    else{
                        if(this.B1PackagingTray.q.size()>0 && this.B2PackagingTray.q.size()>0){
                            if(this.initial==false){
                                rel.lock();
                                this.currentBottle = this.B1PackagingTray.q.peek();
                                this.B1PackagingTray.q.remove();
                                rel.unlock();
                                this.initial = true;
                            }
                            else{
                                rel.lock();
                                this.currentBottle = this.B2PackagingTray.q.peek();
                                this.B2PackagingTray.q.remove();
                                rel.unlock();
                                this.initial=false;
                            }
                        }
                        else if(this.B1PackagingTray.q.size()>0){
                            rel.lock();
                            this.currentBottle = this.B1PackagingTray.q.peek();
                            this.B1PackagingTray.q.remove();
                            rel.unlock();
                            this.initial=true;
                        }
                        else if(this.B2PackagingTray.q.size()>0){
                            rel.lock();
                            this.currentBottle = this.B2PackagingTray.q.peek();
                            this.B2PackagingTray.q.remove();
                            rel.unlock();
                            this.initial=false;
                        }
                        this.empty=false;
                        System.out.println("Bottle drawn by Packaging Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
                    }
                    this.processingTime = 1;
                }
            }

			phsr.arriveAndAwaitAdvance(); 

			try { 
				Thread.sleep(50); 
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
    boolean pending;
    ReentrantLock rel;

    SealingUnitThread(Phaser p, String name,Bottles bottle,Tray sealingTray,Tray B1Tray,Tray B2Tray
                            ,UnfinishedTray unfinishedTray,GoDown goDown,ReentrantLock rel)
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
        this.pending=false;
        this.rel=rel;
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
            if(this.empty==false && this.processingTime<3){
                System.out.println("Sealing Unit: Currently Processing Bottle :" + Integer.toString(this.currentBottle.type));
            }
            else{
                if(this.processingTime>=3 && this.empty==false){
                    if(this.currentBottle.sealingStatus==false){
                        if(this.currentBottle.type==1) b1++;
                        else b2++;
                        System.out.println("Sealing DONE!!" + Integer.toString(this.currentBottle.type) + " "+  Integer.toString(this.currentBottle.id));
                        this.currentBottle.sealingStatus= true;
                    }
                    else
                    {
                        System.out.println("Idleeee");
                    }

                    if(this.currentBottle.packagingStatus == true){
                        if(this.currentBottle.type == 1){
                            rel.lock();
                            this.goDown.totalB1++;
                            rel.unlock();
                        }
                        else {
                            rel.lock();
                            this.goDown.totalB2++;
                            rel.unlock();
                        }
                        this.empty = true;
                        System.out.println("DONE!!" + Integer.toString(this.currentBottle.type));
                    }
                    else if(this.currentBottle.type == 1 && this.B1PackagingTray.q.size() < this.B1PackagingTray.size)
                    {
                        this.empty=true;
                        this.pending=true;
                    }  
                     else if(this.currentBottle.type == 2 && this.B2PackagingTray.q.size() < this.B2PackagingTray.size){
                            this.pending=true;
                            this.empty = true;
                        }       
                    
                }
                else if(this.empty ==true){
                    
                    if(this.sealingTray.q.size()==0){
                        if(unfinishedTray.totalB1 ==0 && unfinishedTray.totalB2==0){
                            System.out.println("No bottles left to Draw !!");
                        }
                        else{
                            Bottles bottle = new Bottles(unfinishedTray.id++, this.currBottleToDraw);
                            this.currentBottle = bottle;
                            if(this.currentBottle.type==1) {
                                rel.lock();
                                unfinishedTray.totalB1--;
                                rel.unlock();
                                if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                                else if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                            }
                            else{
                                rel.lock();
                                unfinishedTray.totalB2--;
                                rel.unlock();
                                if(unfinishedTray.totalB1>0) this.currBottleToDraw=1;
                                else if(unfinishedTray.totalB2>0) this.currBottleToDraw=2;
                            }
                            this.empty=false;
                            System.out.println("Bottle drawn by Sealing Unit from Unfinished Tray !! of type:" + Integer.toString(this.currentBottle.type));
                        }
                    }
                    else{
                        rel.lock();
                        this.currentBottle = this.sealingTray.q.peek();
                        this.sealingTray.q.remove();
                        rel.unlock();
                        this.empty=false;
                        System.out.println("Bottle drawn by Sealing Unit from Tray !! of type:" + Integer.toString(this.currentBottle.type));
  
                    }
                    this.processingTime = 1;
                }
            }
            phsr.arriveAndAwaitAdvance(); 

			try { 
				Thread.sleep(50); 
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
    Queue<Bottles> q; 

    Tray(int id,int size){
        this.id = id;
        this.size = size;
        q = new LinkedList<>(); 
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
    int totalB1;
    int totalB2;

    UnfinishedTray(int totalB1,int totalB2){
        this.totalB1 = totalB1;
        this.totalB2 = totalB2;
        id=1;
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
        ReentrantLock rel = new ReentrantLock(); 
        
        PackagingUnitThread pkg=null;
        SealingUnitThread slg=null;

        CustomPhaser phsr = new CustomPhaser(1, time,rel);

        UnfinishedTray unfinishedTray = new UnfinishedTray(totalB1,totalB2);
        Tray B1trayPackaging = new Tray(1,2);
        Tray B2trayPackaging = new Tray(2, 3);
        Tray traySealing = new Tray(3,2);
        GoDown goDown = new GoDown(); 

        pkg  =new PackagingUnitThread(phsr,"Packaging Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown,rel);
        slg =  new SealingUnitThread(phsr,"Sealing Unit",null,traySealing,B1trayPackaging,B2trayPackaging,unfinishedTray,goDown,rel);
        phsr.set(pkg,slg);
        while (!phsr.isTerminated()) { 
            phsr.arriveAndAwaitAdvance(); 
        }

        System.out.println("| Type  |   Status    |   Count");
        System.out.println("|   B1  |   Packaged  |   "+Integer.toString(pkg.b1));
        System.out.println("|   B1  |   Sealed    |   "+Integer.toString(slg.b1));
        System.out.println("|   B1  |   Godown    |   "+Integer.toString(goDown.totalB1));

        System.out.println("|   B2  |   Packaged  |   "+Integer.toString(pkg.b2));
        System.out.println("|   B2  |   Sealed    |   "+Integer.toString(slg.b2));
        System.out.println("|   B2  |   Godown    |   "+Integer.toString(goDown.totalB2));
		System.out.println("The phaser is terminated\n");
    }
}