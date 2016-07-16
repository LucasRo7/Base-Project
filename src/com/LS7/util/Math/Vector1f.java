package com.LS7.util.Math;

public class Vector1f {
    public float x;
    public Vector1f(float xx){
        x=xx;
    }
	public Vector1f(double xx){
        x=(float)xx;
    }
    public Vector1f sum(float d) {
        x+=d;
        return this;
    }
    public Vector1f sub(float d) {
        x-=d;
        return this;
    }
    public Vector1f mult(float d) {
        x*=d;
        return this;
    }
    public Vector1f div(float d) {
        x/=d;
        return this;
    }
    public Vector1f round(float div){
    	x/=div;
    	x=Math.round(x);
    	x*=div;
		return this;
    }
	public static Vector1f transformCoordinates(Vector1f coord, Matrix2f transMatrix) {
		float x=(coord.x*transMatrix.get(0))+transMatrix.get(1);
		float w=(coord.x*transMatrix.get(2))+transMatrix.get(3);
		return new Vector1f(x/w);
	}
    @Override
    public String toString(){
        return "Vector2f("+x+")";
    }
}
