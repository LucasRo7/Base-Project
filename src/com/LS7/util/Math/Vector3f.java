package com.LS7.util.Math;

public class Vector3f {
	
	public static Vector3f up    = new Vector3f( 0, 0, 1);
	public static Vector3f down  = new Vector3f( 0, 0,-1);
	
	public static Vector3f north = new Vector3f( 0, 1, 0);
	public static Vector3f south = new Vector3f( 0,-1, 0);
	
	public static Vector3f east  = new Vector3f( 1, 0, 0);
	public static Vector3f west  = new Vector3f(-1, 0, 0);
	public static Vector3f zero  = new Vector3f( 0, 0, 0);
	
    public float x,y,z;
    public Vector3f(float xx, float yy,float zz){
        x=xx;
        y=yy;
        z=zz;
    }
	public Vector3f(double xx, double yy, double zz){
        x=(float)xx;
        y=(float)yy;
        z=(float)zz;
    }
    public Vector3f sum(float d) {
        x+=d;
        y+=d;
        z+=d;
        return this;
    }
    public Vector3f sum(float xx, float yy,float zz) {
        x+=xx;
        y+=yy;
        z+=zz;
        return this;
    }
    public Vector3f sum(Vector3f other) {
        x+=other.x;
        y+=other.y;
        z+=other.z;
        return this;
    }
    public Vector3f sub(float d) {
        x-=d;
        y-=d;
        z-=d;
        return this;
    }
    public Vector3f sub(float xx, float yy,float zz) {
        x-=xx;
        y-=yy;
        z-=zz;
        return this;
    }
    public Vector3f sub(Vector3f other) {
        x-=other.x;
        y-=other.y;
        z-=other.z;
        return this;
    }
    public Vector3f mult(float d) {
        x*=d;
        y*=d;
        z*=d;
        return this;
    }
    public Vector3f mult(float xx, float yy,float zz) {
        x*=xx;
        y*=yy;
        z*=zz;
        return this;
    }
    public Vector3f mult(Vector3f other) {
        x*=other.x;
        y*=other.y;
        z*=other.z;
        return this;
    }
    public Vector3f div(float d) {
        x/=d;
        y/=d;
        z/=d;
        return this;
    }
    public Vector3f div(float xx, float yy,float zz) {
        x/=xx;
        y/=yy;
        z/=zz;
        return this;
    }
    public Vector3f div(Vector3f other) {
        x/=other.x;
        y/=other.y;
        z/=other.z;
        return this;
    }
    public Vector3f round(float div){
    	x/=div;
    	y/=div;
    	z/=div;
    	x=Math.round(x);
    	y=Math.round(y);
    	z=Math.round(z);
    	x*=div;
    	y*=div;
    	z*=div;
		return this;
    }
	public float length(){
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	public static Vector3f cross(Vector3f left, Vector3f right) {
		float x=left.y*right.z-left.z*right.y;
		float y=left.z*right.x-left.x*right.z;
		float z=left.x*right.y-left.y*right.x;
		return new Vector3f(x,y,z);
	}
	public static float dot(Vector3f left, Vector3f right) {
		return (left.x*right.x+left.y*right.y+left.z*right.z);
	}
	public Vector3f normalize() {
		float len = this.length();
		if(len==0){
			return this;
		}
		float num = 1.0f/len;
		x*=num;
		y*=num;
		z*=num;
		return this;
	}
	public static Vector3f transformCoordinates(Vector3f coord, Matrix4f transMatrix) {
		float x=(coord.x*transMatrix.get(0))+(coord.y*transMatrix.get(4))+(coord.z*transMatrix.get(8))+transMatrix.get(12);
		float y=(coord.x*transMatrix.get(1))+(coord.y*transMatrix.get(5))+(coord.z*transMatrix.get(9))+transMatrix.get(13);
		float z=(coord.x*transMatrix.get(2))+(coord.y*transMatrix.get(6))+(coord.z*transMatrix.get(10))+transMatrix.get(14);
		float w=(coord.x*transMatrix.get(3))+(coord.y*transMatrix.get(7))+(coord.z*transMatrix.get(11))+transMatrix.get(15);
		return new Vector3f(x/w,y/w,z/w);
	}
    @Override
    public String toString(){
        return "Vector3f("+x+":"+y+":"+z+")";
    }
	public void set(float xx, float yy, float zz) {
        x=xx;
        y=yy;
        z=zz;
	}
	public void set(double xx, double yy, double zz) {
        x=(float)xx;
        y=(float)yy;
        z=(float)zz;
	}
	public Vector3f copy() {
		return new Vector3f(x,y,z);
	}
}
