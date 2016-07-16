package com.LS7.util.Math;

public class Matrix2f {
    public float[] e = new float[4];
    public void empty(){
    	for(int i=0;i<4;i++)
    		e[i]=0.0f;
    }
    public Matrix2f(float f){
    	empty();
    	e[0]=f;
    	e[3]=f;
    }
    public Matrix2f add(Matrix2f other){
    	for(int i=0;i<4;i++)
    		e[i]+=other.e[i];
		return this;
    }
    public Matrix2f mult(float n){
    	for(int i=0;i<4;i++)
    		e[i]*=n;
		return this;
    }
    public Matrix2f mult(Matrix2f other){
    	Matrix2f res = new Matrix2f(0);
    	for(int x=0;x<2;x++)
    		for(int y=0;y<2;y++){
    			float r = 0;
        		for(int i=0;i<2;i++){
        			r+=e[i+y*2]*other.e[x+i*2];
        		}
        		res.e[x+y*2]=r;
    		}
    	e=res.e;
		return this;
    }
    public static Matrix2f identity(){
    	return new Matrix2f(1.0f);
    }
	public float get(int x, int y) {
		return e[x+y*2];
	}
	public float get(int n) {
		return e[n];
	}
	public void set(int x, int y, float value) {
		e[x+y*2]=value;
	}
	public void set(int n, float value) {
		e[n]=value;
	}
}
