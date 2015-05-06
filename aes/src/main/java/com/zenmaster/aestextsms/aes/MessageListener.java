package com.zenmaster.aestextsms.aes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by chnt on 02/05/2014.
 */
//interceptor del mensaje
public class MessageListener extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        // si la accion equivale a un mensaje recibido entonces
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();

                        // variable no cifrada
                        String msgBody = msgs[i].getMessageBody();
                        Log.d("MENSAJE: ", msgBody);


                        // llave Compartida
                        AES aes = new AES();

                        //desencriptar
                        String m = aes.Decipher(msgBody);

                        //desencriptar con DH


                        //MENSAJE PARA QUE SE MUESTRE EN NOTIFICACION
                        Toast.makeText(context, msgBody, Toast.LENGTH_LONG).show();

                        //muestro el mensaje final
                        showNotification(context, m.replace("0", ""));

                        Intent abrirApp = new Intent();

                        abrirApp.setClassName("com.zenmaster.aestextsms.aes",
                                "com.zenmaster.aestextsms.aes.MainActivity");

                        abrirApp.setAction(Intent.ACTION_SEND);

                        abrirApp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        //enviar un texto extra y voy a enviar el mensaje decifrado intercambiando 0 por espacios en nulo
                        abrirApp.putExtra(Intent.EXTRA_TEXT, m.replace("0", ""));

                       // abrirApp.putExtra(Intent.EXTRA_TEXT, msgBody);

                        abrirApp.setType("text/plain");

                        Log.d("DEscifrado", m.replace("0", ""));

                        context.startActivity(abrirApp);
                    }

                } catch (Exception e) {

                }
            }
        }
    }

    private void showNotification(Context context, String message) {
       Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
