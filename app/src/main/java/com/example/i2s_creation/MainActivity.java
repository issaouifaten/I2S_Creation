package com.example.i2s_creation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {


    EditText edtuserid, edtpass;
    Button btnlogin, server, back, btuser, btnpass;
    final Context co = this;
    ConnectionClass connectionClass;
    String CodeRepresentant;
    String user, password, base, ip;
    ResultSet rs;
    Boolean test;
    String CodeSociete, NomUtilisateur, CZ, RAD, Commercial, Admin, CodeEmployer, CodeZone = "";
    boolean st = false;
    String prefname = "usersession", NomSociete = "";
    Boolean Actif = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionClass = new ConnectionClass();

        btnlogin = (Button) findViewById(R.id.btlogin);
        //  pbbar = (ProgressBar) findViewById(R.id.pbbar);


        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        user = pref.getString("user", user);
        ip = pref.getString("ip", ip);
        password = pref.getString("password", password);
        base = pref.getString("base", base);
        connectionClass = new ConnectionClass();

        edtuserid = (EditText) findViewById(R.id.edtuserid);
        edtpass = (EditText) findViewById(R.id.edtpass);


        final Context co = this;
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DoLogin doLogin = new DoLogin();
                doLogin.execute("");


            }
        });


        //********************************************************************////////////
        //********************************************************************////////////


    }


    public class DoLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;


        String userid = edtuserid.getText().toString();
        String p = edtpass.getText().toString();


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {

          Toast.makeText(MainActivity.this, r+Actif, Toast.LENGTH_SHORT).show();
            if (Actif) {
                if (isSuccess) {

                    SharedPreferences prefs = MainActivity.this.getSharedPreferences(prefname, Context.MODE_PRIVATE);
                    SharedPreferences.Editor edt = prefs.edit();
                    edt.putBoolean("etat", true);
                    edt.putString("NomUtilisateur", NomUtilisateur);
                    edt.putString("CodeEmployer", CodeEmployer);
                    edt.putString("NomSociete", NomSociete);

                    edt.commit();

                    Intent i = new Intent(MainActivity.this, MenuHome.class);

                    startActivity(i);
                    Toast.makeText(getApplicationContext(), NomUtilisateur, Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Verifiez vos Données", Toast.LENGTH_SHORT).show();


                }
            } else {


                AlertDialog.Builder alt = new AlertDialog.Builder(co);

                alt.setIcon(R.drawable.i2s);
                alt.setTitle("Erreur License    " );
                alt.setMessage("Prière de contacter IDEAL SOFTWARE SOLUTION sur\nTel : 74 440 602 \nMail : sales@ideal2s.com) ");
                alt.setCancelable(false);
                alt.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                AlertDialog d = alt.create();
                d.show();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            if (userid.trim().equals("") || password.trim().equals(""))
                z = "Please enter User Id and Password";
            else {
                try {
                    Connection con = connectionClass.CONN(ip, password, user, base);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "SELECT *,(select RaisonSociale from Societe)as NomSociete from Utilisateur  " +
                                " where Utilisateur.NomUtilisateur='" + userid + "' and MotDePasse='" + p + "'";
                        Log.e("q",query);
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);


                        if (rs.next()) {
                            boolean TEST = false;

                            NomUtilisateur = rs.getString("NomUtilisateur");
                            NomSociete = rs.getString("NomSociete");
                            Actif = rs.getBoolean("Actif");
                            Log.e("Actif",""+Actif);


                            CodeEmployer = "";
                            isSuccess = true;
                            z = "Login avec succée";


                        }


                    }
                } catch (SQLException ex) {
                    isSuccess = false;
                    // z = ex.toString();
                }
            }
            return z;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences p = getSharedPreferences(prefname, Context.MODE_PRIVATE);
        st = p.getBoolean("etat", false);
        if (st == true) {
            Intent i = new Intent(MainActivity.this, MenuHome.class);
            startActivity(i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menuparametre, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            LayoutInflater li = LayoutInflater.from(co);
            View px = li.inflate(R.layout.print, null);
            final AlertDialog.Builder alt = new AlertDialog.Builder(co);
            alt.setIcon(R.drawable.i2s);
            alt.setTitle("Parametre");
            alt.setView(px);

            connectionClass = new ConnectionClass();

            final EditText edtuserid = (EditText) px.findViewById(R.id.edtuserid);
            final EditText edtpass = (EditText) px.findViewById(R.id.edtpass);

            alt.setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface di, int i) {

                                    if (edtuserid.getText().toString().equals("admin") && edtpass.getText().toString().equals("admin")) {
                                        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor edt = pref.edit();
                                        edt.putBoolean("etatsql", false);
                                        edt.commit();
                                        Intent inte = new Intent(getApplicationContext(), Parametrage.class);
                                        startActivity(inte);
                                    } else {


                                        Toast.makeText(getApplicationContext(), "Erreur login", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                    .setNegativeButton("Annuler",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface di, int i) {
                                    di.cancel();
                                }
                            });
            final AlertDialog d = alt.create();


            d.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    //   d.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundResource(R.drawable.bt);
                    //  d.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundResource(R.drawable.bt);


                }
            });

            d.show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
