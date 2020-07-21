package com.example.i2s_creation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.transform.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AjoutArticle extends AppCompatActivity {
    Spinner spinTVA,spinUnite,spinFamille;
    EditText edtDesignation,edtPrixAchat,edtPrixVente,edtFamille,edtPrixGros,edtValeurUnite;
    ConnectionClass connectionClass;
    String CodeRepresentant,NomUtilisateur="";
    String user, password, base, ip;
    ArrayList<String> data_code_unite = new ArrayList<String>();
    ArrayList<String> data_code_famille = new ArrayList<String>();
    ArrayList<String> data_code_sous_famille = new ArrayList<String>();
    ArrayList<String> data_code_tva= new ArrayList<String>();
    ArrayList<String> data_designation_famille = new ArrayList<String>();
    ArrayList<String> data_designation_unite = new ArrayList<String>();
    ArrayList<String> data_designation_tva = new ArrayList<String>();
    private ZXingScannerView scannerView;
    String CodeArticle="";
    Button btValider;
    Boolean testExiste=false;
    String resultscan="-";
    Context co=this;
    TextView edtCodeBare;
    String Designation="",codeTableArticle="";
    final int sdk = android.os.Build.VERSION.SDK_INT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_article);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted


            ActivityCompat.requestPermissions(AjoutArticle.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }

        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        user = pref.getString("user", user);
        ip = pref.getString("ip", ip);
        password = pref.getString("password", password);
        base = pref.getString("base", base);
        //session user
        SharedPreferences prefuser = getSharedPreferences("usersession", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt_user = pref.edit();

        NomUtilisateur = prefuser.getString("NomUtilisateur", NomUtilisateur);
        connectionClass = new ConnectionClass();

        Button b = (Button) findViewById(R.id.btscan);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scannerView = new ZXingScannerView(AjoutArticle.this);
                scannerView.setResultHandler(new ZXingScannerResultHandler());
                setContentView(scannerView);
                scannerView.startCamera();

            }
        });






        spinTVA=(Spinner)findViewById(R.id.spin_tva);
        spinFamille=(Spinner)findViewById(R.id.spinfamille);
        spinUnite=(Spinner)findViewById(R.id.spin_unite);
        edtCodeBare=(TextView) findViewById(R.id.edt_code_barre);
        edtDesignation=(EditText)findViewById(R.id.edt_designation);
        edtPrixAchat=(EditText)findViewById(R.id.est_prix_achat);
        edtPrixVente=(EditText)findViewById(R.id.edt_prixvente);
   //     edtFamille=(EditText)findViewById(R.id.edt_famille);
        edtPrixGros=(EditText)findViewById(R.id.edt_prixgros);
        edtValeurUnite=(EditText)findViewById(R.id.edt_valeur_unite);

        String query_rp = "select * from UniteArticle  ";

        try {
            Connection connect = connectionClass.CONN(ip, password, user, base);
            PreparedStatement stmt;
            stmt = connect.prepareStatement(query_rp);
            ResultSet rsss = stmt.executeQuery();

            while (rsss.next()) {
                String id = rsss.getString("CodeUnite");
                String libelle = rsss.getString("Libelle");

                data_designation_unite.add(libelle);
                data_code_unite.add(id);

            }


            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),
                    R.layout.spinner, data_designation_unite);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinUnite.setAdapter(adapter);
            spinUnite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        /////////////////////////////////////////////////////////////

        String query_famille = "select FamilleArticle.CodeFamille,FamilleArticle.Libelle,CodeSousFamille from FamilleArticle \n" +
                "inner join SousFamilleArticle on SousFamilleArticle.CodeFamille=FamilleArticle.CodeFamille   \n" +
                " ";

        try {
            Connection connect = connectionClass.CONN(ip, password, user, base);
            PreparedStatement stmt;
            stmt = connect.prepareStatement(query_famille);
            ResultSet rsss = stmt.executeQuery();

            while (rsss.next()) {
                String id = rsss.getString("CodeFamille");
                String libelle = rsss.getString("Libelle");
                String CodeSousFamille = rsss.getString("CodeSousFamille");

                data_designation_famille.add(libelle);
                data_code_famille.add(id);
                data_code_sous_famille.add(CodeSousFamille);

            }


            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),
                    R.layout.spinner, data_designation_famille);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinFamille.setAdapter(adapter);
            spinFamille.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        /////////////////////////////////////////////////////////////




        String query_tva = "select * from TVA";

        try {
            Connection connect = connectionClass.CONN(ip, password, user, base);
            PreparedStatement stmt;
            stmt = connect.prepareStatement(query_tva);
            ResultSet rsss = stmt.executeQuery();


            while (rsss.next()) {
                String id = rsss.getString("CodeTVA");
                String libelle = rsss.getString("TauxTVA");

                data_designation_tva.add(libelle);
                data_code_tva.add(id);

            }


            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),
                    R.layout.spinner, data_designation_tva);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinTVA.setAdapter(adapter);
            spinTVA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }






        ////////////////////////////////////
        btValider=(Button)findViewById(R.id.btvalider);
        btValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtDesignation.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(),"La désignation est Obligatoire",Toast.LENGTH_LONG).show();

                }else if(edtPrixVente.getText().toString().equals("")||edtPrixVente.getText().toString().equals("0")){
                    Toast.makeText(getApplicationContext(),"Le Prix de vente  est Obligatoire",Toast.LENGTH_LONG).show();
                }else{
                    btValider.setEnabled(false);
                    btValider.setBackgroundResource(R.drawable.circle_gray);
                    TestArticle testArticle=new TestArticle();
                    testArticle.execute("");
                }
            }
        });
        Button btAnnuler=(Button)findViewById(R.id.btretour);
        btAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(getApplicationContext(),MenuHome.class);
                startActivity(intent);

            }
        });

    }


    public class Compteur extends AsyncTask<String, String, String> {


        Boolean isSuccess = false;
        String z = "";
        String c, pre = "",sosufamille="";

        @Override
        protected void onPreExecute() {
            sosufamille = data_code_sous_famille.get(spinFamille.getSelectedItemPosition());
        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r + NumCMD, Toast.LENGTH_SHORT).show();
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




                        String  query_code=" SELECT top 1 \n" +
                                "\n" +
                                "(case '"+sosufamille+"' \n" +
                                "when 'BAL 'then\n" +
                                "(select    REPLICATE('0', 6 - LEN(isnull(MAX(CONVERT(NUMERIC,CodeArticle)),0)+1)) + CAST(isnull(MAX(CONVERT(NUMERIC,CodeArticle)),0)+1 AS VARCHAR(6))\n" +
                                "AS code from Article\n" +
                                "where CodeFamille = 'BAL' and CodeSousFamille = 'BAL' and len(CodeArticle)=6) \n" +
                                "\n" +
                                "else\n" +
                                "( (select  'MCC'+REPLICATE('0', 5 - LEN( isnull(MAX(CONVERT(NUMERIC,substring(CodeArticle,4,5))),0) +1)) + CAST( isnull(MAX(CONVERT(NUMERIC,substring(CodeArticle,4,5))),0)+1 AS VARCHAR(5))   AS code from Article where CodeArticle like 'MCC%')\n" +
                                ")\n" +
                                "end )as code\n" +
                                "FROM Article" +
                                " ";
                        Statement stmt2 = con.createStatement();
                        ResultSet rs2 = stmt2.executeQuery(query_code);

                        Log.e("querycomp",query_code);
                        while (rs2.next()) {
                            codeTableArticle = rs2.getString("code");
                            Log.e("codeTableArticle",codeTableArticle);
                        }







                    }
                } catch (SQLException ex) {
                    isSuccess = false;
                    z = "echec import ancien compteur" + ex.toString();
                    Log.e("erreurcompteur",ex.toString());
                }
            }
            return z;
        }
    }

    public class TestArticle extends AsyncTask<String, String, String> {



        String z = "";
        String c, des = "" ;

        @Override
        protected void onPreExecute() {
            des=edtDesignation.getText().toString();
            testExiste=false;
        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r +testExiste  , Toast.LENGTH_SHORT).show();


            if(testExiste)
            {
                Toast.makeText(getApplicationContext(),"Cet article est  déja ajouté ",Toast.LENGTH_LONG).show();
                btValider.setEnabled(true);
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btValider.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_green) );
                } else {
                    btValider.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_green));
                }
            }else{

                InsertDemande insertDemande=new InsertDemande();
                insertDemande.execute("");

            }
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

                        String query = "Select * from Article  where Designation='"+des+"' ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {




                            testExiste = true;


                        }


                    }
                } catch (SQLException ex) {
                    //   testExiste = false;
                    z = "echec  " + ex.toString();
                }
            }
            return z;
        }
    }
    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {


        @Override
        public void handleResult(com.google.zxing.Result result) {
            String resultCode = result.toString();
            Toast.makeText(AjoutArticle.this, resultCode, Toast.LENGTH_SHORT).show();


            // setContentView(R.layout.activity_ajout_article);

            Log.e("codelu", resultCode);
            resultscan=resultCode;
            Toast.makeText(getApplicationContext(),resultCode,Toast.LENGTH_LONG).show();


            scannerView.stopCamera();
            TestArticleBarre testArticleBarre =new TestArticleBarre();
            testArticleBarre.execute("");

        }
    }




    public class InsertDemande extends AsyncTask<String, String, String> {
        String z = "", CodeUnite="";
        String CodeTVA = "", famille = "", prixvente = "", prixachat = "",CodeBare="",PrixGros="",tauxtva="",sousfamille="",valeurunite="";
boolean testinsert=false;
        @Override
        protected void onPreExecute() {
            Compteur compteur=new Compteur();
            compteur.execute("");

            CodeTVA = data_code_tva.get(spinTVA.getSelectedItemPosition());
          //  famille = edtFamille.getText().toString();
            prixvente = edtPrixVente.getText().toString();
            prixachat = edtPrixAchat.getText().toString();
            valeurunite = edtValeurUnite.getText().toString();
            CodeBare = edtCodeBare.getText().toString();
            Designation = edtDesignation.getText().toString();
            PrixGros = edtPrixGros.getText().toString();
            CodeUnite = data_code_unite.get(spinUnite.getSelectedItemPosition());
            famille = data_code_famille.get(spinFamille.getSelectedItemPosition());
            sousfamille = data_code_sous_famille.get(spinFamille.getSelectedItemPosition());
            tauxtva= data_designation_tva.get(spinTVA.getSelectedItemPosition());

            if(prixachat.equals(""))
            {
                prixachat=prixvente;
            }
            if(valeurunite.equals(""))
            {
                valeurunite="1";
            }
            if(prixvente.equals(""))
            {
                prixvente="0";
            }
            if(PrixGros.equals(""))
            {
                PrixGros=prixvente;
            }
        }

        @Override
        protected void onPostExecute(String r) {

            //      Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();
//            Intent intent=new Intent(getApplicationContext(),AjoutArticle.class);
//            startActivity(intent);

if(testinsert) {
    AlertDialog.Builder alt = new AlertDialog.Builder(co);
    //    LayoutInflater li = LayoutInflater.from(co);
    //    View px = li.inflate(R.layout.diagprix, null);
    //    alt.setView(px);

    alt.setIcon(R.drawable.i2s);
    alt.setTitle("Article :  " + Designation);
    alt.setMessage("Voulez vous scanner le code à barre ");
    alt.setCancelable(false);

    alt.setPositiveButton("Scanner", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            scannerView = new ZXingScannerView(AjoutArticle.this);
            scannerView.setResultHandler(new ZXingScannerResultHandler());
            setContentView(scannerView);
            scannerView.startCamera();

        }
    });
    alt.setNegativeButton("NON", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            AlertDialog.Builder alt2 = new AlertDialog.Builder(co);
            //    LayoutInflater li = LayoutInflater.from(co);
            //    View px = li.inflate(R.layout.diagprix, null);
            //    alt.setView(px);

            alt2.setIcon(R.drawable.i2s);
            alt2.setTitle("Article :  ");
            alt2.setCancelable(false);
            alt2.setMessage("Article  " + Designation + " avec PrixVente TTC " + edtPrixVente.getText().toString() + " est ajouté");

            alt2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent intent = new Intent(getApplicationContext(), AjoutArticle.class);

                    startActivity(intent);
                }
            });
            AlertDialog d = alt2.create();
            d.show();
        }
    });
    AlertDialog d = alt.create();
    d.show();
}else{
    Toast.makeText(getApplicationContext(),"Erreur insertion",Toast.LENGTH_SHORT).show();
}

        }


        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {

            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    z = "Error in connection with SQL server";
                } else {





                    float prixventeHT=Float.parseFloat(prixvente)/(1+Float.parseFloat(tauxtva)/100);
                    float prixventeHTgros=Float.parseFloat(PrixGros)/(1+Float.parseFloat(tauxtva)/100);
                    float prixAchatHT=0;  float marge=0;
                    float margegros=0;
                    if(prixachat.equals(prixvente)){
                         prixAchatHT=0;
                          marge=0;

                    }else{
                         prixAchatHT=Float.parseFloat(prixachat)/(1+Float.parseFloat(tauxtva)/100);

                        marge=(prixventeHT-prixAchatHT)/prixAchatHT;

                    }
                    if(prixachat.equals(PrixGros)){
                          margegros=0;
                    }else{
                        margegros=(prixventeHTgros-prixAchatHT)/prixAchatHT;
                    }
                    if(Float.parseFloat(prixachat)==0||prixachat.equals(prixvente)){
                        marge=0;
                        margegros=0;
                        prixAchatHT=0;

                    }
                    marge=marge*100;
                    margegros=margegros*100;



                    String query_article="INSERT INTO Article \n" +
                            "           ( CodeArticle \n" +
                            "           , CodeFamille \n" +
                            "           , CodeSousFamille \n" +
                            "           , CodeType \n" +
                            "           , CodeMarque \n" +
                            "           , CodeUnite \n" +
                            "           , CodeUniteVente \n" +
                            "           , ValeurUniteVente \n" +
                            "           , Designation \n" +
                            "           , DernierPrixAchatHT \n" +
                            "           , DernierTauxRemise \n" +
                            "           , PrixAchatHT \n" +
                            "           , Marge \n" +
                            "           , PrixVenteHT \n" +
                            "           , CodeTVA \n" +
                            "           , PrixVenteTTC \n" +
                            "           , LiteTauxRemise \n" +
                            "           , Fodec \n" +
                            "           , Stockable \n" +
                            "           , Actif \n" +
                            "           , TailleCouleur \n" +
                            "           , Observation \n" +
                            "           , NumeroSerie \n" +
                            "           , CodeBarre \n" +
                            "           , ImageCodeBarre \n" +
                            "           , Logo \n" +
                            "           , PrixVenteNetHT \n" +
                            "           , PrixVenteTTCSansRemise \n" +
                            "           , PrixAchatTTCSansRemise \n" +
                            "           , NumeroRayon \n" +
                            "           , EnPromotion \n" +
                            "           , DateDebutPromotion \n" +
                            "           , DateFinPromotion \n" +
                            "           , NumeroPromotion \n" +
                            "           , NombrePoint \n" +
                            "           , Active \n" +
                            "           , CodeArticleDeduction \n" +
                            "           , QuantiteDeduite \n" +
                            "           , Compose " +
                            ",PrixVenteHTGros" +
                            ",PrixVenteTTCGros" +
                            ",MargeGros,UtilisateurCreateur)\n" +
                            "     VALUES\n" +
                        "  ( '"+codeTableArticle+"' \n" +
                            "           ,'"+famille+"' \n" +
                            "           ,'"+sousfamille+"'\n" +
                            "           , 'PF'\n" +
                            "           ,'MC' \n" +
                            "           ,'"+CodeUnite+"' \n" +
                            "           ,'"+CodeUnite+"'\n" +
                            "           ,  " +valeurunite+
                            "           , ? \n" +
                            "           , \n" +prixAchatHT+
                            "           , 0 \n" +
                            "           , \n" +prixAchatHT+
                            "           ,  " +marge+
                            "           ,  " +prixventeHT+
                            "           ,  \n" +CodeTVA+
                            "           ,   \n" +prixvente+
                            "           , 0 \n" +
                            "           , 0 \n" +
                            "           ,1 \n" +
                            "           , 1 \n" +
                            "           , 0 \n" +
                            "           ,'"+CodeArticle+"' \n" +
                            "           , 0 \n" +
                            "           , '"+codeTableArticle+"' \n" +
                            "           , null\n" +
                            "           , null \n" +
                            "           ,   \n" +prixventeHT+
                            "           ,   \n" +prixvente+
                            "           ,   \n" +prixachat+
                            "           , 'R001' \n" +
                            "           , 0 \n" +
                            "           , '' \n" +
                            "           , ''\n" +
                            "           ,  '' \n" +
                            "           , 0 \n" +
                            "           , 0 \n" +
                            "           ,  '' \n" +
                            "           , 0 \n" +
                            "           ,0 \n" +
                            "           , \n" +prixventeHTgros+
                            "           ,   \n" +PrixGros+
                            "           ,"+margegros+",? )";
//                            "           ( m?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,?)";

                    Log.e("queryinsert2", query_article);
                    Log.e("codeTableArticle","cod"+codeTableArticle);

                    PreparedStatement ps_article = con.prepareStatement(query_article);


                  ps_article.setString(1,Designation);
                  ps_article.setString(2,NomUtilisateur+"_a");

                    ps_article.executeUpdate();


                    Log.e("wra ", ""+ ps_article.getWarnings() );

//                    String query = "insert into ArticleAndroid (NumeroLigne,CodeBarre,Designation,CodeUnite,PrixAchatTTC," +
//                            "CodeTVA,PrixVenteTTC,Famille,DateCreation,PrixGros)\n" +
//                            "values(?,?,?,?,?,?,?,?,getdate(),?);";
//                    Log.e("queryinsert", query);
//
//                    PreparedStatement preparedStatement = con.prepareStatement(query);
//
//                    //&$code, &$codempl, &$conge, &$codempl, &$tel,
//                    //        &$datedu, &$dateau, &$nomuser, 0, &$droit,&$demijournee ,&$matin,&$apresmidi )
//                    preparedStatement.setString(1, CodeArticle);
//                    preparedStatement.setString(2, CodeBare);
//                    preparedStatement.setString(3, Designation);
//                    preparedStatement.setString(4, CodeUnite);
//                    preparedStatement.setString(5, prixachat);
//                    preparedStatement.setString(6, CodeTVA);
//                    preparedStatement.setString(7, prixvente);
//                    preparedStatement.setString(8, famille);
//                    preparedStatement.setString(9, PrixGros);
//
//                    preparedStatement.executeUpdate();


                    testinsert=true;






                    z = "Article Ajouté ";
                }
            } catch (SQLException ex) {
                z = "ERREUR ";
                Log.e("erreur ", ex.toString());
                testinsert=false;

            } catch (Exception ex) {
                z = "ERREUR ";
                Log.e("erreur ", ex.toString());
                testinsert=false;
            }
            return z;
        }
    }




    public class UpdateScan extends AsyncTask<String, String, String> {


        Boolean isSuccess = false;
        String z = "";
        String c, pre = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), r , Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alt = new AlertDialog.Builder(co);
            //    LayoutInflater li = LayoutInflater.from(co);
            //    View px = li.inflate(R.layout.diagprix, null);
            //    alt.setView(px);

            alt.setIcon(R.drawable.i2s);
            alt.setTitle("Article :  " );
            alt.setCancelable(false);
            alt.setMessage("Article  "+Designation+ " avec PrixVente TTC "+edtPrixVente.getText().toString()+" est ajouté");

            alt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(getApplicationContext(),AjoutArticle.class);

                    startActivity(intent);
                }
            });
            AlertDialog d = alt.create();
            d.show();

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




                        String query2 =         " update Article set CodeArticle='"+resultscan+"' ,CodeBarre='" + resultscan + "'" +
                                " where CodeArticle='"+codeTableArticle+"'" ;
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
    public class DeleteArticle extends AsyncTask<String, String, String> {


        Boolean isSuccess = false;
        String z = "";
        String c, pre = "";

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), r , Toast.LENGTH_SHORT).show();

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




                        String query2 = "delete Article  " +
                                " where CodeArticle='"+codeTableArticle+"'";
                        Log.e("q",query2);
                        PreparedStatement preparedStatement = con.prepareStatement(query2);


                        preparedStatement.executeUpdate();







                    }
                } catch (SQLException ex) {
                    isSuccess = false;
                    z = "echec import ancien compteur" + ex.toString();
                }
            }
            return z;
        }
    }
    public class TestArticleBarre extends AsyncTask<String, String, String> {



        String z = "";
        String c, des = "" ;

        @Override
        protected void onPreExecute() {

            testExiste=false;
        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r +testExiste  , Toast.LENGTH_SHORT).show();


            if(testExiste)
            {
                Toast.makeText(getApplicationContext(),"Cet article est  déja ajouté ",Toast.LENGTH_LONG).show();

                DeleteArticle deleteArticle=new DeleteArticle();
                deleteArticle.execute();
                AlertDialog.Builder alt = new AlertDialog.Builder(co);
                alt.setIcon(R.drawable.i2s);
                alt.setTitle("Article :  " );
                alt.setMessage("Cet Code à barre est  déja ajouté  ");
                alt.setCancelable(false);
                alt.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent=new Intent(getApplicationContext(),AjoutArticle.class);

                        startActivity(intent);
                    }
                });
                AlertDialog d = alt.create();
                d.show();


            }else{


                UpdateScan updateScan=new UpdateScan();
                updateScan.execute("");

            }
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

                        String query = "Select * from Article  where CodeBarre='"+resultscan+"' ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {




                            testExiste = true;


                        }


                    }
                } catch (SQLException ex) {
                    //   testExiste = false;
                    z = "echec  " + ex.toString();
                }
            }
            return z;
        }
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
