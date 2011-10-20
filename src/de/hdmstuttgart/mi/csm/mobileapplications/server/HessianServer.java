package de.hdmstuttgart.mi.csm.mobileapplications.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import android.util.Log;

import com.caucho.hessian.io.Hessian2Input;

import de.hdmstuttgart.mi.csm.mobileapplications.HelloWorld;

/**
 * Basic Hession Server. Uses the Server to wait for connections, then handles
 * them and reads a hello world object.
 * 
 * @author moritzhaarmann
 * 
 */
public class HessianServer implements ServerListener {
    /**
     * The listener interface
     * @author moritzhaarmann
     *
     */
    public interface Listener {
        public void onReceivedObject(Object o);
    }
    
    /**
     * Holds the "expected class" that is read from the stream
     */
    private Class<?> expectedClass;

    /**
     * The Server used for networking
     */
    private Server server;
    
    private ArrayList<Listener> listeners;

    /**
     * Public constructor, Initializes the server and starts accepting
     * connections.
     * 
     * @param expectedClass
     */
    public HessianServer(Class<?> expectedClass) {
        this.expectedClass = expectedClass;

        this.server = new Server();
        this.server.addListener(this);
        this.listeners = new ArrayList<Listener>();

        /*
         * Open and accept could be one call
         */
        this.server.open();
        this.server.accept();
    }

    /**
     * Implementation of the callback that is received when a new client
     * connects to the server.
     */
    @Override
    public void onNewConnection(Socket socket) {

        try {
            InputStream is = socket.getInputStream();
            // Create the Hessian input stream using the input stream
            Hessian2Input input = new Hessian2Input(is);
            // Magic: Read the object from the Hessian Input Stream
            HelloWorld o = (HelloWorld) input.readObject(this.expectedClass);
            Log.d("Decoding", "Output is " + o.text);
            for ( Listener l : this.listeners) {
                l.onReceivedObject(o);
            }
            
        } catch (IOException e) {
            // NOthing so far.
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Just logs any socket error in case the server reports it.
     */
    @Override
    public void onError(Exception e) {
        System.out.println("Got an error: " + e);

    }
    

    /**
     * Adds a listener . 
     * @param listener the new listener.
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener
     * @param listener the listener that should be removed.
     */
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
}
