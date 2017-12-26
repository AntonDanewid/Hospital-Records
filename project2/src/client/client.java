package client;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.cert.*;
import java.util.Scanner;

/*
 * This example shows how to set up a key manager to perform client
 * authentication.
 *
 * This program assumes that the client is not inside a firewall.
 * The application can be modified to connect to a server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class client {

	private String host;
	private int port;
	private static boolean quitflag;

	public static void main(String[] args) throws Exception {
		String host = null;
		int port = -1;
		for (int i = 0; i < args.length; i++) {
			System.out.println("args[" + i + "] = " + args[i]);
		}
		if (args.length < 2) {
			System.out.println("USAGE: java client host port");
			System.exit(-1);
		}
		try { /* get input parameters */
			host = args[0];
			port = Integer.parseInt(args[1]);
		} catch (IllegalArgumentException e) {
			System.out.println("USAGE: java client host port");
			System.exit(-1);
		}

		while (!quitflag) {
			process(host, port);
		}
	}

	public static void process(String host, int port) {

		try { /* set up a key manager for client authentication */
			SSLSocketFactory factory = null;
			String username = null;
			try {
				System.out.println("Please enter username");
				Scanner scan = new Scanner(System.in);
				username = scan.nextLine();

				System.out.println("Please enter password");
				char[] password = scan.nextLine().toCharArray();
				KeyStore ks = KeyStore.getInstance("JKS");
				KeyStore ts = KeyStore.getInstance("JKS");
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				SSLContext ctx = SSLContext.getInstance("TLS");
				ks.load(new FileInputStream(username + "keystore"), password); // keystore
																				// password
				char[] password2 = "password".toCharArray(); // (storepass)
				ts.load(new FileInputStream(username + "truststore"), password); // truststore
				// password
				// (storepass);
				kmf.init(ks, password); // user password (keypass)
				tmf.init(ts); // keystore can be used as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				factory = ctx.getSocketFactory();
			} catch (Exception e) {
				System.out.println("wrong password or username \n \n");
				return;
			}

			SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
			// System.out.println("\nsocket before handshake:\n" + socket +
			// "\n");

			/*
			 * send http request
			 *
			 * See SSLSocketClient.java for more information about why there is
			 * a forced handshake here when using PrintWriters.
			 */
			socket.startHandshake();

			SSLSession session = socket.getSession();

			X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];

			String issuer = cert.getIssuerDN().getName();
			String subject = cert.getSubjectDN().getName();
			System.out.println(subject.substring(3,11));
			if(!subject.substring(3, 11).equals("MyServer")) {
				System.out.println("NEIN!! DAS SERVER IS EINE IMPOSTERU!");
				return;
			}
			System.out.println("socket after handshake:\n" + socket + "\n");

			System.out.println("secure connection established\n\n Welcome ");

			BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String msg;
			for (;;) {

				System.out.print(">");

				String input = null;
				input = in.readLine().replaceAll("#NEWLINE#", "\n");
				System.out.println(input);

				msg = read.readLine();

				if (msg.equalsIgnoreCase("quit")) {
					quitflag = true;
					break;
				}
				out.println(msg);
				out.flush();

			}
			in.close();
			out.close();
			read.close();
			socket.close();
		} catch (
		Exception e) {
			System.out.println("Wrong user/password");
		}
	}

}
