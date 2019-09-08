package com;

import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;

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
