package de.hdmstuttgart.mi.csm.mobileapplications.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import android.content.Context;
import android.net.wifi.WifiManager.MulticastLock;
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
     * 
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
     * JmDNS handles the zeroconf stuff. good to have it around.
     */
    private JmDNS jmdns;

    // We need this lock to ensure we can use multicast DNS.
    private MulticastLock lock;

    /**
     * Reference to the context is required for jmdns.
     */
    private Context context;

    /**
     * Public constructor, Initializes the server and starts accepting
     * connections.
     * 
     * @param expectedClass
     * @throws IOException 
     */
    public HessianServer(Class<?> expectedClass, Context context) throws IOException {
        this.expectedClass = expectedClass;
        this.context = context;
        this.server = new Server();
        this.server.addListener(this);
        this.listeners = new ArrayList<Listener>();

        /*
         * Open and accept could be one call
         */
        this.server.open();
        this.server.accept();
        this.registerBonjour();
    }
    
    /**
     * Close the server.    
     */
    public void close(){
        this.deregisterBonjour();
        this.server.close();
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
            for (Listener l : this.listeners) {
                l.onReceivedObject(o);
            }

        } catch (IOException e) {
            // NOthing so far.
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Registers the current server using multicast dns.
     * @throws IOException 
     */
    private void registerBonjour() throws IOException {
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
                .getSystemService(android.content.Context.WIFI_SERVICE);
        lock = wifi.createMulticastLock("HeeereDnssdLock");
        // Hahahahahaha. can't stop laughing. Do they have arc in java land?
        lock.setReferenceCounted(true);
        lock.acquire();
        this.jmdns = JmDNS.create();
        this.jmdns.registerService(ServiceInfo.create("_hessian._tcp.local.", "HessianServer", Server.SERVER_PORT, "blablabla"));
    }

    /**
     * Cancels the mDNS announcement.
     */
    private void deregisterBonjour() {
        this.jmdns.unregisterAllServices();
        if (lock != null)
            lock.release();

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
     * 
     * @param listener
     *            the new listener.
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener
     * 
     * @param listener
     *            the listener that should be removed.
     */
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
}
