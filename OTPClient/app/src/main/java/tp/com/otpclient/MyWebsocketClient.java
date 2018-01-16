package tp.com.otpclient;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import tech.gusavila92.websocketclient.WebSocketClient;

import static android.content.ContentValues.TAG;
import static tp.com.otpclient.OTPVerifyService.numbersTosendSms;
import static tp.com.otpclient.smsService.receivedContacts;

/**
 * Created by Aruna on 07-12-2017.
 */

public class MyWebsocketClient {
    public static WebSocketClient webSocketClient;

    public static WebSocketClient createWebSocketClient() {
        URI uri;
        try {
            uri = new URI("ws://162.222.177.34:9090");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        webSocketClient = new WebSocketClient(uri) {


            @Override
            public void onOpen() {
                Log.i(TAG, "In OnOpen. ");
            }

            @Override
            public void onTextReceived(String message) {
                Log.i(TAG, "onTextReceived . " + message);

                Gson gson = new Gson();
                try {
                    Comms comsObj = gson.fromJson(message, Comms.class);

                    if (comsObj != null) {
                        numbersTosendSms.push(comsObj);

                    }
                } catch (Exception ex) {
                }
                System.out.println("onTextReceived");
            }

            @Override
            public void onBinaryReceived(byte[] data) {
                System.out.println("onBinaryReceived");
            }

            @Override
            public void onPingReceived(byte[] data) {
                System.out.println("onPingReceived");
            }

            @Override
            public void onPongReceived(byte[] data) {
                System.out.println("onPongReceived");
            }

            @Override
            public void onException(Exception e) {

            }

            @Override
            public void onCloseReceived() {
                //   SocketCommunicationEstablisher.mysock = MyWebsocketClient.createWebSocketClient();
            }


        };

        webSocketClient.addHeader("OTP", "");

        return webSocketClient;
    }
}
