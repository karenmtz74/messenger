package com.zenmaster.aestextsms.aes;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends Activity {

    ImageButton send;
    EditText text, number;
    TextView result;


    @Override
    //lo que se ejecuta al inicio de la app
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//va a pintar en la pantalla lo de activity main comand click
        send = (ImageButton)findViewById(R.id.imageButton);// la burbuja
        text = (EditText)findViewById(R.id.editText);
        result = (TextView)findViewById(R.id.txtCipher);
        number = (EditText)findViewById(R.id.editNumber);

        // cuando le das clicl sucede todo lo del try
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String sms = text.getText().toString();//editable y eso a string

                    //llave privada
                    AES aes = new AES();

                    // ENCRIPTAR EN AES
                    String c = aes.Cipher(sms);

                    //mandar un mensaje de error visible uno para la compu con el mensaje
                    //cifrado y el log se puede abrir dando ALT 6
                    Log.e("MENSAJE AES: ", c);

                    //instanteando el objeto que puede enviar mensaje de texto en el cel

                    SmsManager smsManager = SmsManager.getDefault();

                    // enviar el mensaje direcion sc, mensaje, pending intent, delivery intent
                    smsManager.sendTextMessage(number.getText().toString(), null, c, null, null);


                }catch (NullPointerException e){

                }
            }
        });
    }

    @Override

    //cachar el mensaje de texto y lo imprime en result
    protected void onResume() {

        // recibe el mensaje extraetodo siempre y cuando haya sido invocado desde el reciver
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            Log.d("ACTION SEND:", "true");
            if ("text/plain".equals(type)) {

                //recuperando la variable string que mande
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                //String otraVariable =intent.getStringExtra(llaveCompartida);

                Log.d("RESULT: ", sharedText);

                // visualizo el mensaje
                result.setText(sharedText);
            }
        }
    }
}
