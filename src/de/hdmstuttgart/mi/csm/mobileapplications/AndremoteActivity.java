package de.hdmstuttgart.mi.csm.mobileapplications;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import de.hdmstuttgart.mi.csm.mobileapplications.server.HessianServer;


public class AndremoteActivity extends Activity implements HessianServer.Listener {
    
    private TextView textField;
    private TextView numberField;
    
    /** Called when the activity is first created. */    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.textField = (TextView) this.findViewById(R.id.textLabel);
        this.numberField = (TextView) this.findViewById(R.id.numberLabel);
        
        /**
         * Just create a server.. the rest is handled internally, as no callbacks are set.
         */
        HessianServer server = new HessianServer(HelloWorld.class);
        server.addListener(this);
    }

    @Override
    public void onReceivedObject(final Object o) {
        textField.post(new Runnable(){
            public void run(){
                HelloWorld hw = (HelloWorld)o;
                textField.setText(hw.text);
                numberField.setText(String.format("%d",hw.number));
            }
        });
    }
}