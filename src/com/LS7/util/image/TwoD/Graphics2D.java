package com.LS7.util.image.TwoD;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.LS7.core.Renderer;
import com.LS7.util.Math.Vector2f;
import com.LS7.util.Math.Vector3f;
import com.LS7.util.image.Pixel;
/**
 * This was going to handle graphics just like Graphics3D, but i decided not to do 
 * it, instead its gonna handle some stuff like normal maps and lighting.
 * @author LucasRo7
 */
public class Graphics2D{
	public List<light> lights = new ArrayList<>();
	public List<object2D> objects = new ArrayList<>();
	private Comparator<object2D> comparator=new Comparator<object2D>(){
		public int compare(object2D obj1, object2D obj2) {
			return obj1.renderPriority()-obj2.renderPriority();
		}
	};
	
	public class light{
		/**Position of the light*/
		protected Vector3f position;
		public light(Vector3f pos){
			position=pos; // too lazy to write "this.position = position;", oh wait,  just did :P
		}
		/**Returns the position of the light 
		 * @return the position of this light*/
		public Vector3f getPosition(){
			return position;
		}
	}
	public interface object2D{
		/**Draw the sprite on the screen
		 * @param graphics the graphics used to draw on the screen.*/
		public void render(Renderer r,List<light> lights);
		/**Used to order the drawing of sprites*/
		public int renderPriority();
	}
	/**
	 * Renders all objects into the screen
	 * @param r Renderer class used to draw on the screen
	 */
	public void render(Renderer r){
		objects.sort(comparator);
		for(object2D o:objects)
			o.render(r,lights);
	}
	/**
	 * Transform a 3D coordinate into 2D, its very simple, 
	 * @param position
	 * @return
	 */
	public static Vector2f project(Vector3f position){
		// simple enough huh? why is there even a graphics3D class with such 
		// complicated matrices and lots of lag? (i mean, seriously! '-' )
		return new Vector2f(position.x,position.y+position.z);
	}
	/**
	 * Return a vector representing the normal of a pixel, the pixel contains the color of the normal
	 * @param invertGreen Invert the y component(green)
	 * @param invertRed Invert the x component(red)
	 * @param normalPixel the pixel of the normal map
	 * @return a vector representing the normal
 	 */
	public static Vector3f getNormal(boolean invertGreen, boolean invertRed, Pixel normalPixel){
		return new Vector3f(((normalPixel.red/128.0f)-1)*(invertRed?1:-1),((normalPixel.green/128.0f)-1)*(invertGreen?1:-1),-((normalPixel.blue/128.0f)-1)).normalize();
	}
}
