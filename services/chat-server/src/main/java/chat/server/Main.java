package chat.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class Main {
	public static void main(String[] args) {
		final String baseUri = "http://localhost:5000/";
		final String paket = "chat.server";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages", paket);
		System.out.println("Starte grizzly...");
		SelectorThread threadSelector = null;
		try {
			threadSelector = GrizzlyWebContainerFactory.create(baseUri, initParams);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("Grizzly l�uft unter %s%n", baseUri);
		// Wait forever
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Grizzly wurde beendet");
		System.exit(0);
	}

	public static void starteChatServer(String uri){
		final String baseUri = uri;
		final String paket = "chat.server";
		final Map<String, String> initParams = new HashMap<String, String>();

		initParams.put("com.sun.jersey.config.property.packages", paket);
		System.out.println("Starte grizzly...");
		SelectorThread threadSelector = null;
		try {
			threadSelector = GrizzlyWebContainerFactory.create(baseUri, initParams);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.printf("Grizzly l�uft unter %s%n", baseUri);
		// Wait forever
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Grizzly wurde beendet");
		System.exit(0);
	}
	public static void stopChatServer(){
		System.exit(0);
	}
}
