package com.LS7.util.Data;

import com.LS7.util.image.Pixel;
import java.util.HashMap;
import java.util.Random;

public class Map{
    public HashMap<Integer,Pixel> regionToColor;
    public int[][] map;
    public int region;
    public int width;
    public int height;
    public Map(int w, int h){
        width=w;
        height=h;
        regionToColor = new HashMap<Integer,Pixel>();
        map = new int[width][height];
        region = 0;
        for (int[] columns : map)
            for (int y = 0; y < columns.length; y++) {
                columns[y] = 0;
            }
    }
    public void setTile(int x, int y, int tile){
        if(inbounds(x,y))
            map[x][y]=tile;
    }
    public void setTile(int x, int y){
        if(inbounds(x,y))
            map[x][y]=region;
    }
    public int getTile(int x, int y){
        if(inbounds(x,y))
            return map[x][y];
        else
            return -1;
    }
    public boolean inbounds(int x, int y){
        return (x>=0)&&(x<width)&&(y>=0)&&(y<height);
    }
    public void setRegion(int i){
        //if(i>=0)
            region=i;
    }
    public void nextRegion(){
        region++;
    }
    public boolean block(int x, int y){
        return getTile(x,y)!=0;
    }
    public boolean block(double x, double y){
        return block((int)x,(int)y);
    }
    public double stepSize(){
        return 1;
    }
    public Pixel getRegionColor(int x, int y) {
        int r = getTile(x,y);
        if(r==0)
            return Pixel.black;
        if(regionToColor.containsKey(r)){
            return regionToColor.get(r);
        }else{
            Pixel color = Pixel.random(new Random(),128);
            regionToColor.put(r, color);
            return color;
        }
    }
}
