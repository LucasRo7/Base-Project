package com.LS7.util.Data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Multiplayer2 {

	protected DatagramSocket socket;
	protected InetAddress ip;
	protected Thread send;
	
	public boolean openConnection(String address, int port) {
		try {
			if(socket.isConnected())
			ip = InetAddress.getByName(address);
			socket = new DatagramSocket();
			socket.connect(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public byte[] receive(int size) {
		byte[] data = new byte[size];
		DatagramPacket packet = new DatagramPacket(data, size);
		try {
			socket.receive(packet);
		} catch (IOException e) {
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
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	public int getPort(){
		return socket.getLocalPort();
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
