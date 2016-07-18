package com.LS7.util.image.ThreeD;

import java.awt.Graphics;

import com.LS7.util.Math.Matrix4f;
import com.LS7.util.Math.Vector3f;

public abstract class Object3D {
	public Vector3f position;
	public Vector3f rotation;
	public Matrix4f worldMatrix = Matrix4f.rotationYPR(rotation).multiply(Matrix4f.translate(position));
	/**
	 * Used to order the rendering.
	 * It can be simply subtracting 1 on the z coordinate of each vertex's vector3f(after
	 * it has been transformed using the matrix) and averaging the magnitude of the vectors.
	 * @param transformMatrix The matrix used to transform the vectors
	 * @return the average distance of this object.
	 */
	public abstract float averageDistance(Matrix4f transformMatrix);
	/**
	 * Render the object.
	 * @param graphics the graphics class used to render
	 * @param transformMatrix the matrix.
	 * @return the average distance of this object.(average of the 
	 * magnitude of each vertex of this object)
	 */
	public abstract void render(Graphics graphics, Matrix4f transformMatrix);
}
