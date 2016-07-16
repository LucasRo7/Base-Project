package com.LS7.util.image;
 
import java.awt.Color;
import java.util.Random;
 
public class Pixel {
    
	public static class hslPixel{
	    public double hue=0;
	    public double sat=0;
	    public double lig=0;
	    public double alpha=255;
	    public hslPixel(){
	        hue=0;sat=0;lig=0;
	    }
	    public hslPixel(double h, double s, double l){
	        hue=h;sat=s;lig=l;
	    }
	    public static double hue2rgb(double p, double q, double t){
	        if(t < 0) t += 1.0;
	        if(t > 1) t -= 1.0;
	        if(t < 1.0/6) return p + (q - p) * 6.0 * t;
	        if(t < 1.0/2) return q;
	        if(t < 2.0/3) return p + (q - p) * (2/3.0 - t) * 6;
	        return p;
	    }
	    public static Pixel HSLtoRGB(double h, double s, double l) {
	        Pixel result = new Pixel();
	        if(s==0){
	            l*=255;
	            result = new Pixel((int)l,(int)l,(int)l);
	        }else{
	            double q = (l<0.5) ? (l*(1+s)) : (l+s-l*s);
	            double p = 2 * l - q;
	            result.red=(int)(hue2rgb(p, q, h + 1/3.0)*255);
	            result.green=(int)(hue2rgb(p, q, h)*255);
	            result.blue=(int)(hue2rgb(p, q, h - 1/3.0)*255);
	        }
	        return result;
	    }
	    public hslPixel RGBtoHSL(int red, int green, int blue) {
	    	hslPixel result = new hslPixel();
	        double r = red/255.0;
	        double g = green/255.0;
	        double b = blue/255.0;
	        double cMax=Math.max(Math.max(r, g), b);
	        double cMin=Math.min(Math.min(r, g), b);
	        double delta = cMax-cMin;
	        if(delta==0){
	        	result.hue=0;
	        	result.sat=0;
	        }
	        if(cMax==r){
	        	result.hue=(int) (60*(((g-b)/delta)%6));
	        }
	        if(cMax==g){
	        	result.hue=(int) (60*(((b-r)/delta)+2));
	        }
	        if(cMax==b){
	        	result.hue=(int) (60*(((r-g)/delta)+4));
	        }
	        result.lig=(int) ((cMax+cMin)/2);
	        if(delta!=0){
	        	result.sat=(int) (delta/(1-Math.abs(2*result.lig-1)));
	        }
	        return result;
	    }
	    public static Pixel HSLtoRGB(hslPixel hsl) {
	        return HSLtoRGB(hsl.hue,hsl.sat,hsl.lig);
	    }
	    public hslPixel RGBtoHSL(Pixel rgb) {
	    	return RGBtoHSL(rgb.red,rgb.green,rgb.blue);
	    }
	}
	
    public static Pixel white=new Pixel(255,255,255);
    public static Pixel gray=new Pixel(128,128,128);
    public static Pixel black=new Pixel(0,0,0);
    public static Pixel transparent=new Pixel(0,0,0,0);
 
    public int red=0;
    public int green=0;
    public int blue=0;
    public int alpha=255;
 
    public Pixel(){}
    public Pixel(int hex){
        alpha=(hex>>24)&0xff;
        red  =(hex>>16)&0xff;
        green=(hex>>8 )&0xff;
        blue =(hex    )&0xff;
        limit();
    }
    public Pixel(int r, int g, int b){
        red=r;green=g;blue=b;alpha=255;
        limit();
    }
    public Pixel(int r, int g, int b,int a){
        red=r;green=g;blue=b;alpha=a;
        limit();
    }
    public void set(int r, int g, int b){
        red=r;green=g;blue=b;alpha=255;
        limit();
    }
    public void set(int r, int g, int b,int a){
        red=r;green=g;blue=b;alpha=a;
        limit();
    }
    public int getHex(){
        limit();
        return ((alpha<<24)|(red<<16)|(green<<8)|blue);
    }
     
