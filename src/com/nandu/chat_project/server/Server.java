package com.nandu.chat_project.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server implements Runnable{
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	private List<Integer> clientResponse = new ArrayList<Integer>();
	
	private int port;
	private DatagramSocket socket;
	private boolean running = false;
	private Thread run,manage,send,receive;
	
	private boolean raw = false;
	
	private final int MAX_ATTEMPTS = 5;
	
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
		
		Scanner scanner = new Scanner(System.in);
		while(running) {
			String text = scanner.nextLine();
			if(!text.startsWith("/")) {
				
			}
			text=text.substring(1);
			if(text.equals("raw")) {
				raw=!raw;
			}else if(text.equals("clients")) {
				System.out.println("Clients");
				System.out.println("--------------");
				for(int i =0;i<clients.size();i++) {
					ServerClient c  =ion clients.get(i);
					System.out.println(c.name + " (" + c.getID() + ") : " + c.address.toString() + ":" + c.port);
				}
				System.out.println("--------------");
			}
			
				
		}
	}
	
	private void manageClients() {
		manage = new Thread("Manage") {
			public void run() {
				while(running) {
					sendToAll("/p/server");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for(int i =0;i<clients.size();i++) {
						ServerClient c = clients.get(i);
						if(!clientResponse.contains(c.getID())) {
							if(c.attempt >= MAX_ATTEMPTS) {
								disconnect(c.getID(),false);
								
							}else {
								c.attempt++;
							}
						}else {
							clientResponse.remove(new Integer(c.getID()));
							c.attempt=0;
						}
					}
					
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
				}
			}
		};
		receive.start();
	}
	
	private void sendToAll(String message) {
		
		if(message.startsWith("/m/")) {
			String text = message.substring(3);
			text=text.split("/e/")[0];
			System.out.println(text);
		}
	
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
		
		if(raw) System.out.println(string);
		if(string.startsWith("/c/")) {
			int id  = UniqueIdentifier.getIdentifier();
			clients.add(new ServerClient(string.substring(3,string.length()),packet.getAddress(),packet.getPort(),id));
			String ID = "/c/" + id;
			send(ID,packet.getAddress(),packet.getPort());
			
		}else if(string.startsWith("/m/")){
			sendToAll(string);
		}else if (string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id),true);
			
		}else if(string.startsWith("/p/")) {
			clientResponse.add(Integer.parseInt(string.split("/p/|/e/")[0]));
			
		}
	}
	
	private void disconnect(int id , boolean status) {
		ServerClient c = null;
		for(int i=0;i<clients.size();i++) {
			if(clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				break;
			}
		}
		String message = "";
		if(status) {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " disconnected";
		}else {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " timed out";
		}
		
	}
}







