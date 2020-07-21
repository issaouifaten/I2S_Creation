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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Inventaire extends AppCompatActivity {
    ConnectionClass connectionClass;
    String user, password, base, ip, NomUtilisateur = "";
    final Context co = this;
    GridView gridInvantaire;
    public static String NumeroInventaire = "", Depot = "", TypeInventaire = "";
    ProgressBar progressBar;
    TextView txt_numinventaire,txt_code;
    EditText edt_qt,edt_recherche;
    Button bt_scan;
    private ZXingScannerView scannerView;
    String CodeArticleScan="",qt_saisi="0",query="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventaire);

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
        Intent intent=getIntent();
        NumeroInventaire =intent.getStringExtra("NumeroInventaire");
        txt_numinventaire=(TextView)findViewById(R.id.numinventaire);
        txt_numinventaire.setText(NumeroInventaire);
        bt_scan=(Button)findViewById(R.id.btscan);
        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scannerView = new ZXingScannerView(Inventaire.this);
                scannerView.setResultHandler(new ZXingScannerResultHandler());
                setContentView(scannerView);
                scannerView.startCamera();

            }
        });
        gridInvantaire=(GridView)findViewById(R.id.gridinventaire) ;
        edt_recherche=(EditText)findViewById(R.id.edtrecherche) ;
        FillList fillList=new FillList();
        fillList.execute("");
        Button btrecherche=(Button)findViewById(R.id.btrecherche);
        btrecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t=edt_recherche.getText().toString();
                query="select * from Article where Actif=1 and  ( Designation like '%"+t+"%' or  CodeArticle like'%"+t+"%')";
                FillListRecherche fillListRecherche=new FillListRecherche();
                fillListRecherche.execute("");
            }
        });
        Button btlist=(Button)findViewById(R.id.btinventaire);
        btlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FillList fillList=new FillList();
                fillList.execute("");
            }
        });

    }
    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {


        @Override
        public void handleResult(Result result) {
            String resultCode = result.toString();
            Toast.makeText(Inventaire.this, resultCode, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alt = new AlertDialog.Builder(co);
            LayoutInflater li = LayoutInflater.from(co);
            View px = li.inflate(R.layout.diag_saisi_inventaire, null);
            alt.setView(px);
        //    gridRecherche = (GridView) px.findViewById(R.id.grid);
        //    pbbar = (ProgressBar) px.findViewById(R.id.pb);
            txt_code = (TextView) px.findViewById(R.id.code_article);
            txt_code.setText(resultCode);
            edt_qt = (EditText) px.findViewById(R.id.qt);

             alt.setIcon(R.drawable.i2s);
            alt.setTitle("Scan :  " );


            alt.setPositiveButton("fermer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), Inventaire.class);
                    intent.putExtra("NumeroInventaire",NumeroInventaire);
                    startActivity(intent);

                }
            });
            alt.setNegativeButton("valider", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CodeArticleScan=txt_code.getText().toString();
                    qt_saisi=edt_qt.getText().toString();
                    InsertInventaire insertInventaire=new InsertInventaire();
                    insertInventaire.execute("");

                }
            });

            AlertDialog d = alt.create();
            d.show();


            scannerView.stopCamera();


        }
    }




    public class InsertInventaire extends AsyncTask<String, String, String> {


        Boolean isSuccess = false;
        String z = "";


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), r , Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), Inventaire.class);
            intent.putExtra("NumeroInventaire",NumeroInventaire);
            startActivity(intent);


        }

        @Override
        protected String doInBackground(String... params) {
            if (false) {
                z = "Erreur ";
            } else {
                try {
                    Connection con = connectionClass.CONN(ip, password, user, base);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {




                        String query2 = " INSERT INTO   ComptageReel \n" +
                                "           ( NumeroInventaire \n" +
                                "           , NumeroComptage \n" +
                                "           , NumeroOrdre \n" +
                                "           , CodeBarre \n" +
                                "           , DateSaisie \n" +
                                "           , CodeArticle \n" +
                                "           , Quantite \n" +
                                "           , NomUtilisateur \n" +
                                "           , Importee )\n" +
                                "     VALUES\n" +
                                "           ( '"+NumeroInventaire+"' " +
                                "           ,'1'\n" +
                                "           ,(select isnull (max(NumeroOrdre)+1,1) from ComptageReel where NumeroInventaire='"+NumeroInventaire+"')\n" +
                                "           , '"+CodeArticleScan+"' \n" +
                                "           ,getdate()\n" +
                                "           ,(select CodeArticle from article where CodeBarre='"+CodeArticleScan+"')\n" +
                                "           ,"+qt_saisi+"\n" +
                                "           ,'"+NomUtilisateur+"'" +
                                "           ,0)\n" +
                                " " ;
                        Log.e("q",query2);
                        PreparedStatement preparedStatement = con.prepareStatement(query2);


                        preparedStatement.executeUpdate();







                    }
                } catch (SQLException ex) {
                    isSuccess = false;
                    z = "echec import ancien compteur" + ex.toString();
                    Log.e("erreur code barre",ex.toString());
                }
            }
            return z;
        }
    }



    public class FillList extends AsyncTask<String, String, String> {
        List<Map<String, String>> prolist = new ArrayList();

        String z = "", Total = "", TotalDate = "";

        public FillList() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            //  progressBar.setVisibility(View.VISIBLE);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String r) {

            //   progressBar.setVisibility(View.GONE);
            //CodeDemande ,CodeDemandeur ,Nom,CONVERT(date,DateCreation)AS DateCreation, Etat.Libelle AS EtatPiece ,NomUtilisateur
            String[] from = { "CodeArticle","Quantite","CodeBarre","Designation"};
            int[] views = {R.id.txt_code_article, R.id.txt_qt,R.id.txt_code_barre,R.id.txt_designation};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.item_comptage, from,
                    views);
            gridInvantaire.setAdapter(ADA);
            gridInvantaire.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap) ADA.getItem(arg2);




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
                    String q="select *, (SELECT Designation from Article where Article.CodeArticle=ComptageReel.CodeArticle) as Designation from ComptageReel where NumeroInventaire='"+NumeroInventaire+"' " +
                            " and ComptageReel.NomUtilisateur ='"+NomUtilisateur+"' order by DateSaisie Desc";

