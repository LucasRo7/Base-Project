package com.LS7.util.Math;

public class Vector2f {
    public float x,y;
    public Vector2f(float xx, float yy){
        x=xx;
        y=yy;
    }
	public Vector2f(double xx, double yy){
        x=(float)xx;
        y=(float)yy;
    }
    public Vector2f sum(float d) {
        x+=d;
        y+=d;
        return this;
    }
    public Vector2f sum(float xx, float yy) {
        x+=xx;
        y+=yy;
        return this;
    }
    public Vector2f sum(Vector2f other) {
        x+=other.x;
        y+=other.y;
        return this;
    }
    public Vector2f sub(float d) {
        x-=d;
        y-=d;
        return this;
    }
    public Vector2f sub(float xx, float yy) {
        x-=xx;
        y-=yy;
        return this;
    }
    public Vector2f sub(Vector2f other) {
        x-=other.x;
        y-=other.y;
        return this;
    }
    public Vector2f mult(float d) {
        x*=d;
        y*=d;
        return this;
    }
    public Vector2f mult(float xx, float yy) {
        x*=xx;
        y*=yy;
        return this;
    }
    public Vector2f mult(Vector2f other) {
        x*=other.x;
        y*=other.y;
        return this;
    }
    public Vector2f div(float d) {
        x/=d;
        y/=d;
        return this;
    }
    public Vector2f div(float xx, float yy) {
        x/=xx;
        y/=yy;
        return this;
    }
    public Vector2f div(Vector2f other) {
        x/=other.x;
        y/=other.y;
        return this;
    }
    public Vector2f round(float div){
    	x/=div;
    	y/=div;
    	x=Math.round(x);
    	y=Math.round(y);
    	x*=div;
    	y*=div;
		return this;
    }
	public static Vector2f transformCoordinates(Vector2f coord, Matrix3f transMatrix) {
		float x=(coord.x*transMatrix.get(0))+(coord.y*transMatrix.get(1))+transMatrix.get(2);
		float y=(coord.x*transMatrix.get(3))+(coord.y*transMatrix.get(4))+transMatrix.get(5);
		float w=(coord.x*transMatrix.get(6))+(coord.y*transMatrix.get(7))+transMatrix.get(8);
		return new Vector2f(x/w,y/w);
	}
    @Override
    public String toString(){
        return "Vector2f("+x+":"+y+")";
    }
    public Vector2f copy(){
        return new Vector2f(x,y);
    }
    public float magnitude(){
        return (float)(Math.sqrt(x*x+y*y));
    }
}
