package com.LS7.util.Math;

public class Matrix4f implements Cloneable{
	
	private float[] e = new float[16];

	public Matrix4f(){
		for(int i = 0; i<e.length;i++){
			e[i]=0.0f;
		}
	}
	public Matrix4f(float d){
		for(int i = 0; i<e.length;i++){
			e[i]=0.0f;
		}
		e[0+0*4]=d;
		e[1+1*4]=d;
		e[2+2*4]=d;
		e[3+3*4]=d;
	}
	public Matrix4f(float a,float b,float c,float d,float q,float f,float g,float h,float i,float j,float k,float l,float m,float n,float o,float p){
		for(int ii = 0; ii<e.length;ii++){
			e[ii]=0.0f;
		}
		e[ 0]=a;
		e[ 1]=b;
		e[ 2]=c;
		e[ 3]=d;
		e[ 4]=q;
		e[ 5]=f;
		e[ 6]=g;
		e[ 7]=h;
		e[ 8]=i;
		e[ 9]=j;
		e[10]=k;
		e[11]=l;
		e[12]=m;
		e[13]=n;
		e[14]=o;
		e[15]=p;
	}
	public static Matrix4f identity(){
		return new Matrix4f(1.0f);
	}
	public Matrix4f multiply2(Matrix4f other){
		float[] temp = new float[16];
		for(int x = 0; x<4;x++){
			for(int y = 0; y<4;y++){
				float sum = 0.0f;
				for(int ee = 0; ee<4;ee++){
					sum+=e[ee+y*4]*other.e[x+ee*4];
				}
				temp[x+y*4]=sum;
			}
		}
		for(int i=0;i<temp.length;i++){
			e[i]=temp[i];
		}
		return this;
	}
	public Matrix4f multiply(Matrix4f other){
		Matrix4f result= new Matrix4f();
        result.e[0] = this.e[0] * other.e[0] + this.e[1] * other.e[4] + this.e[2] * other.e[8] + this.e[3] * other.e[12];
        result.e[1] = this.e[0] * other.e[1] + this.e[1] * other.e[5] + this.e[2] * other.e[9] + this.e[3] * other.e[13];
        result.e[2] = this.e[0] * other.e[2] + this.e[1] * other.e[6] + this.e[2] * other.e[10] + this.e[3] * other.e[14];
        result.e[3] = this.e[0] * other.e[3] + this.e[1] * other.e[7] + this.e[2] * other.e[11] + this.e[3] * other.e[15];
        result.e[4] = this.e[4] * other.e[0] + this.e[5] * other.e[4] + this.e[6] * other.e[8] + this.e[7] * other.e[12];
        result.e[5] = this.e[4] * other.e[1] + this.e[5] * other.e[5] + this.e[6] * other.e[9] + this.e[7] * other.e[13];
        result.e[6] = this.e[4] * other.e[2] + this.e[5] * other.e[6] + this.e[6] * other.e[10] + this.e[7] * other.e[14];
        result.e[7] = this.e[4] * other.e[3] + this.e[5] * other.e[7] + this.e[6] * other.e[11] + this.e[7] * other.e[15];
        result.e[8] = this.e[8] * other.e[0] + this.e[9] * other.e[4] + this.e[10] * other.e[8] + this.e[11] * other.e[12];
        result.e[9] = this.e[8] * other.e[1] + this.e[9] * other.e[5] + this.e[10] * other.e[9] + this.e[11] * other.e[13];
        result.e[10] = this.e[8] * other.e[2] + this.e[9] * other.e[6] + this.e[10] * other.e[10] + this.e[11] * other.e[14];
        result.e[11] = this.e[8] * other.e[3] + this.e[9] * other.e[7] + this.e[10] * other.e[11] + this.e[11] * other.e[15];
        result.e[12] = this.e[12] * other.e[0] + this.e[13] * other.e[4] + this.e[14] * other.e[8] + this.e[15] * other.e[12];
        result.e[13] = this.e[12] * other.e[1] + this.e[13] * other.e[5] + this.e[14] * other.e[9] + this.e[15] * other.e[13];
        result.e[14] = this.e[12] * other.e[2] + this.e[13] * other.e[6] + this.e[14] * other.e[10] + this.e[15] * other.e[14];
        result.e[15] = this.e[12] * other.e[3] + this.e[13] * other.e[7] + this.e[14] * other.e[11] + this.e[15] * other.e[15];
		return result;
	}
	public static Matrix4f orthographic(float l, float r, float b,float t,float n, float f){
		Matrix4f result = Matrix4f.identity();
		result.e[0+0*4]=2.0f/(r-l);
		result.e[1+1*4]=2.0f/(t-b);
		result.e[2+2*4]=2.0f/(n-f);
		result.e[0+3*4]=(l+r)/(l-r);
		result.e[1+3*4]=(b+t)/(b-t);
		result.e[2+3*4]=(n+f)/(n-f);
		return result;
	}
	public static Matrix4f orthographic(float width, float height, float near, float far) {
		Matrix4f result = Matrix4f.identity();
		result.e[0+0*4]=1.0f/width;
		result.e[1+1*4]=1.0f/height;
		result.e[2+2*4]=-2.0f/(far-near);
		result.e[2+3*4]=(far+near)/(far-near);
		return result;
	}
	public static Matrix4f perspective2(float fov, float aspect,float n, float f){
		Matrix4f result = Matrix4f.identity();	
		result.e[0+0*4]=((float)(1.0/Math.tan(Math.toRadians(0.5*fov))))/aspect;
		result.e[1+1*4]=((float)(1.0/Math.tan(Math.toRadians(0.5*fov))));
		result.e[2+2*4]=(n+f)/(n-f);
		result.e[3+2*4]=1.0f;
		result.e[2+3*4]=(1.0f*n*f)/(n-f);
		return result;
	}
	public static Matrix4f perspective3(float fovx,float fovy,float n, float f){
		Matrix4f result = Matrix4f.identity();	
		result.e[0+0*4]=((float)(1.0/Math.tan(Math.toRadians(0.5*fovx))));
		result.e[1+1*4]=((float)(1.0/Math.tan(Math.toRadians(0.5*fovy))));
		result.e[2+2*4]=(f+n)/(f-n);
		result.e[3+2*4]=1.0f;
		result.e[3+3*4]=0f;
		result.e[2+3*4]=(2.0f*n*f)/(f-n);
		return result;
	}
	public static Matrix4f perspective(float fov, float aspect,float znear, float zfar){
		Matrix4f matrix = new Matrix4f();
        float tan = (float) (1.0 / (Math.tan(fov * 0.5)));
        matrix.e[0] = tan / aspect;
        matrix.e[1] = matrix.e[2] = matrix.e[3] = 0.0f;
        matrix.e[5] = tan;
        matrix.e[4] = matrix.e[6] = matrix.e[7] = 0.0f;
        matrix.e[8] = matrix.e[9] = 0.0f;
        matrix.e[10] = -zfar / (znear - zfar);
        matrix.e[11] = 1.0f;
        matrix.e[12] = matrix.e[13] = matrix.e[15] = 0.0f;
        matrix.e[14] = (znear * zfar) / (znear - zfar);
        return matrix;
	}
	public static Matrix4f translate(float x, float y,float z){
		Matrix4f result = Matrix4f.identity();
		result.e[12]=x;
		result.e[13]=y;
		result.e[14]=z;
		return result;
	}
	public static Matrix4f translate(Vector3f sub) {
		return translate(sub.x,sub.y,sub.z);
	}
	public static Matrix4f rotate(float a, Vector3f axis){
		Matrix4f result = Matrix4f.identity();
		float r = (float)Math.toRadians(a);
		float s = (float)Math.sin(r);
		float c = (float)Math.cos(r);
		float omc = 1.0f-c;
		result.e[0+3*4]=axis.x*omc+c;
		result.e[1+3*4]=axis.y*axis.x*omc+axis.z*s;
		result.e[2+3*4]=axis.x*axis.z*omc-axis.y*s;

		result.e[0+1*4]=axis.x*axis.y*omc-axis.z*s;
		result.e[1+1*4]=axis.y*omc+c;
		result.e[2+1*4]=axis.y*axis.z*omc+axis.x*s;

		result.e[0+2*4]=axis.x*axis.z*omc+axis.y*s;
		result.e[1+2*4]=axis.x*axis.z*omc+axis.y*s;
		result.e[2+2*4]=axis.z*omc+c;
		return result;
	}
	public static Matrix4f scale(Vector3f s){
		Matrix4f result = Matrix4f.identity();
		result.e[0+0*4]=s.x;
		result.e[1+1*4]=s.y;
		result.e[2+2*4]=s.z;
		return result;
	}
	public Matrix4f invert() {
		float[] temp = new float[16];

		temp[0]  = e[5] * e[10] * e[15] - e[5] * e[11] * e[14] - e[9] * e[6] * e[15] + e[9] * e[7] * e[14] + e[13] * e[6] * e[11] - e[13] * e[7] * e[10];
		temp[1]  = -e[1] * e[10] * e[15] + e[1] * e[11] * e[14] + e[9] * e[2] * e[15] - e[9] * e[3] * e[14] - e[13] * e[2] * e[11] + e[13] * e[3] * e[10];
		temp[2]  = e[1] * e[6] * e[15] - e[1] * e[7] * e[14] - e[5] * e[2] * e[15] + e[5] * e[3] * e[14] + e[13] * e[2] * e[7] - e[13] * e[3] * e[6];
		temp[3]  = -e[1] * e[6] * e[11] + e[1] * e[7] * e[10] + e[5] * e[2] * e[11] - e[5] * e[3] * e[10] - e[9] * e[2] * e[7] + e[9] * e[3] * e[6];
		temp[4]  = -e[4] * e[10] * e[15] + e[4] * e[11] * e[14] + e[8] * e[6] * e[15] - e[8] * e[7] * e[14] - e[12] * e[6] * e[11] + e[12] * e[7] * e[10];
		temp[5]  = e[0] * e[10] * e[15] - e[0] * e[11] * e[14] - e[8] * e[2] * e[15] + e[8] * e[3] * e[14] + e[12] * e[2] * e[11] - e[12] * e[3] * e[10];
		temp[6]  = -e[0] * e[6] * e[15] + e[0] * e[7] * e[14] + e[4] * e[2] * e[15] - e[4] * e[3] * e[14] - e[12] * e[2] * e[7] + e[12] * e[3] * e[6];
		temp[7]  = e[0] * e[6] * e[11] - e[0] * e[7] * e[10] - e[4] * e[2] * e[11] + e[4] * e[3] * e[10] + e[8] * e[2] * e[7] - e[8] * e[3] * e[6];
		temp[8]  = e[4] * e[9] * e[15] - e[4] * e[11] * e[13] - e[8] * e[5] * e[15] + e[8] * e[7] * e[13] + e[12] * e[5] * e[11] - e[12] * e[7] * e[9];
		temp[9]  = -e[0] * e[9] * e[15] + e[0] * e[11] * e[13] + e[8] * e[1] * e[15] - e[8] * e[3] * e[13] - e[12] * e[1] * e[11] + e[12] * e[3] * e[9];
		temp[10] = e[0] * e[5] * e[15] - e[0] * e[7] * e[13] - e[4] * e[1] * e[15] + e[4] * e[3] * e[13] + e[12] * e[1] * e[7] - e[12] * e[3] * e[5];
		temp[11] = -e[0] * e[5] * e[11] + e[0] * e[7] * e[9] + e[4] * e[1] * e[11] - e[4] * e[3] * e[9] - e[8] * e[1] * e[7] + e[8] * e[3] * e[5];
		temp[12] = -e[4] * e[9] * e[14] + e[4] * e[10] * e[13] + e[8] * e[5] * e[14] - e[8] * e[6] * e[13] - e[12] * e[5] * e[10] + e[12] * e[6] * e[9];
		temp[13] = e[0] * e[9] * e[14] - e[0] * e[10] * e[13] - e[8] * e[1] * e[14] + e[8] * e[2] * e[13] + e[12] * e[1] * e[10] - e[12] * e[2] * e[9];
		temp[14] = -e[0] * e[5] * e[14] + e[0] * e[6] * e[13] + e[4] * e[1] * e[14] - e[4] * e[2] * e[13] - e[12] * e[1] * e[6] + e[12] * e[2] * e[5];
		temp[15] = e[0] * e[5] * e[10] - e[0] * e[6] * e[9] - e[4] * e[1] * e[10] + e[4] * e[2] * e[9] + e[8] * e[1] * e[6] - e[8] * e[2] * e[5];

		double determinant = e[0] * temp[0] + e[1] * temp[4] + e[2] * temp[8] + e[3] * temp[12];
		determinant = 1.0 / determinant;

		for (int i = 0; i < 16; i++)
			e[i] = (float) (temp[i] * determinant);

		return this;
	}
	public float get(int i){
		return e[i];
	}
	public float get(int row, int column){
		return e[row+column*4];
	}
	public void set(int i,float v){
		e[i]=v;
	}
	public void get(int row, int column,float v){
		e[row+column*4]=v;
	}
	/*public FloatBuffer getBuffer(){
		return BufferUtil.tFB(elements);
	}*/
	@Override
	public Matrix4f clone(){
		Matrix4f res = Matrix4f.identity();
		for(int i=0;i<e.length;i++){
			res.e[i]=e[i];
		}
		return res;
	}
	public static Matrix4f lookAtLH(Vector3f p, Vector3f t, Vector3f up) {
		Vector3f z=t.sub(p);
		z.normalize();
		Vector3f x=Vector3f.cross(up,z);
		x.normalize();
		Vector3f y=Vector3f.cross(z,x);
		y.normalize();
		float ex=-Vector3f.dot(x,p);
		float ey=-Vector3f.dot(y,p);
		float ez=-Vector3f.dot(z,p);
		return new Matrix4f(x.x,x.y,x.z,ex, y.x,y.y,y.z,ey, z.x,z.y,z.z,ez, 0,0,0,1);
		//return new Matrix4f(x.x,y.x,z.x,0, x.y,y.y,z.y,0, x.z,y.z,z.z,0, ex,ey,ez,1);
	}
	public static Matrix4f rotationX(float angle){
		Matrix4f res = new Matrix4f();
		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);
		/*res.elements[0]=1;
		res.elements[15]=1;
		res.elements[5]=c;
		res.elements[10]=c;
		res.elements[9]=-s;
		res.elements[6]=s;
		/*/
		res.e[0]=1;
		
