package com.LS7.util.Math;

public class Matrix3f {
    public float[] e = new float[9];
    public void empty(){
    	for(int i=0;i<9;i++)
    		e[i]=0.0f;
    }
    public Matrix3f(float f){
    	empty();
    	e[0]=f;
    	e[4]=f;
    	e[8]=f;
    }
    public Matrix3f add(Matrix3f other){
    	for(int i=0;i<9;i++)
    		e[i]+=other.e[i];
		return this;
    }
    public Matrix3f mult(float n){
    	for(int i=0;i<9;i++)
    		e[i]*=n;
		return this;
    }
    public Matrix3f mult(Matrix3f other){
    	Matrix3f res = new Matrix3f(0);
    	for(int x=0;x<3;x++)
    		for(int y=0;y<3;y++){
    			float r = 0;
        		for(int i=0;i<3;i++){
        			r+=e[i+y*3]*other.e[x+i*3];
        		}
        		res.e[x+y*3]=r;
    		}
    	e=res.e;
		return this;
    }
    public static Matrix3f identity(){
    	return new Matrix3f(1.0f);
    }
	public float get(int x, int y) {
		return e[x+y*3];
	}
	public float get(int n) {
		return e[n];
	}
	public void set(int x, int y, float value) {
		e[x+y*3]=value;
	}
	public void set(int n, float value) {
		e[n]=value;
	}
}
