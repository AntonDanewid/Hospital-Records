package server;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.net.*;
import javax.net.ssl.*;
import javax.security.cert.Certificate;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class server implements Runnable {
	private ServerSocket serverSocket = null;
	private static int numConnectedClients = 0;
	private Logger log;

	public server(ServerSocket ss) throws IOException {
		serverSocket = ss;
		newListener();
		log = new Logger();
	}

	public void run() {
		String userName = "Unknown";
		try {
			SSLSocket socket = (SSLSocket) serverSocket.accept();
			newListener();
			SSLSession session = socket.getSession();
			X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];

			String subject = cert.getSubjectDN().getName();
			numConnectedClients++;
			System.out.println("client connected");
			System.out.println("client name (cert subject DN field): " + subject);
			System.out.println("Issuer:" + cert.getIssuerDN());
			System.out.println(numConnectedClients + " concurrent connection(s)\n");

			PrintWriter out = null;
			BufferedReader in = null;
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String clientMsg = null;
			String temp = subject.substring(0, subject.indexOf(","));

			String[] login = temp.split(":");
			User user = null; // catch exception
			try {
				user = getUserType(login);
			} catch (FileNotFoundException e) {
				out.println("USER NOT FOUND");
				log.login(login.toString(), "USER NOT FOUND");
				in.close();
				out.close();
				socket.close();
				numConnectedClients--;

				return;
			} catch (IllegalUserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				out.println("WRONG USER TYPE: ACCESS DENIED. VISIT ADMINISTRATION FOR FURTHER DETAILS");
				log.login(login.toString(), "WRONG USER TYPE");
				in.close();
				out.close();
				socket.close();
				numConnectedClients--;

				return;
			} // catch exception
			log.login(user.getName(), "User logged in");
			out.print("Logged in as:" + login[0]);
			out.println(
					". Posible commands are: Read Write Delete.  Write commands as following: Read patient_name/record");
			out.flush();
			while ((clientMsg = in.readLine()) != null) {
				String a = processCommand(user, clientMsg);
				if (a == "exit") break;
				out.println(a);
				out.flush();
				System.out.println("done\n");
			}
			in.close();
			out.close();
			socket.close();
			numConnectedClients--;
			System.out.println("client disconnected");
			log.login(user.getName(), "Logged out");

			System.out.println(numConnectedClients + " concurrent connection(s)\n");

		} catch (IOException e) {
			System.out.println("Client died: " + e.getMessage());
			log.login("NON AUTHORIZED USER", "CONNECTION DROPPED");
			e.printStackTrace();
			return;
		}
	}

	private User getUserType(String[] loginDetails) throws IllegalUserException, IOException {
		loginDetails[0] = loginDetails[0].replace("CN=", "");
		System.out.println(loginDetails[0]);
		System.out.println(loginDetails[1]);

		switch (loginDetails[1]) {
		case "Doctor":
			return new Doctor(loginDetails[0]);

		case "Nurse":
			return new Nurse(loginDetails[0]);

		case "Patient":
			return new Patient(loginDetails[0]);

		case "GovernmentAgency":
			return new GovernmentAgency();

		default:
			throw new IllegalUserException("Illegal user type");
		}
	}

	private class IllegalUserException extends Exception {
		public IllegalUserException(String msg) {
			super(msg);
		}
	}

	private void newListener() {
		(new Thread(this)).start();
	} // calls run()

	public static void main(String args[]) {
		System.out.println("\nServer Started\n");
		int port = -1;
		if (args.length >= 1) {
			port = Integer.parseInt(args[0]);
		}
		String type = "TLS";
		try {
			ServerSocketFactory ssf = getServerSocketFactory(type);
			ServerSocket ss = ssf.createServerSocket(port);
			((SSLServerSocket) ss).setNeedClientAuth(true); // enables client
															// authentication
			new server(ss);
		} catch (IOException e) {
			System.out.println("Unable to start Server: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static ServerSocketFactory getServerSocketFactory(String type) {
		if (type.equals("TLS")) {
			SSLServerSocketFactory ssf = null;
			try { // set up key manager to perform server authentication
				SSLContext ctx = SSLContext.getInstance("TLS");

				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
				char[] password = "password".toCharArray();

				ks.load(new FileInputStream("serverkeystore"), password); // keystore
																			// password
																			// (storepass)
				ts.load(new FileInputStream("servertruststore"), password); // truststore
																			// password
																			// (storepass)

				kmf.init(ks, password); // certificate password (keypass)
				tmf.init(ts); // possible to use keystore as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				ssf = ctx.getServerSocketFactory();
				return ssf;
			} catch (Exception e) {
				// e.printStackTrace();
			}
		} else {
			return ServerSocketFactory.getDefault();
		}
		return null;
	}

	private void parse(String s) {
		String[] login = s.split(",");
		if (login[0].equals("Doctor")) {

		}
	}

	public String processCommand(User user, String command) {
		String[] words = command.split(" ");
		switch (words[0]) {
		case ("Read"):
			String read;
			try {
				read = user.read(words[1]);
				log.log(user.getName(), "Read", words[1], "SUCCESS");

				return read.replace("\n", "#NEWLINE#");

			} catch (IOException e2) {

				log.log(user.getName(), "Read", words[1], "FAILED. IO INTERRUPTION");
				e2.printStackTrace();
				return "IO ERROR";
			} catch (IllegalAccessException ace) {
				log.log(user.getName(), "Read", words[1], "FAILED: ACCESS DENIED");
				return "You do not have permission";

			}

		case ("Write"):
			String content = arrayToString(words, 2);
			content.replace("#NEWLINE#", "\n");

			try {
				String write = user.write(words[1], content);
				log.log(user.getName(), "Write", words[1], "SUCESS");
				return write;
			} catch (FileNotFoundException e1) {
				log.log(user.getName(), "Write", words[1], "FAILED: FILE NOT FOUND");
				e1.printStackTrace();
				return "File not found";

			} catch (IOException e1) {
				log.log(user.getName(), "Write", words[1], "FAILED: IO INTERUPTTION");
				e1.printStackTrace();
				return "IO ERROR";

			} catch (IllegalAccessException ace) {
				log.log(user.getName(), "Write", words[1], "FAILED: ACCESS DENIED");
				return "You do not have permission";

			}

		case ("Delete"):

			try {
				String delete = user.delete(words[1]);
				log.log(user.getName(), "Delete", words[1], "SUCCESS");
				System.out.println(numConnectedClients + " <- clients");
				return delete;
			} catch (IOException e) {
				log.log(user.getName(), "Delete", words[1], "FAILED. IO INTERRUPTION");
//				e.printStackTrace();
				return "IOERROR!!!";
			} catch (IllegalAccessException ace) {
				log.log(user.getName(), "Delete", words[1], "FAILED: ACCESS DENIED");
				return "You do not have permission";

			}

		case ("Create"):
			try {

				String create = user.create(words[1], words[2], words[3], words[4],
						arrayToString(words, 5));
				log.log(user.getName(), "Create", words[1], "SUCCESS");

				return create;
			} catch (IllegalAccessException e) {
				log.log(user.getName(), "Create", words[1], "FAILED: ACCESS DENIED");
//				e.printStackTrace();
				return "You do not have permission";

			} catch (ArrayIndexOutOfBoundsException e) {
				log.log(user.getName(), "Create", words[1], "FAILED: WRONG INPUT");
//				e.printStackTrace();
				return "You did not provide correct format";
			}
		
		case "quit":
			return "quit";
		 default:
			return words[0] + " could not be identified as a command";

		}

	}
	
	private String arrayToString(String[] words, int startidx) {
		StringBuilder sb = new StringBuilder();
		for (int i=startidx; i < words.length; i++) {
			sb.append(words[i] + " ");
		}
		String ans = sb.toString();
		if (ans.startsWith("\"")) {
			ans = ans.substring(1, ans.length());
		}
		if (ans.endsWith("\"")) {
			ans = ans.substring(0, ans.length()-2);
		}
		return ans;
	}
}
