package chat_project;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private String name,address;
	private int port;
	private JTextField txtMessage;
	
	private JTextArea history;
	
	private DatagramSocket socket;
	private InetAddress ip;

	public Client(String name, String address ,int port) {
		setTitle("Chat Client");
		
		this.name=name;
		this.address=address;
		this.port=port;
		
		createWindow(); 
		console("Attempting a connection to "+address+":"+port+" , user : "+name);
		boolean connect = openConnection(address,port);
		if(!connect) {
			console("Conncection Failed");
		}
	}
	
	
	private boolean openConnection(String address , int port ) {
		
		try {
			socket  = new DatagramSocket(port);
			ip =  InetAddress.getByName(address);
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private String receive() {
		
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data,data.length);
		try {
			socket.receive(packet);
		}catch(IOException e) {
			e.printStackTrace();
		}
		String messsage = new String(packet.getData());
		
		return message;
	}
	
	
	private void createWindow() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setVisible(true);
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{28,815,30,7}; //SUM=880
		gbl_contentPane.rowHeights = new int[]{35,475,40};	//SUM=550
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		JScrollPane  scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx =0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth=3;
		scrollConstraints.gridheight=2;
		scrollConstraints.insets= new Insets(0,5,0,0);
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER) {
					send(txtMessage.getText());
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText());
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets=new Insets(0,0,0,5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		
		txtMessage.requestFocusInWindow();
		
	}
	
	public void send(String message) {
		if(message.trim().equals("")) {
			txtMessage.setText("");
			return;
		}
		message = name + ": "+ message;
		console(message);
		txtMessage.setText("");
		txtMessage.requestFocusInWindow();
	}
	
	public void console(String message) {
		history.append(message + "\n");
		history.setCaretPosition(history.getDocument().getLength());
	}
}