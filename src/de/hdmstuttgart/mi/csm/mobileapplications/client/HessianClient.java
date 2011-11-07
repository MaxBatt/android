package de.hdmstuttgart.mi.csm.mobileapplications.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import android.util.Log;
import com.caucho.hessian.io.Hessian2Output;
import de.hdmstuttgart.mi.csm.mobileapplications.HelloWorld;

public class HessianClient {

	private String host;
	private int port;

	/** Constructor only assigns host and port */
	public HessianClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/** Method for serializing object and sending over socket */
	public void Send(HelloWorld hw) {
		/**
		 * Create socket with given host and port Sending Hessian object over
		 * Socket
		 */
		try {
			Socket socket = new Socket(this.host, this.port);
			OutputStream os = socket.getOutputStream();
			Hessian2Output hessianOut = new Hessian2Output(os);
			// Why are you writing the Hessian2Output to an ObjectOutputStream?
			// You should be
			hessianOut.writeObject(hw);
		} catch (IOException e) {
			Log.d("Socket Send", "Exception: " + e);
		}
	}

}
