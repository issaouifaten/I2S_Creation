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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChoixInventaire extends AppCompatActivity {
    ConnectionClass connectionClass;
    String user, password, base, ip, NomUtilisateur = "";
    final Context co = this;
    GridView gridInvantaire;
    public static String NumeroInventaire = "", Depot = "", TypeInventaire = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choix_inventaire);


        progressBar=(ProgressBar)findViewById(R.id.progressBar);

        ////session base
        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        user = pref.getString("user", user);
        ip = pref.getString("ip", ip);
        password = pref.getString("password", password);
        base = pref.getString("base", base);
        ////SESSION UTILISATEUR
        SharedPreferences prefe = getSharedPreferences("usersession", Context.MODE_PRIVATE);
        SharedPreferences.Editor edte = prefe.edit();
        NomUtilisateur = prefe.getString("NomUtilisateur", NomUtilisateur);


        connectionClass = new ConnectionClass();
        gridInvantaire = (GridView) findViewById(R.id.gridinventaire);

        FillList fillList=new FillList();
        fillList.execute("");

    }


    public class FillList extends AsyncTask<String, String, String> {
        List<Map<String, String>> prolist = new ArrayList();

        String z = "", Total = "", TotalDate = "";

        public FillList() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String r) {

          progressBar.setVisibility(View.GONE);
            //CodeDemande ,CodeDemandeur ,Nom,CONVERT(date,DateCreation)AS DateCreation, Etat.Libelle AS EtatPiece ,NomUtilisateur
            String[] from = {"NumeroInventaire", "Libelle"};
            int[] views = {R.id.code, R.id.designation};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.item_inventaire, from,
                    views);
            gridInvantaire.setAdapter(ADA);
            gridInvantaire.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap) ADA.getItem(arg2);
                    AlertDialog.Builder alt = new AlertDialog.Builder(co);

                    final String NumeroInventaire = (String) obj.get("NumeroInventaire");
                    final String DateInventaire = (String) obj.get("DateInventaire");
                    final String Libelle = (String) obj.get("Libelle");

                            Intent intent = new Intent(getApplicationContext(), Inventaire.class);
                            intent.putExtra("NumeroInventaire", NumeroInventaire);
                            intent.putExtra("DateInventaire", DateInventaire);
                            intent.putExtra("Libelle", Libelle);
                            startActivity(intent);



                }
            });
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    this.z = "Error in connection with SQL server";
                } else {


                    ResultSet rs = con.prepareStatement("select * from Inventaire inner join Depot on Depot.CodeDepot=Inventaire.CodeDepot where NomValidateur=''").executeQuery();
                    new ArrayList();
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<>();
                        datanum.put("NumeroInventaire", rs.getString("NumeroInventaire"));
                        datanum.put("DateInventaire", rs.getString("DateInventaire"));
                        datanum.put("Libelle", rs.getString("Libelle"));


                        this.prolist.add(datanum);
                    }
                    this.z = "Success";
                }
            } catch (SQLException ex) {
                this.z = ex.toString();
                Log.e("erreur_rq", ex.toString());
            } catch (Exception e) {
                Log.e("erreur ", e.toString());
            }
            return this.z;
        }
    }











}
