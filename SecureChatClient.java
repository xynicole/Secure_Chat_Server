import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.math.BigInteger;
import javax.swing.event.*;
import javax.swing.text.DefaultCaret;

public class SecureChatClient extends JFrame implements Runnable, ActionListener {

	public static final int PORT = 8765;
	ObjectInputStream myReader;
	ObjectOutputStream myWriter;
	JTextArea outputArea;
	JLabel prompt;
	JTextField inputField;
	String myName, serverName;
	Socket connection;
	SymCipher cipher;
	BigInteger E; // public key
	BigInteger N;// public mod value
	String cipherType;
	BigInteger key;
	BigInteger encryptKey;

	public SecureChatClient() {
		try {

			myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
			serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
			InetAddress addr = InetAddress.getByName(serverName);
			connection = new Socket(addr, PORT); // Connect to server with new

			// Socket
			// creates an ObjectOutputStream on the socket (for writing)
			// immediately calls the flush() method (this technicality prevents deadlock).
			myWriter = new ObjectOutputStream(connection.getOutputStream()); // get writer
			myWriter.flush();

			// creates on ObjectInputStream on the socket (be sure you create this AFTER
			// creating the ObjectOutputStream).
			myReader = new ObjectInputStream(connection.getInputStream()); // get reader

			E = (BigInteger) myReader.readObject();
			N = (BigInteger) myReader.readObject();

			cipherType = (String) myReader.readObject();
			// check cipher type
			// creates either a Substitute object or an Add128 object, storing the resulting
			// object in a SymCipher variable.
			if (cipherType.equals("Add")) {
				// System.out.println("Cipher type is Add128");
				cipher = new Add128();

			} else if (cipherType.equals("Sub")) {
				// System.out.println("Cipher type is Substitute");
				cipher = new Substitute();
			} else {

				System.out.println("Type not found");
				System.exit(0);

			}

			// gets the key from its cipher object using the getKey() method,
			// and then converts the result into a BigInteger object.
			key = new BigInteger(1, cipher.getKey()); // make sure it is positive
			// encrypts the BigInteger version of the key using E and N
			encryptKey = key.modPow(E, N);

			System.out.println("\nE: " + E);
			System.out.println("\nN: " + N);
			System.out.println("\n Type of symmetric encryption : " + cipherType);
			System.out.println("\n The symmetric Key: " + key);
			System.out.println("\n Encrypted Key: " + encryptKey);

			// sends the resulting BigInteger to the server
			myWriter.writeObject(encryptKey);
			myWriter.flush();

			// prompts the user for his/her name, then encrypts it using the cipher and
			// sends it to the server.
			// The encryption will be done using the encode() method of the SymCipher
			// interface,
			// and the resulting arrayof bytes will be sent to the server as a single object
			// using the ObjectOutputStream.

			myWriter.writeObject(cipher.encode(myName));
			myWriter.flush();

			// myWriter.println(myName); // Send name to Server. Server will need
			// this to announce sign-on and sign-off
			// of clients

			this.setTitle(myName); // Set title to identify chatter

			//GUI 
			Box b = Box.createHorizontalBox(); // Set up graphical environment for
			outputArea = new JTextArea(8, 30); // user

			// set color
			outputArea.setFont(new Font("Serif", Font.BOLD, 16));
			outputArea.setBackground(Color.WHITE);
			outputArea.setLineWrap(true);
			outputArea.setWrapStyleWord(true);
			outputArea.setEditable(false);
			
			
			Container c = getContentPane();	
			c.setBackground(new Color(206,249,209)); //change bg color
			
			//auto scrolls
			JScrollPane scrollPane = new JScrollPane(outputArea);
			//setBounds(int x,int y,int width,int height) 
			//set position
			scrollPane.setBounds(0, 30, 500, 350);
			
			c.add(scrollPane);
			JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setViewportView(outputArea);
			DefaultCaret caret = (DefaultCaret)outputArea.getCaret();	 
			caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	        
			//b.add(new JScrollPane(outputArea));
			outputArea.append("Welcome to the Chat Group, " + myName + "\n");

			
			//input message 
			inputField = new JTextField(""); // This is where user will type input
			inputField.setFont(new Font("Serif", Font.PLAIN, 16));
			inputField.addActionListener(this);
			inputField.setBounds(0, 440, 500, 30);
			
			
			
			prompt = new JLabel("Type your messages below:");
			prompt.setFont(new Font("Serif", Font.PLAIN, 16));
			prompt.setBounds(0, 400, 500, 30);
			
			
			
			
			JMenuBar menuBar = new JMenuBar();
			menuBar.setBounds(0, 0, 500, 30);

			UIManager.put("MenuBar.background", Color.GREEN);
			UIManager.put("MenuItem.background", Color.GREEN);
			
			
			JMenu text = new JMenu("Text Color");
			JMenu font = new JMenu("Text Font");
			JMenu bg = new JMenu("Background Color");
			
			
			menuBar.add(text);
			menuBar.add(font);
			menuBar.add(bg);
			
			JMenuItem tBlack = new JMenuItem("Black");
			JMenuItem tWhite = new JMenuItem("White");
			JMenuItem tRed = new JMenuItem("Red");
			text.add(tBlack);
			text.add(tWhite);
			text.add(tRed);
			
			JMenuItem se = new JMenuItem("Serif");
			JMenuItem he = new JMenuItem("Helvetica");
			JMenuItem tr = new JMenuItem("TimesRoman");
			font.add(se);
			font.add(he);
			font.add(tr);
			
			JMenuItem bgBlack = new JMenuItem("Black");
			JMenuItem bgWhite = new JMenuItem("White");
			bg.add(bgBlack);
			bg.add(bgWhite);
			
			tBlack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setForeground(Color.BLACK); 
                }
            });
			
			tWhite.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setForeground(Color.WHITE); 
                }
            });
			
			tRed.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setForeground(Color.RED); 
                }
            });
			
			bgBlack.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setBackground(Color.BLACK); 
                }
            });
			
			bgWhite.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setBackground(Color.WHITE); 
                }
            });
			
			
			se.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setFont(new Font("Serif", Font.PLAIN, 16)); 
                }
            });
			
			he.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setFont(new Font("Helvetica", Font.PLAIN, 16)); 
                }
            });
			
			tr.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                	outputArea.setFont(new Font("TimesRoman", Font.PLAIN, 16));
                }
            });
			
			c.setLayout(null);
			c.add(b);
			c.add(prompt);
			c.add(inputField);
			c.add(menuBar);
			
			
			//c.add(menuBar, BorderLayout.NORTH);
			//c.add(b, BorderLayout.NORTH);
			//c.add(prompt, BorderLayout.CENTER);
			//c.add(inputField, BorderLayout.SOUTH);

			
			
			
			Thread outputThread = new Thread(this); // Thread is to receive strings
			outputThread.start(); // from Server

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					try {

						myWriter.writeObject(cipher.encode("CLIENT CLOSING"));
						myWriter.flush();
						
						connection.close();
						System.exit(0);
						
					} catch (IOException e1) {
						System.out.println("Problem closing!");
						e1.printStackTrace();
					}
					// myWriter.println("CLIENT CLOSING");
					// System.exit(0);
				}
			});

			setSize(500, 500);
			setVisible(true);

		} catch (Exception e) {
			System.out.println("Problem starting client!");
		}
	}

	public void run() {
		while (true) {
			try {
				// get encrypted msg from server
				byte[] encryptMsg = (byte[]) myReader.readObject();
				// to decrpt
				String currMsg = cipher.decode(encryptMsg);
				outputArea.append(currMsg + "\n");

				System.out.println("\n-------Message Decryption-----------");
				System.out.println("\nThe array of bytes received:   " + Arrays.toString(encryptMsg));
				System.out.println("\nThe decrypted array of bytes:   " + Arrays.toString(currMsg.getBytes()));
				System.out.println("\nThe corresponding String:   " + currMsg);

			} catch (Exception e) {
				System.out.println(e + ", closing client!!");
				break;
			}
		}
		System.exit(0);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String currMsg = e.getActionCommand(); // Get input value
			inputField.setText("");

			// myWriter.println(myName + ":" + currMsg); // Add name and send itto Server
			currMsg = myName + ": " + currMsg; // Format the message

			System.out.println("\n-------Message Encryption-----------");
			System.out.println("\nThe original String message:   " + currMsg);
			System.out.println("\nThe corresponding array of bytes:   " + Arrays.toString(currMsg.getBytes()));
			System.out.println("\nThe encrypted array of bytes:   " + Arrays.toString(cipher.encode(currMsg)));

			myWriter.writeObject(cipher.encode(currMsg));
			myWriter.flush();
		} catch (IOException e1) {
			System.out.println("Fail to sending the message: ");
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SecureChatClient JR = new SecureChatClient();
		JR.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
}
