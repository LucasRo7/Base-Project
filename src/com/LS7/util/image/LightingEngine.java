package com.LS7.util.image;

import java.util.ArrayList;

import com.LS7.util.Data.Map;

public class LightingEngine {

    public static abstract class lightmap extends Map{
        public lightmap(int w, int h) {
            super(w, h);
        }
        public abstract boolean setLight(double x, double y, Pixel color);
        public abstract double blockFadeoff(double radius);
        public abstract double normalFadeoff(double radius);
    }

    public static void light(lightmap map, double x, double y, double radius, Pixel color){
        if(map.setLight(x, y,Pixel.black))
            return;
        ArrayList<Double> angles = new ArrayList<Double>();
        boolean[][] process = new boolean[(int)(radius*2+1)][(int)(radius*2+1)];
        for(int xo=(int) -radius;xo<=radius;xo++)
            for(int yo=(int) -radius;yo<=radius;yo++){
                if(xo==(int)-radius||xo==(int)radius||yo==(int)-radius||yo==(int)radius){
                    double angle = Math.atan2(xo,yo);
                    angles.add(angle);
                }
                if(Math.sqrt(xo*xo+yo*yo)<radius)
                    process[(int) (xo+radius)][(int) (yo+radius)]=true;
            }
        for(int i=0;i<angles.size();i++){
            double angle = angles.get(i);
            double sin = Math.sin(angle);
            double cos = Math.cos(angle);
            double intensity=1.0;
            for(double mag=0;(mag<radius)&&(intensity>0);mag+=map.stepSize()){
                intensity-=map.normalFadeoff(radius);
                int nx = (int) (mag*sin/map.stepSize());
                int ny = (int) (mag*cos/map.stepSize());
                if(map.block(x+nx, y+ny))
                    intensity-=map.blockFadeoff(radius);
                if(process[(int) (nx+radius)][(int) (ny+radius)]){
                    process[(int) (nx+radius)][(int) (ny+radius)]=false;
                    if(map.setLight(x+nx, y+ny, color.mult(intensity,intensity)))
                        break;
                }
            }
        }
    }
    public static double nextStep(double sin,double cos,double nextX, double nextY){
        double sx = nextX/sin;
        double sy = nextY/cos;
        return sx>sy?sy:sx;
    }

}
