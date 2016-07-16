package com.LS7.util.Data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

// TODO rename this class to a more apropriate name, since "Multiplayer"
//      is for games and not "anything that uses internet" (NetworkHandler, maybe?)
public class Multiplayer {
	
	public interface receiver{
		public void receive(byte[] message);
	}
	public class relay implements Runnable{
		public List<receiver> receivers = new ArrayList<>();
		public Thread thread;
		public boolean running=false;
		public void start(){
			running=true;
			thread = new Thread(this,"Relay(Multiplayer class)");
			thread.start();
		}
		public void stop(){
			running=false;
		}
		public void run() {
			while(running){
				byte[] message = receive(1024*1024,0);
				for(receiver r : receivers){
					r.receive(message);
				}
			}
		}
		public void addReceiver(receiver r){
			receivers.add(r);
		}
	}
	
	protected DatagramSocket socket;
	protected InetAddress ip;
	protected Thread send;
	protected relay relay;
	
	public boolean openPort(int localPort) {
		try {
			System.out.println("Opening port: "+localPort);
			if(socket.isConnected())
			socket = new DatagramSocket(localPort);
		} catch (SocketException e) {
			System.out.println("Failed to open port!(Port in use?)");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean connect(String targetAddress, int targetPort) {
		try {
			System.out.println("Connecting to "+targetAddress+":"+targetPort);
			if(socket==null){
				System.out.println("Failed to connect!(No port was opened)");
				return false;
			}
			ip = InetAddress.getByName(targetAddress);
			socket.connect(ip, targetPort);
		} catch (UnknownHostException e) {
			System.out.println("Failed to connect!(Correct address?)");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public byte[] receive(int size,int timeout) {
		byte[] data = new byte[size];
		DatagramPacket packet = new DatagramPacket(data, size);
		try {
			if(timeout>=0)
				socket.setSoTimeout(timeout);
			socket.receive(packet);
		} catch(SocketException e){
			System.out.println("Failed to receive!(Timeout reached)");
		} catch (IOException e) {
			System.out.println("Failed to receive!(I/O exception)");
			e.printStackTrace();
		}
		return packet.getData();
	}
	public void send(final byte[] data) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, socket.getRemoteSocketAddress());
				try {
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("Failed to send!(I/O exception)");
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	public int getLocalPort(){
		return socket.getLocalPort();
	}
	public int getTargetPort(){
		return socket.getPort();
	}
	public String getLocalAddress(){
		return socket.getLocalAddress().getHostName();
	}
	public String getTargetAddress(){
		return socket.getInetAddress().getHostAddress();
	}
	public void closeConnection() {
		new Thread() {
			public void run() {
				synchronized (socket) {
					socket.close();
				}
			}
		}.start();
	}
	
}
