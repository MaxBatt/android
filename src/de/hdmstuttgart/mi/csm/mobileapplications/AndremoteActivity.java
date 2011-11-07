package de.hdmstuttgart.mi.csm.mobileapplications;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.hdmstuttgart.mi.csm.mobileapplications.client.HessianClient;
import de.hdmstuttgart.mi.csm.mobileapplications.server.HessianServer;


public class AndremoteActivity extends Activity implements HessianServer.Listener, OnClickListener {

    private TextView textField;
    private TextView numberField;
	private Button sendButton;
	
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
        
        /** Listener for Client's Send-Button. */
        sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this); 
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
    
    /** Listener for Send-Button. */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		/** Read out entered Values. */
		EditText inputHost 	= (EditText) findViewById(R.id.inputHost);
		EditText inputPort 	= (EditText) findViewById(R.id.inputPort);
		EditText inputText 	= (EditText) findViewById(R.id.inputHost);
		EditText inputNumber= (EditText) findViewById(R.id.inputNumber);
		String host 		= inputHost.getText().toString();
		int port 			= Integer.parseInt(inputPort.getText().toString());
		String text 		= inputText.getText().toString();
		int number 			= Integer.parseInt(inputNumber.getText().toString());
		
		HelloWorld hw = new HelloWorld();
		hw.text = text;
		hw.number = number;
		
		/** Construct the Hessian Client */
		HessianClient client = new HessianClient(host,port);
		
		/** Use Method Send to Serialize and send object */
		client.Send(hw); 
	}
}