/*
* Author: Mukul Verma 
* Summary: The Modules contains the class to store the bottle information.
*/

package com;
import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;

/*Bottle Class*/
class Bottles{
    int id;
    
    /*Type variable to store whether the bottle is of type B1 or B2*/
    int type;
    boolean packagingStatus;
    boolean sealingStatus;


    /*Initialize the bottle details*/
    Bottles(int id,int type){
        this.id = id;
        this.type = type;
        packagingStatus = false;
        sealingStatus = false;
    }
}