    public void limit(){
        min(0);
        max(255);
    }
    public static Pixel random(Random rand) {
        return new Pixel(rand.nextInt());
    }
    public static Pixel random(Random rand, int min) {
        Pixel pix = new Pixel(rand.nextInt());
        if(pix.red<min&&pix.green<min&&pix.blue<min){
            if(pix.red>pix.green){
                if(pix.red>pix.blue){
                    pix.red=min;
                }else{
                    if(pix.blue>pix.green){
                        pix.blue=min;
                    }else{
                        pix.green=min;
                    }
                }
            }else{
                if(pix.blue>pix.green){
                    pix.blue=min;
                }else{
                    pix.green=min;
                }
            }
        }
        return pix;
    }
    public Pixel grayscale() {
        int res = (red+green+blue)/3;
        red=green=blue=res;
        return this;
    }
 
    public Pixel light(Pixel light){
        Pixel res = this.copy();
        int r = light.red;
        int g = light.green;
        int b = light.blue;
        if(res.red  >r)res.red  =r;
        else res.red  +=(r-res.red  )/2;
        if(res.green>g)res.green=g;
        else res.green+=(g-res.green)/2;
        if(res.blue >b)res.blue =b;
        else res.blue +=(b-res.blue )/2;
        return res;
    }
    public Pixel mid(Pixel p){
        if(alpha==0&&p.alpha==0)
            return Pixel.transparent;
        if(alpha==0)
            return this.copy();
        if(p.alpha==0)
            return p.copy();
        return new Pixel((p.red+red)/2,(p.green+green)/2,(p.blue+blue)/2);
    }
    public Pixel sum(Pixel p){
        return new Pixel(p.red+red,p.green+green,p.blue+blue);
    }
    public Pixel sub(Pixel p){
        return new Pixel(red-p.red,green-p.green,blue-p.blue);
    }
    public Pixel mult(double num){
        return mult(num,num,num);
    }
    public Pixel mult(double alpha,double colors){
        return mult(alpha,colors,colors,colors);
    }
    public Pixel mult(double r,double g,double b){
        return mult(1.0,r,g,b);
    }
    public Pixel mult(double a,double r,double g,double b){
        return new Pixel((int)(red*r),(int)(green*g),(int)(blue*b),(int)(alpha*a));
    }
    public Pixel min(int r,int g,int b,int a){
        if(red<r)red=r;
        if(green<g)green=g;
        if(blue<b)blue=b;
        if(alpha<a)alpha=a;
        return this;
    }
    public Pixel max(int r,int g,int b,int a){
        if(red>r)red=r;
        if(green>g)green=g;
        if(blue>b)blue=b;
        if(alpha>a)alpha=a;
        return this;
    }
    public Pixel min(int x){
        min(x,x,x,x);
        return this;
    }
    public Pixel max(int x){
        max(x,x,x,x);
        return this;
    }
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Pixel))
            return false;
        Pixel e = (Pixel) obj;
        return ((e.red==this.red)&&(e.green==this.green)&&(e.blue==this.blue));
    }
    public boolean isBrighter(Pixel other){
        return diference(other)>0;
    }
    public boolean isDarker(Pixel other){
        return diference(other)<0;
    }
    public double diference(Pixel other){
    	return (red-other.red)/**0.85*/+(green-other.green)+(blue-other.blue)/**0.7*/;
    }
    public Pixel copy() {
        return new Pixel(red, green, blue);
    }
    public Color getColor(){
        limit();
        return new Color(red,green,blue);
    }
    public Color getColor(int alpha){
        limit();
        return new Color(red,green,blue,255-alpha);
    }
    @Deprecated
    public double difference(Pixel other) {
        double r = Math.abs(red-other.red)/255.0;
        double g = Math.abs(green-other.green)/255.0;
        double b = Math.abs(blue-other.blue)/255.0;
        double a = Math.abs(alpha-other.alpha)/255.0;
        return (r+b+g+a)/4.0;
    }
    public Pixel over(Pixel other){
    	double a = alpha/255.0;
    	double oa = other.alpha/255.0;
        int nr = (int)(red*a+other.red*oa*(1-a));
        int ng = (int)(green*a+other.green*oa*(1-a));
        int nb = (int)(blue*a+other.blue*oa*(1-a));
        return new Pixel(nr,ng,nb);
    }
}