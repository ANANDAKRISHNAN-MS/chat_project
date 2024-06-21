package com.nandu.chat_project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	
	private int port;
	private DatagramSocket socket;
	private boolean running = false;
	private Thread run,manage,send,receive;
	
	public Server(int port) {
		this.port=port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this,"Server");
		run.start();
	}


	public void run() {
		running = true;
		manageClients();
		recieve();	
	}
	
	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while(running) {
					
				}
			}
		};
		manage.start();
	}
	
	private void recieve() {
		receive = new Thread("Running") {
			public void run() {
				while(running) {
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data,data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
					clients.add(null);
				}
			}
		};
		receive.start();
	}

	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		if(string.startsWith("/c/")) {
			clients.add(new ServerClient(string.substring(3,string.length()),packet.getAddress(),packet.getPort(),1));
			
		}else {
			
		}
	}
}







