package com.LS7.util.image;
 
import java.awt.Color;
import java.util.Random;
/**
 * Class made for handling colors.
 * (i should rename it to "Colors" someday)
 * @author LucasRo7
 */
public class Pixel {
    /**
     * Used to store HSL colors(instead of RGB)
     * It also has methods to convert from HSL to RGB and vice-versa
     * 
     * @author LucasRo7
     */
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
    public Pixel(int a, int r, int g, int b){
        red=r;green=g;blue=b;alpha=a;
        limit();
    }
    public void set(int r, int g, int b){
        red=r;green=g;blue=b;alpha=255;
        limit();
    }
    public void set(int a, int r, int g, int b){
        red=r;green=g;blue=b;alpha=a;
        limit();
    }
    /**
     * Puts all ARGB values into a single hexadecimal value. This 
     * can be undone by using "new Pixel(value)".
     * @return The hexadecimal value of this pixel
     */
    public int getHex(){
        limit();
        return ((alpha<<24)|(red<<16)|(green<<8)|blue);
    }
    /**
     * Limit each channel of this pixel to be positive and smaller than 256
     */
    public void limit(){
        min(0);
        max(255);
    }
    /**
     * Generates a random pixel and return s it
     * @param random used to generate the values
     * @return A randomly generated pixel
     */
    public static Pixel random(Random rand) {
        return new Pixel(rand.nextInt());
    }
    /**
     * Same as random(...), but makes sure that at least one channel(red, 
     * green or blue) remains above the minimum value
     * @param random used to generate the values
     * @param min minimum value
     * @return A randomly generated pixel with at least one channel above 
     * the minimum value
     */
    public static Pixel random(Random random, int min) {
        Pixel pix = new Pixel(random.nextInt());
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
    /**
     * Sets all color channels to the average of them
     * @return this pixel
     */
    public Pixel grayscale() {
        int res = (red+green+blue)/3;
        red=green=blue=res;
        return this;
    }
    // This was used to project light into this pixel
    /*public Pixel light(Pixel light){
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
    }*/
    
    // i dont even know why i used this :/
    /*public Pixel mid(Pixel p){
        if(alpha==0&&p.alpha==0)
            return Pixel.transparent;
        if(alpha==0)
            return this.copy();
        if(p.alpha==0)
            return p.copy();
        return new Pixel((p.red+red)/2,(p.green+green)/2,(p.blue+blue)/2);
    }*/
    /**
     * Sums this pixel's channels and another pixel's channels and 
     * returns a new Pixel with the results
     * @param other the other pixel to be summed with
     * @return the result of each color channel of both pixels summed
     */
    public Pixel sum(Pixel other){
        return new Pixel(other.red+red,other.green+green,other.blue+blue);
    }
    /**
     * Subtracts this pixel's channels and another pixel's channels and 
     * returns a new Pixel with the results
     * @param other the other pixel to be subtracted with
     * @return the result of each color channel of both pixels subtracted
     */
    public Pixel sub(Pixel p){
        return new Pixel(red-p.red,green-p.green,blue-p.blue);
    }
    /**
     * Multiplies this pixel's color channels by the specified number and returns 
     * it in a new pixel
     * @param num the number to multiply
     * @return a new pixel identical to this one, but with all channels multiplied by a number
     */
    public Pixel mult(double num){
        return mult(num,num,num);
    }
    /**
     * Multiplies this pixel's alpha and color channels by two numbers(alpha and colors respectively) 
     * and returns the new values in a new pixel.
     * @param alpha the number to multiply
     * @param colors the number to multiply
     * @return a new pixel identical to this one, but with its channels multiplied by the supplied numbers
     */
    public Pixel mult(double alpha,double colors){
        return mult(alpha,colors,colors,colors);
    }
    /**
     * Multiplies this pixel's color channels by three numbers(r,g and b respectively) 
     * and returns the new values in a new pixel.
     * @param r the number to multiply the red channel
     * @param g the number to multiply the green channel
     * @param b the number to multiply the blue channel
     * @return a new pixel identical to this one, but with its channels multiplied by the supplied numbers
     */
    public Pixel mult(double r,double g,double b){
        return mult(1.0,r,g,b);
    }
    /**
     * Multiplies this pixel's alpha and color channels by three numbers(a, r, g and b respectively) 
     * and returns the new values in a new pixel.
     * @param a the number to multiply the alpha channel
     * @param r the number to multiply the red channel
     * @param g the number to multiply the green channel
     * @param b the number to multiply the blue channel
     * @return a new pixel identical to this one, but with its channels multiplied by the supplied numbers
     */
    public Pixel mult(double a,double r,double g,double b){
        return new Pixel((int)(red*r),(int)(green*g),(int)(blue*b),(int)(alpha*a));
    }
    /**
     * Makes sure that each channel of this pixel is bigger than supplied values.
     * @return this pixel
     */
    public Pixel min(int a, int r,int g,int b){
        if(alpha<a)alpha=a;
        if(red<r)red=r;
        if(green<g)green=g;
        if(blue<b)blue=b;
        return this;
    }
    /**
     * Makes sure that each channel of this pixel is smaller than supplied values.
     * @return this pixel
     */
    public Pixel max(int r,int g,int b,int a){
        if(red>r)red=r;
        if(green>g)green=g;
        if(blue>b)blue=b;
        if(alpha>a)alpha=a;
        return this;
    }
    /**
     * Makes sure that all channels of this pixel are bigger than supplied values.
     * @return this pixel
     */
    public Pixel min(int x){
        min(x,x,x,x);
        return this;
    }
    /**
     * Makes sure that all channels of this pixel are smaller than supplied values.
     * @return this pixel
     */
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
    /**
     * Returns true if this pixel is brighter than supplied one
     * @param other the other pixel
     * @return true if this pixel is brighter than the other one and false otherwise.
     */
    public boolean isBrighter(Pixel other){
        return difference(other)>0;
    }
    /**
     * Returns true if this pixel is darker than supplied one
     * @param other the other pixel
     * @return true if this pixel is darker than the other one and false otherwise.
     */
    public boolean isDarker(Pixel other){
        return difference(other)<0;
    }
    /**
     * Returns the sum of the difference between each color channel of this pixel and another.
     * Also, p1.difference(p2)==-p2.difference(p1) should always be true
     * @param other the other pixel
     * @return the sum of the difference between each color channel of both pixels
     */
    public double difference(Pixel other){
    	// these coefficients(*0.85 and *0.7) were here because blue looks 
    	// darker than red and red looks darker than green and i wanted to fix it.
    	return (red-other.red)/* *0.85 */+(green-other.green)+(blue-other.blue)/* *0.7 */;
    }
    /**
     * Returns a new instance of this pixel with the same channel values.
     * @return a copy of this pixel.
     */
    public Pixel copy() {
        return new Pixel(alpha, red, green, blue);
    }
    /**
     * Returns a Color version of this pixel, so it can be used in the Graphics class
     * @return a Color to be used in Graphics
     */
    public Color getColor(){
        limit();
        return new Color(red,green,blue,alpha);
    }
    /**
     * Returns a Color version of this pixel with specified alpha, so it can be used in the Graphics class
     * @return a Color to be used in Graphics
     */
    public Color getColor(int alpha){
        limit();
        return new Color(red,green,blue,255-alpha);
    }
    /**
     * Draws this pixel on top of another, this should be used when drawing a transparent pixel on top of another.
     * @param other the other pixel
     * @return a new pixel with the description above
     */
    public Pixel over(Pixel other){
    	double a = alpha/255.0;
    	double oa = other.alpha/255.0;
        int nr = (int)(red*a+other.red*oa*(1-a));
        int ng = (int)(green*a+other.green*oa*(1-a));
        int nb = (int)(blue*a+other.blue*oa*(1-a));
        return new Pixel(nr,ng,nb);
    }
    /**
     * Draws a pixel on top of this one, this should be used when drawing a transparent pixel on top of this one.
     * @param other the other pixel
     * @return a new pixel with the description above
     */
    public Pixel under(Pixel other){
    	return other.over(this);
    }
}