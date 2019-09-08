package com;
import java.util.concurrent.Phaser;
import java.util.Scanner;
import java.util.LinkedList; 
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;  
import com.*;

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