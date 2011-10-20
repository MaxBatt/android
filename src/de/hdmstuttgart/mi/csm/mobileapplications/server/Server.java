package de.hdmstuttgart.mi.csm.mobileapplications.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * Listening Server, Opens a socket and waits for incoming connections. Notifies
 * its listeners of events.
 * 
 * @author moritzhaarmann
 * 
 */
public class Server {

    public final static int SERVER_PORT = 8998;

    /**
     * Reference to listeners
     */
    private List<ServerListener> listeners;

    /**
     * The accepting server socket
     */
    private ServerSocket serverSocket;

    /**
     * The thread in which connections are accepted.
     */
    private Thread acceptThread;

    public Server() {
        this.listeners = new ArrayList<ServerListener>();
    }

    /**
     * Opens the socket. Handles all errors internally and just returns true or
     * false.
     * 
     * @return true if the socket could be opened, false if not.
     */
    public boolean open() {
        boolean returnValue = true;

        try {
            // 10 stands for the backlog
            this.serverSocket = new ServerSocket(Server.SERVER_PORT, 10);
        } catch (IOException e) {
            Log.d("server", "Exception: " + e);
            returnValue = false;
        }

        return returnValue;
    }

    /**
     * Starts accepting connections. Contrary to ServerSocket, this
     * implementation is non-blocking and can therefore be safely called from a
     * UI-Thread.
     */
    public void accept() {
        /*
         * Create the anonymous class holding the thread.
         */
        this.acceptThread = new Thread() {
            public void run() {
                // NewSocket holds the sockets that represent a client.
                Socket newSocket;
                try {
                    // Run 4Eva.
                    while (true) {

                        newSocket = Server.this.serverSocket.accept();

                        Log.d("server", "Server Accepted a new connection");
                        
                        // Notify listeners. ( On same thread )
                        for (ServerListener listener : Server.this.listeners) {
                            listener.onNewConnection(newSocket);
                        }

                    }
                } catch (IOException e) {
                    Log.d("server", "Server Couldn't be established, error: "
                            + e);

                    for (ServerListener listener : Server.this.listeners) {
                        listener.onError(e);
                    }
                }
            }
        };
        this.acceptThread.start();
    }

    /**
     * Adds a listener . 
     * @param listener the new listener.
     */
    public void addListener(ServerListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener
     * @param listener the listener that should be removed.
     */
    public void removeListener(ServerListener listener) {
        this.listeners.remove(listener);
    }

}
