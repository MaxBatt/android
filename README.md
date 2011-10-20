Android Hessian Server/Client
------
You'll want to have a project in eclipse named hessdroid containing hessdroid. You need to specifiy that project in the Andremote java build path in eclipse, otherwise the project won't build. 

The Android device is listening on port 8998, the easist way to test the setup is using an adb port forward like that..

	./adb forward tcp:9999 tcp:9889 
	
that way, connections to localhost 9999 will be forwarded to the emulator. 