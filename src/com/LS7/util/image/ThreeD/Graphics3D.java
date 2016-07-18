package com.LS7.util.image.ThreeD;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.LS7.core.Renderer;
import com.LS7.util.Math.*;
/**
 * Class made to order and draw many objects on the screen
 * (Holy cow! so many matrices!)
 * @author LucasRo7
 */
public class Graphics3D{
	
	protected List<Object3D> objects = new ArrayList<>();
	protected Comparator<Object3D> comparator = new Comparator<Object3D>(){
		public int compare(Object3D obj1, Object3D obj2) {
			Matrix4f transform1Matrix=obj1.worldMatrix.multiply(Matrix4f.translate(cameraPos.mult(-1))).multiply(viewMatrix).multiply(projectionMatrix);
			Matrix4f transform2Matrix=obj2.worldMatrix.multiply(Matrix4f.translate(cameraPos.mult(-1))).multiply(viewMatrix).multiply(projectionMatrix);
			float dist = obj1.averageDistance(transform1Matrix)-obj2.averageDistance(transform2Matrix);
			if(dist>0)
				return 1;
			else if(dist<0)
				return -1;
			else
				return 0;
		}
	};
	
	public Vector3f cameraPos=new Vector3f(0,0,0);
	public Vector3f cameraTarget=new Vector3f(0,0,1);
	public Matrix4f projectionMatrix = Matrix4f.identity();
	protected Matrix4f viewMatrix = Matrix4f.lookAtLH(cameraPos,cameraTarget,new Vector3f(0,1,0));
	protected Matrix4f screenMatrix = Matrix4f.translate(new Vector3f(1,1,0)).multiply(Matrix4f.translate(new Vector3f(Renderer.width/2,Renderer.height/2,1)));
	/**
	 * Draws an object on the screen(it also makes sure the x and y component of the transformed vectors
	 * are inside the screen instead of -1,-1 to 1,1)
	 * @param graphics
	 * @param obj
	 */
	private void render(Graphics graphics, Object3D obj){
		Matrix4f transformMatrix=obj.worldMatrix.multiply(viewMatrix).multiply(projectionMatrix).multiply(screenMatrix);
		obj.render(graphics, transformMatrix);
	}
	/**
	 * This not a very efficient method, but it gets the work done.
	 * (also it relies entirely on the CPU, so that slows it even more)
	 * @param graphics
	 */
	public void render(Graphics graphics){
		objects.sort(comparator);
		for(Object3D obj:objects)
			render(graphics,obj);
	}
}
