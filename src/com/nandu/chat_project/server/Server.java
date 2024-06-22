package com.nandu.chat_project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
			System.out.println("Server Started at "+port);
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
	
	private void sendToAll(String message) {
		for(int i=0;i<clients.size();i++) {
			ServerClient client = clients.get(i);
			send(message.getBytes(),client.address,client.port);
		}
	}
	
	private void send(final byte[] data , final InetAddress address , final int port) {
		send = new Thread("send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data,data.length,address,port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message,InetAddress address , int port) {
		message+="/e/"; //Shows the end of the message
		send(message.getBytes(),address,port);
	}

	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		if(string.startsWith("/c/")) {
			int id  = UniqueIdentifier.getIdentifier();
			clients.add(new ServerClient(string.substring(3,string.length()),packet.getAddress(),packet.getPort(),id));
			String ID = "/c/" + id;
			send(ID,packet.getAddress(),packet.getPort());
			
		}else if(string.startsWith("/m/")){
			sendToAll(string);
		}else {
			
		}
	}
}