Log.e("inventairelist",q);
                    ResultSet rs = con.prepareStatement(q).executeQuery();
                    new ArrayList();
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<>();

                        datanum.put("CodeArticle", rs.getString("CodeArticle"));
                        datanum.put("Quantite", rs.getString("Quantite"));
                        datanum.put("DateSaisie", rs.getString("DateSaisie"));
                        datanum.put("Designation", rs.getString("Designation"));
                        datanum.put("CodeBarre", rs.getString("CodeBarre"));


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




    public class FillListRecherche extends AsyncTask<String, String, String> {
        List<Map<String, String>> prolist = new ArrayList();

        String z = "", Total = "", TotalDate = "";


        /* access modifiers changed from: protected */
        public void onPreExecute() {

            //pbbar.setVisibility(View.VISIBLE);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String r) {

         //   pbbar.setVisibility(View.GONE);

         //   txtexiste.setText(r);


            //CodeDemande ,CodeDemandeur ,Nom,CONVERT(date,DateCreation)AS DateCreation, Etat.Libelle AS EtatPiece ,NomUtilisateur
            String[] from = {"Designation",  "NumeroLigne", "CodeBarre"};
            int[] views = {R.id.txt_designation, R.id.txt_num, R.id.txt_code_barre};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.item_recherche_inventaire, from,
                    views);
            gridInvantaire.setAdapter(ADA);
            gridInvantaire.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap) ADA.getItem(arg2);
                    AlertDialog.Builder alt = new AlertDialog.Builder(co);
                    //    LayoutInflater li = LayoutInflater.from(co);
                    //    View px = li.inflate(R.layout.diagprix, null);
                    //    alt.setView(px);
                    final String Designation = (String) obj.get("Designation");
                    final String NumeroLigne = (String) obj.get("NumeroLigne");
                    final String CodeBarre = (String) obj.get("CodeBarre");


                    LayoutInflater li = LayoutInflater.from(co);
                    View px = li.inflate(R.layout.diag_saisi_inventaire, null);
                    alt.setView(px);
                    //    gridRecherche = (GridView) px.findViewById(R.id.grid);
                    //    pbbar = (ProgressBar) px.findViewById(R.id.pb);
                    txt_code = (TextView) px.findViewById(R.id.code_article);
                    txt_code.setText(CodeBarre);
                    edt_qt = (EditText) px.findViewById(R.id.qt);

                    alt.setIcon(R.drawable.i2s);
                    alt.setTitle("Scan :  " );


                    alt.setPositiveButton("fermer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), Inventaire.class);
                            intent.putExtra("NumeroInventaire",NumeroInventaire);
                            startActivity(intent);

                        }
                    });
                    alt.setNegativeButton("valider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CodeArticleScan=txt_code.getText().toString();
                            qt_saisi=edt_qt.getText().toString();
                            InsertInventaire insertInventaire=new InsertInventaire();
                            insertInventaire.execute("");

                        }
                    });

                    AlertDialog d = alt.create();
                    d.show();
                }
            });
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    this.z = "Connexion n'est pas établie";
                } else {


                    Log.e("query", query);
                    ResultSet rs = con.prepareStatement(query).executeQuery();
                    new ArrayList();
                    z="l'article n'existe pas";
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<>();
                        datanum.put("Designation", rs.getString("Designation"));
                        datanum.put("CodeArticle", rs.getString("CodeArticle"));

                        datanum.put("CodeBarre", rs.getString("CodeBarre"));

                        //    datanum.put("Total", rs.getString("Total"));

                        Total ="0";
                        TotalDate ="0";
                        this.prolist.add(datanum);
                        z="";
                    }

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
