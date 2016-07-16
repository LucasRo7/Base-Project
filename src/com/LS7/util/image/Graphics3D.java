package com.LS7.util.image;

import com.LS7.util.Math.*;

public class Graphics3D{
	
	public Matrix4f matrix=Matrix4f.identity();
	
	public Vector3f transform(Vector3f pos){
		return Vector3f.transformCoordinates(pos, matrix);
	}
}
