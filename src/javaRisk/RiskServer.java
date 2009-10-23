package javaRisk;
import java.awt.Color;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The RiskServer class contains code to accept incoming connections
 * and delegate them.
 */
public class RiskServer {

	/**
	 * Flag to determining if new clients should be accepted.
	 */
	private static boolean acceptingClients = true;
	
	/**
	 * Main method. Accepts
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket();
			ss.bind( new InetSocketAddress(Constants.HOST, Constants.PORT) );
			System.out.println("Server waiting on port " + ss.getLocalPort());
			
			ServerModel model = new ServerModel();
			for(;;) {
				while (acceptingClients) {
					Socket socket = ss.accept();
					System.out.println("Connect: " + socket.getRemoteSocketAddress().toString());
					
					ClientProxy proxy = new ClientProxy( socket );
					model.addListener( proxy );
					proxy.setModel(model);
					proxy.start();
				}
				if( model.getWinner() != null ) {
					acceptingClients = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stops the server from accepting new clients.
	 */
	void stopServer() {
		// no-modifier so only this package can call it
		acceptingClients = false;
	}
	
}