		res.e[15]=1;
		res.e[5]=c;
		res.e[10]=c;
		res.e[9]=-s;
		res.e[6]=s;//*/
		return res;
	}
	public static Matrix4f rotationZ(float angle){
		Matrix4f res = new Matrix4f();
		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);
		/*res.elements[0]=c;
		res.elements[1]=s;
		res.elements[4]=-s;
		res.elements[5]=c;
		res.elements[10]=1;
		res.elements[15]=1;
		/*/
		res.e[0]=c;
		res.e[4]=s;
		res.e[1]=-s;
		res.e[5]=c;
		res.e[10]=1;
		res.e[15]=1;//*/
		return res;
	}
	public static Matrix4f rotationY(float angle){
		Matrix4f res = new Matrix4f();
		float s = (float) Math.sin(angle);
		float c = (float) Math.cos(angle);
		/*res.elements[0]=c;
		res.elements[2]=-s;
		res.elements[5]=1;
		res.elements[8]=s;
		res.elements[10]=c;
		res.elements[15]=1;
		/*/
		res.e[0]=c;
		 
		res.e[8]=-s;
		res.e[5]=1;
		res.e[2]=s;
		res.e[10]=c;
		res.e[15]=1;//*/
		return res;
	}
	public static Matrix4f rotationYPR(float yaw, float pitch, float roll){
		return Matrix4f.rotationZ(roll).multiply(Matrix4f.rotationX(pitch)).multiply(Matrix4f.rotationY(yaw));
	}
	public static Matrix4f rotationYPR(Vector3f rotation){
		return Matrix4f.rotationYPR(rotation.y,rotation.x,rotation.z);
	}
}
