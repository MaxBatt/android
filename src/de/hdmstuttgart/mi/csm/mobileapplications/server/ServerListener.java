package de.hdmstuttgart.mi.csm.mobileapplications.server;

import java.net.Socket;

/**
 * Server events to be handled by listeners.
 * @author moritzhaarmann
 *
 */
public interface ServerListener {
    /**
     * Called when a new connection arrives.
     * @param socket
     */
    public void onNewConnection(Socket socket);
    
    /**
     * Called in case of an error
     * @param e
     */
    public void onError(Exception e);
}
