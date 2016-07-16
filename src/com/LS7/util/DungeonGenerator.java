package com.LS7.util;

import java.util.ArrayList;

import com.LS7.util.Data.Map;
import com.LS7.util.Math.Vector2f;

public class DungeonGenerator {
    
    public static final int maxRoomAttempts = 500;
    public static final int maxRoomCount = 20;
    public static final int roomMinSize = 2;
    public static final int roomMaxSize = 5;
    
    public static class generator implements Runnable{
        public Thread thread;
        public Map map;
        public stage currStage;
        public generator(Map m){
            map=m;
            currStage=new roomStage(map);
        }
        public void start(final long maxTime){
            thread = new Thread(this,"generationThread");
            thread.start();
            new Thread(new Runnable(){
                public synchronized void run() {
                    try {
                        wait(maxTime);
                    } catch (Exception e) {}
                    stop();
                    start(maxTime);
                }
            });
        }
        public void stop(){
            try{
                Thread stopThread = new Thread(new Runnable(){
                    public void run() {
                        while(thread.isAlive())
                            try{currStage.running=false;}catch(Exception e){}
                    }
                });
                stopThread.start();
                thread.interrupt();
            }catch(Exception e){}
        }
        public void run() {
            while(currStage!=null){
                currStage.start();
                while(currStage.running){
                    currStage.run();
                }
                currStage=currStage.nextStage();
            }
        }
        public void waitUntilDone(){
            while(currStage!=null);
        }
    }
    public static abstract class stage{
        public boolean running=false;
        public Map m;
        public stage(Map m){
            this.m=m;
        }
        public void start(){
            running=true;
        }
        public abstract void run();
        public abstract stage nextStage();
    }
    public static class roomStage extends stage{
        public int roomAttempts;
        public int roomCount;
        public roomStage(Map m) {
            super(m);
            roomAttempts = 0;
            roomCount = 0;
        }
        public void run() {
            if(m.region==1)
                m.setRegion(2);
            int x=(int)((m.width -roomMinSize)*Math.random()/2)*2;
            int y=(int)((m.height-roomMinSize)*Math.random()/2)*2;
            int w=(int)(roomMinSize+(roomMaxSize*Math.random()))*2;
            int h=(int)(roomMinSize+(roomMaxSize*Math.random()))*2;
            roomAttempts++;
            if(roomAttempts>=maxRoomAttempts){
                roomAttempts=0;
                roomCount++;
            }
            {
                boolean isOpen=true;
                for(int xp=x;xp<=x+w;xp++)
                    for(int yp=y;yp<=y+h;yp++){
                        if(m.getTile(xp, yp)!=0)
                            isOpen=false;
                    }
                if(!isOpen)
                    return;
            }
            for(int xp=x;xp<=x+w;xp++)
                for(int yp=y;yp<=y+h;yp++){
                    m.setTile(xp,yp);
                }
            roomAttempts=0;
            roomCount++;
            m.nextRegion();
            if(roomCount>=maxRoomCount||roomAttempts>=maxRoomAttempts)
                running=false;
        }
        public stage nextStage() {
            return new passagesStage(m);
        }
    }
    public static class passagesStage extends stage{
        public ArrayList<Vector2f> Vector2fitions;
        public boolean started;
        public passagesStage(Map m) {
            super(m);
            Vector2fitions = new ArrayList<Vector2f>();
            started = false;
        }
        public void run() {
            m.setRegion(1);
            if(!started&&Vector2fitions.isEmpty()){
                Vector2f Vector2f = new Vector2f(m.width*Math.random(),m.height*Math.random()).mult(0.5f).mult(2);
                if(m.getTile((int)Vector2f.x, (int)Vector2f.y)==0){
                    started=true;
                    m.setTile((int)Vector2f.x, (int)Vector2f.y);
                    Vector2fitions.add(Vector2f);
                }
                return;
            }
            if(Vector2fitions.isEmpty()){
                boolean availableSpace = false;
                for(int x=0;x<m.width;x+=2)
                    for(int y=0;y<m.height;y+=2){
                        if(m.getTile(x, y)==0)
                            availableSpace=true;
                    }
                if(availableSpace){
                    for(int x=0;x<m.width;x+=2)
                        for(int y=0;y<m.height;y+=2){
                            Vector2f Vector2f = new Vector2f(m.width*Math.random(),m.height*Math.random()).round(2);
                            if(m.getTile((int)Vector2f.x, (int)Vector2f.y)==1)
                                if(m.getTile((int)Vector2f.x, (int)Vector2f.y-2)==0||m.getTile((int)Vector2f.x+2, (int)Vector2f.y)==0||m.getTile((int)Vector2f.x, (int)Vector2f.y+2)==0||m.getTile((int)Vector2f.x-2, (int)Vector2f.y)==0)
                                    Vector2fitions.add(Vector2f);
                        }
                }else{
                    running=false;
                }
            }else{
                for (int i=0;i<Vector2fitions.size();i++) {
                    Vector2f p = Vector2fitions.get(i);
                    int rand = (int)(4*Math.random());
                    switch (rand) {
                        case 0:
                            if(m.getTile((int)p.x, (int)p.y-2)==0){
                                m.setTile((int)p.x,(int)p.y-1);
                                m.setTile((int)p.x,(int)p.y-2);
                                p.y-=2;
                            }   break;
                        case 1:
                            if(m.getTile((int)p.x+2, (int)p.y)==0){
                                m.setTile((int)p.x+1,(int)p.y);
                                m.setTile((int)p.x+2,(int)p.y);
                                p.x+=2;
                            }   break;
                        case 2:
                            if(m.getTile((int)p.x, (int)p.y+2)==0){
                                m.setTile((int)p.x,(int)p.y+1);
                                m.setTile((int)p.x,(int)p.y+2);
                                p.y+=2;
                            }   break;
                        case 3:
                            if(m.getTile((int)p.x-2, (int)p.y)==0){
                                m.setTile((int)p.x-1,(int)p.y);
                                m.setTile((int)p.x-2,(int)p.y);
                                p.x-=2;
                            }   break;
                        default:
                            break;
                    }
                    Vector2fitions.remove(p);
                }
            }
        }
        public stage nextStage() {
            return new doorStage(m);
        }
    }
    public static class doorStage extends stage{
        public ArrayList<Vector2f> Vector2fitions;
        public boolean started;
        public doorStage(Map m) {
            super(m);
            Vector2fitions = new ArrayList<Vector2f>();
            started = false;
        }
        public void run() {
            m.setRegion(-1);
            if(!started){
                Vector2f Vector2f = new Vector2f(m.width*Math.random(),m.height*Math.random());
                if(m.getTile((int)Vector2f.x, (int)Vector2f.y)!=0){
                    m.setTile((int)Vector2f.x,(int)Vector2f.y);
                    started=true;
                }
                return;
            }
            boolean anyLeft=false;
            boolean found=false;
            for(int x=0;x<m.width;x++)
                for(int y=0;y<m.height;y++){
                    if(m.getTile(x, y)==-1){
                        if(m.getTile(x, y-1)>0){
                            m.setTile(x, y-1);
                            found=true;
                        }
                        if(m.getTile(x+1, y)>0){
                            m.setTile(x+1, y);
                            found=true;
                        }
                        if(m.getTile(x, y+1)>0){
                            m.setTile(x, y+1);
                            found=true;
                        }
                        if(m.getTile(x-1, y)>0){
                            m.setTile(x-1, y);
                            found=true;
                        }
                    }
                    if(m.getTile(x, y)!=-1&&m.getTile(x, y)!=0)
                        anyLeft=true;
                }
            if(!anyLeft){
                running=false;
                return;
            }
            if(!found)
                for(int i=0;i<1;i++){
                    //System.out.println(Math.random());
                    Vector2f Vector2f = new Vector2f(m.width*Math.random(),m.height*Math.random());
                    if(m.getTile((int)Vector2f.x, (int)Vector2f.y)==-1){
                        double c = 0.5;
                        if(Math.random()<c&&m.getTile((int)Vector2f.x, (int)Vector2f.y+2)>0){
                            m.setTile((int)Vector2f.x, (int)Vector2f.y+1);
                        }
                        if(Math.random()<c&&m.getTile((int)Vector2f.x, (int)Vector2f.y-2)>0){
                            m.setTile((int)Vector2f.x, (int)Vector2f.y-1);
                        }
                        if(Math.random()<c&&m.getTile((int)Vector2f.x+2, (int)Vector2f.y)>0){
                            m.setTile((int)Vector2f.x+1, (int)Vector2f.y);
                        }
                        if(Math.random()<c&&m.getTile((int)Vector2f.x-2, (int)Vector2f.y)>0){
                            m.setTile((int)Vector2f.x-1, (int)Vector2f.y);
                        }
                    }
                }
        }
        public stage nextStage() {
            return new deadEndRemovingStage(m);
        }
    }
    public static class deadEndRemovingStage extends stage{
        public deadEndRemovingStage(Map m) {
            super(m);
        }
        public void run() {
            int found=0;
            for(int x=0;x<m.width;x++)
                for(int y=0;y<m.height;y++){
                    int n=0;
                    n+=((!m.inbounds(x, y-1))||m.getTile(x, y-1)==0)?1:0;
                    n+=((!m.inbounds(x, y+1))||m.getTile(x, y+1)==0)?1:0;
                    n+=((!m.inbounds(x-1, y))||m.getTile(x-1, y)==0)?1:0;
                    n+=((!m.inbounds(x+1, y))||m.getTile(x+1, y)==0)?1:0;
                    if(m.getTile(x, y)!=0&&n==3){
                        m.setTile(x, y, 0);
                        found++;
                    }
                }
            if(found==0)
                running=false;
        }
        public stage nextStage() {
            return null;//new regionStage(m);
        }
    }
}
