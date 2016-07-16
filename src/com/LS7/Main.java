package com.LS7;

import javax.swing.JFrame;

import com.LS7.core.*;
import com.LS7.util.Input;

public class Main {

	protected Renderer renderer;
	protected Updater updater;
	protected Input input;

	protected JFrame frame;

	public static void main(String[] args) {
		new Main();
	}
	
	protected void init(){
		renderer = new Renderer();
		updater = new Updater();
		input = new Input(Renderer.res);
	}
	
	protected void addListeners(Renderer renderer, Input input){
		renderer.addMouseListener(input);
		renderer.addMouseWheelListener(input);
		renderer.addMouseMotionListener(input);
		renderer.addKeyListener(input);
		renderer.addFocusListener(input);
	}
	
	protected void mkJFrame(Renderer renderer){
		frame = new JFrame();
		frame.setTitle(Data.windowName);
		frame.requestFocus();
		frame.setResizable(false);
		frame.add(renderer);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public Main() {
		Data.running=true;
		
		init();
		addListeners(renderer, input);
		mkJFrame(renderer);
		
		renderer.start();
		updater.start();
	}
}
