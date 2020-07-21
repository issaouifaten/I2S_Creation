package com.example.i2s_creation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Parametrage extends AppCompatActivity {



    boolean st = false;
    String prefnamesql = "usersessionsql";
    Button btadd, btcancel;
    EditText edtip, edtpass, edtbase, edtuser, edtcodliv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametrage);
        btadd = (Button) findViewById(R.id.add);

        edtip = (EditText) findViewById(R.id.ip);
        edtpass = (EditText) findViewById(R.id.password);
        edtuser = (EditText) findViewById(R.id.user);
        edtbase = (EditText) findViewById(R.id.db);

        btadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences prefs = Parametrage.this.getSharedPreferences(prefnamesql, Context.MODE_PRIVATE);
                SharedPreferences.Editor edt = prefs.edit();
                edt.putBoolean("etatsql", true);
                edt.putString("user", edtuser.getText().toString());
                edt.putString("password", edtpass.getText().toString());
                edt.putString("base", edtbase.getText().toString());
                edt.putString("ip", edtip.getText().toString());

                edt.commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });



    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences p = getSharedPreferences(prefnamesql, Context.MODE_PRIVATE);
        st = p.getBoolean("etatsql", false);
        if (st == true) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }

    }
}
