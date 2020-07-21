package com.example.i2s_creation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ModificationArticle extends AppCompatActivity {
    String querySearch = "";
    EditText edtRecherche;
    ConnectionClass connectionClass;
    String CodeRepresentant;
    String user, password, base, ip;
    Spinner spinTVA, spinUnite,spinFamille;
    EditText edtDesignation, edtPrixAchat, edtPrixVente,edtValeurUnite;
    ArrayAdapter<CharSequence> adapterTVA, adapterUnite,adapterFamille;
    ArrayList<String> data_code_unite = new ArrayList<String>();
    ArrayList<String> data_code_tva = new ArrayList<String>();
    ArrayList<String> data_designation_unite = new ArrayList<String>();
    ArrayList<String> data_designation_tva = new ArrayList<String>();
    ArrayList<String> data_code_famille = new ArrayList<String>();
    ArrayList<String> data_code_sous_famille = new ArrayList<String>();
    ArrayList<String> data_designation_famille = new ArrayList<String>();
    private ZXingScannerView scannerView;
    String CodeArticle = "", resultscan = "", Designation = "";
    Button btValider;
    TextView txtNumeroLigne,  txtdate;
    EditText edtCodeBare,edtPrixGros;
    final Context co = this;
    Boolean testExiste = false;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modification_article);


        connectionClass = new ConnectionClass();
        Intent intent = getIntent();
        String d = intent.getStringExtra("Designation");
        String NumeroLigne = intent.getStringExtra("NumeroLigne");
        progressBar=(ProgressBar)findViewById(R.id.progressBar) ;

        querySearch = " select *,isnull(FamilleArticle.Libelle,'') as familleArt, \n" +
                "isnull( convert(numeric(18,2),Article.ValeurUniteVente),1) as UniteValeur" +
                "   ,convert(numeric(18,3),Article.PrixAchatHT*(1+TauxTVA/100))  as Achat from\n" +
                " Article\n" +
                "    inner join UniteArticle on UniteArticle.CodeUnite=Article.CodeUnite\n" +
                "     inner join TVA on TVA.CodeTVA=Article.CodeTVA \n" +
                "  \n" +
                "     left  join FamilleArticle  on FamilleArticle.CodeFamille =Article.CodeFamille where CodeArticle ='" + NumeroLigne + "'\n";

        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        user = pref.getString("user", user);
        ip = pref.getString("ip", ip);
        password = pref.getString("password", password);
        base = pref.getString("base", base);
        connectionClass = new ConnectionClass();
        txtNumeroLigne = (TextView) findViewById(R.id.txt_num);
        spinTVA = (Spinner) findViewById(R.id.spin_tva);
        spinUnite = (Spinner) findViewById(R.id.spin_unite);
        spinFamille=(Spinner)findViewById(R.id.spinfamille);
        edtCodeBare = (EditText) findViewById(R.id.edt_code_barre);
        edtDesignation = (EditText) findViewById(R.id.edt_designation);
        edtPrixAchat = (EditText) findViewById(R.id.est_prix_achat);
        edtPrixVente = (EditText) findViewById(R.id.edt_prixvente);
        edtValeurUnite = (EditText) findViewById(R.id.edt_valeur_unite);

        edtPrixGros = (EditText) findViewById(R.id.edt_prixgros);
        txtdate = (TextView) findViewById(R.id.txt_date);
        /////////////////////////////////////////////////////////////

        String query_famille = "select FamilleArticle.CodeFamille,FamilleArticle.Libelle,CodeSousFamille from FamilleArticle \n" +
                "inner join SousFamilleArticle on SousFamilleArticle.CodeFamille=FamilleArticle.CodeFamille";
        try {
            Connection connect = connectionClass.CONN(ip, password, user, base);
            PreparedStatement stmt;
            stmt = connect.prepareStatement(query_famille);
            ResultSet rsss = stmt.executeQuery();
            data_designation_famille.add("");
            data_code_famille.add("");
            data_code_sous_famille.add("");
            while (rsss.next()) {
                String id = rsss.getString("CodeFamille");
                String libelle = rsss.getString("Libelle");
                String CodeSousFamille = rsss.getString("CodeSousFamille");
                data_designation_famille.add(libelle);
                data_code_famille.add(id);
                data_code_sous_famille.add(CodeSousFamille);
            }


        adapterFamille = new ArrayAdapter(getApplicationContext(),
                    R.layout.spinner, data_designation_famille);
            adapterFamille.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinFamille.setAdapter(adapterFamille);
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

        String query_rp = "select * from UniteArticle";

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


            adapterUnite = new ArrayAdapter(getApplicationContext(),
                    R.layout.spinner, data_designation_unite);
            adapterUnite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinUnite.setAdapter(adapterUnite);
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


            adapterTVA = new ArrayAdapter(getApplicationContext(),
                    R.layout.spinner, data_designation_tva);
            adapterTVA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinTVA.setAdapter(adapterTVA);
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
        btValider = (Button) findViewById(R.id.btvalider);
        btValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtDesignation.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "La désignation est Obligatoire", Toast.LENGTH_LONG).show();

                } else  if(edtPrixVente.getText().toString().equals("")||edtPrixVente.getText().toString().equals("0")){

                    Toast.makeText(getApplicationContext(), "Le prix de vente  est Obligatoire", Toast.LENGTH_LONG).show();
                }else  {

                    TestArticle testArticle=new TestArticle();
                    testArticle.execute("");

                }
            }
        });
        FillList fillList = new FillList();
        fillList.execute("");
        Button btAnnuler = (Button) findViewById(R.id.btretour);
        btAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), MenuHome.class);
                startActivity(intent);

            }
        });
        Button b = (Button) findViewById(R.id.btscan);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scannerView = new ZXingScannerView(ModificationArticle.this);
                scannerView.setResultHandler(new ZXingScannerResultHandler());
                setContentView(scannerView);
                scannerView.startCamera();

            }
        });


    }


    public class FillList extends AsyncTask<String, String, String> {
        List<Map<String, String>> prolist = new ArrayList();
        String z = "", CodeUnite = "",UniteValeur="";
        String CodeTVA = "", famille = "", prixvente = "", prixachat = "", CodeBare = "",PrixGros="", Designation, NumeroLigne, DateCreation = "";


        public FillList() {
        }

        /* access modifiers changed from: protected */
        public void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String r) {
            progressBar.setVisibility(View.GONE);
            edtCodeBare.setText(CodeBare);
            edtDesignation.setText(Designation);
           // edtFamille.setText(famille);
            edtPrixAchat.setText(prixachat);
            edtPrixVente.setText(prixvente);
            edtValeurUnite.setText(UniteValeur);
            int spinnerPosition = adapterTVA.getPosition(CodeTVA);
            spinTVA.setSelection(spinnerPosition);


            int spinnerPositionFamille = adapterFamille.getPosition(famille);
            spinFamille.setSelection(spinnerPositionFamille);

            txtNumeroLigne.setText(NumeroLigne);
            txtdate.setText(DateCreation);
            edtPrixGros.setText(PrixGros);
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            try {
                Connection con = connectionClass.CONN(ip, password, user, base);
                if (con == null) {
                    this.z = "Error in connection with SQL server";
                } else {


                    Log.e("query", querySearch);
                    ResultSet rs = con.prepareStatement(querySearch).executeQuery();
                    new ArrayList();
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<>();
                        Designation = rs.getString("Designation");
                        prixachat = rs.getString("Achat");
                        prixvente = rs.getString("PrixVenteTTC");

                        CodeUnite = rs.getString("Libelle");
                        CodeTVA = rs.getString("TauxTVA");
                        famille = rs.getString("familleArt");
                        NumeroLigne = rs.getString("CodeArticle");
                        DateCreation ="";
                        CodeBare = rs.getString("CodeBarre");
                        PrixGros = rs.getString("PrixVenteTTCGros");
                        UniteValeur = rs.getString("ValeurUniteVente");


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

    public class InsertDemande extends AsyncTask<String, String, String> {
        String z = "", CodeUnite = "",ValeurUnite="";
        String CodeTVA = "", famille = "", prixvente = "", prixachat = "", CodeBare = "", Designation, Num,PrixGros="",tauxtva="",sosufamille="";
        boolean testinsert=false;
        @Override
        protected void onPreExecute() {

            CodeTVA = data_code_tva.get(spinTVA.getSelectedItemPosition());
            //    famille = edtFamille.getText().toString();
            famille = data_code_famille.get(spinFamille.getSelectedItemPosition());
            sosufamille = data_code_sous_famille.get(spinFamille.getSelectedItemPosition());
            prixvente = edtPrixVente.getText().toString();
            prixachat = edtPrixAchat.getText().toString();
            CodeBare = edtCodeBare.getText().toString();
            Designation = edtDesignation.getText().toString();
            Num = txtNumeroLigne.getText().toString();
            PrixGros = edtPrixGros.getText().toString();
            ValeurUnite = edtValeurUnite.getText().toString();
            CodeUnite = data_code_unite.get(spinUnite.getSelectedItemPosition());
            tauxtva= data_designation_tva.get(spinTVA.getSelectedItemPosition());
            if (prixachat.equals("")) {
                prixachat = "0";
            }

            if (prixvente.equals("")) {
                prixvente = "0";
            }
            if (PrixGros.equals("")) {
                PrixGros = "0";
            }
        }

        @Override
        protected void onPostExecute(String r) {
            if(testinsert) {
                AlertDialog.Builder alt2 = new AlertDialog.Builder(co);
                //    LayoutInflater li = LayoutInflater.from(co);
                //    View px = li.inflate(R.layout.diagprix, null);
                //    alt.setView(px);

                alt2.setIcon(R.drawable.i2s);
                alt2.setTitle("Article :  ");
                alt2.setCancelable(false);
                alt2.setMessage("Article  " + Designation + " avec PrixVente TTC " + edtPrixVente.getText().toString() + " est modifié");

                alt2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                        Intent intent = new Intent(getApplicationContext(), MenuHome.class);

                        startActivity(intent);
                    }
                });
                AlertDialog d = alt2.create();
                d.show();
            }else{
                Toast.makeText(getApplicationContext(),"Erreur modification",Toast.LENGTH_SHORT).show();
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



                    String query_q="update Article set CodeBarre=? ,Designation=?,CodeUnite=?,CodeUniteVente=?,PrixAchatHT=?," +
                            " Marge=? , PrixVenteHT=?, CodeTVA=?, PrixVenteTTC=?,PrixVenteNetHT=?,PrixVenteTTCSansRemise=?" +
                            ",PrixAchatTTCSansRemise=?, PrixVenteHTGros=? ,PrixVenteTTCGros=? ,MargeGros=?,CodeFamille=?," +
                            "CodeSousFamille=? ,ValeurUniteVente=?  where " +
                            " CodeArticle=?";
                    PreparedStatement ps= con.prepareStatement(query_q);
                    ps.setString(1,CodeBare);
                    ps.setString(2,Designation);
                    ps.setString(3,CodeUnite);
                    ps.setString(4,CodeUnite);
                    ps.setString(5,""+prixAchatHT);
                    ps.setString(6,""+marge);
                    ps.setString(7,""+prixventeHT);
                    ps.setString(8,CodeTVA);
                    ps.setString(9,prixvente);
                    ps.setString(10,prixventeHT+"");
                    ps.setString(11,prixvente);
                    ps.setString(12,prixachat);
                    ps.setString(13,prixventeHTgros+"");
                    ps.setString(14,PrixGros);
                    ps.setString(15,margegros+"");
                    ps.setString(16,famille);
                    ps.setString(17,sosufamille);
                    ps.setString(18, ValeurUnite);
                    ps.setString(19, Num);
                    ps.executeUpdate();
                    z = "Article Modifié";

//
//                    String query = "update  ArticleAndroid  set " +
//
//                            "CodeBarre=?," +
//                            "Designation=?," +
//                            "CodeUnite=?," +
//                            "PrixAchatTTC=?," +
//                            "CodeTVA=?,PrixVenteTTC=?,Famille=?,PrixGros=? where NumeroLigne=?";
//                    Log.e("queryinsert", query);
//
//                    PreparedStatement preparedStatement = con.prepareStatement(query);
//
//                    //&$code, &$codempl, &$conge, &$codempl, &$tel,
//                    //        &$datedu, &$dateau, &$nomuser, 0, &$droit,&$demijournee ,&$matin,&$apresmidi )
//
//                    preparedStatement.setString(1, CodeBare);
//                    preparedStatement.setString(2, Designation);
//                    preparedStatement.setString(3, CodeUnite);
//                    preparedStatement.setString(4, prixachat);
//                    preparedStatement.setString(5, CodeTVA);
//                    preparedStatement.setString(6, prixvente);
//                    preparedStatement.setString(7, famille);
//
//                    preparedStatement.setString(8,PrixGros);
//                    preparedStatement.setString(9, Num);
//                    preparedStatement.executeUpdate();
                    testinsert=true;

                }
            } catch (SQLException ex) {
                z = "erreur";
                Log.e("erreur update ", ex.toString());
                testinsert=false;

            }
            return z;
        }
    }

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {


        @Override
        public void handleResult(com.google.zxing.Result result) {
            String resultCode = result.toString();
            Toast.makeText(ModificationArticle.this, resultCode, Toast.LENGTH_SHORT).show();


            // setContentView(R.layout.activity_ajout_article);

            Log.e("codelu", resultCode);
            resultscan = resultCode;
            Toast.makeText(getApplicationContext(), resultCode, Toast.LENGTH_LONG).show();


            scannerView.stopCamera();
            TestArticleBarre testArticle = new TestArticleBarre();
            testArticle.execute("");


        }
    }

    public class UpdateScan extends AsyncTask<String, String, String> {


        Boolean isSuccess = false;
        String z = "";
        String c, pre = "",Num="";

        @Override
        protected void onPreExecute() {
            Designation = edtDesignation.getText().toString();
            Num = txtNumeroLigne.getText().toString();
        }

        @Override
        protected void onPostExecute(String r) {
            Toast.makeText(getApplicationContext(), r, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alt = new AlertDialog.Builder(co);
            //    LayoutInflater li = LayoutInflater.from(co);
            //    View px = li.inflate(R.layout.diagprix, null);
            //    alt.setView(px);

            alt.setIcon(R.drawable.i2s);
            alt.setTitle("Article :  ");
            alt.setCancelable(false);
            alt.setMessage("Article  " + Designation + " avec PrixVente TTC " + edtPrixVente.getText().toString() + " est modifié");

            alt.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), ModificationArticle.class);
                    intent.putExtra("Designation", Designation);
                    intent.putExtra("NumeroLigne", Num);

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


                        String query2 =
                                "update Article  set CodeBarre='" + resultscan + "'" +
                                "where CodeArticle='"+Num+"'";

                        Log.e("q", query2);
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
        String c, des = "" ,NumeroLigne="";

        @Override
        protected void onPreExecute() {

            testExiste=false;
            Designation=edtDesignation.getText().toString();
            NumeroLigne=txtNumeroLigne.getText().toString();
        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r +testExiste  , Toast.LENGTH_SHORT).show();


            if(testExiste)
            {


                AlertDialog.Builder alt = new AlertDialog.Builder(co);
                //    LayoutInflater li = LayoutInflater.from(co);
                //    View px = li.inflate(R.layout.diagprix, null);
                //    alt.setView(px);

                alt.setIcon(R.drawable.i2s);
                alt.setTitle("Article :  " );
                alt.setMessage("Cet Code à barre est  déja ajouté \n Appuier sur SCAN si vous voulez entrer un nouveau code barre ");
                alt.setCancelable(false);
                alt.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent=new Intent(getApplicationContext(),ModificationArticle.class);
                        intent.putExtra("Designation",Designation);
                        intent.putExtra("NumeroLigne",NumeroLigne);
                        startActivity(intent);

                    }
                });
                AlertDialog d = alt.create();
                d.show();


            }else{

                AlertDialog.Builder alt = new AlertDialog.Builder(co);
                //    LayoutInflater li = LayoutInflater.from(co);
                //    View px = li.inflate(R.layout.diagprix, null);
                //    alt.setView(px);

                alt.setIcon(R.drawable.i2s);
                alt.setTitle("Scan :  " );
                alt.setMessage("Voulez vous enregistrer Cet Code à barre  ");
                alt.setCancelable(false);
                alt.setPositiveButton("Enregistrer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UpdateScan updateScan=new UpdateScan();
                        updateScan.execute("");


                    }
                });
                alt.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent=new Intent(getApplicationContext(),ModificationArticle.class);
                        intent.putExtra("NumeroLigne",NumeroLigne);
                        startActivity(intent);
                    }
                });
                AlertDialog d = alt.create();
                d.show();

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
    public class TestArticleBarreSaisi extends AsyncTask<String, String, String> {



        String z = "";
        String c, des = "",Code="",NumeroLigne=""
                ;

        @Override
        protected void onPreExecute() {

            testExiste=false;

            Code=edtCodeBare.getText().toString();
            NumeroLigne=txtNumeroLigne.getText().toString();
        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r +testExiste  , Toast.LENGTH_SHORT).show();


            if(testExiste)
            {
                Toast.makeText(getApplicationContext(),"Cet article est  déja ajouté ",Toast.LENGTH_LONG).show();


                AlertDialog.Builder alt = new AlertDialog.Builder(co);
                //    LayoutInflater li = LayoutInflater.from(co);
                //    View px = li.inflate(R.layout.diagprix, null);
                //    alt.setView(px);

                alt.setIcon(R.drawable.i2s);
                alt.setTitle("Article :  " );
                alt.setMessage("Cet Code à barre est  déja ajouté  ");
                alt.setCancelable(false);
                alt.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                    }
                });
                AlertDialog d = alt.create();
                d.show();


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

                        String query = "Select * from Article  where CodeBarre='"+Code+"'" +
                                " and CodeArticle!='"+NumeroLigne+"' and CodeBarre!=''";
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
    public class TestArticle extends AsyncTask<String, String, String> {



        String z = "";
        String c, des = "",NumeroLigne="" ;

        @Override
        protected void onPreExecute() {
            des=edtDesignation.getText().toString();
            testExiste=false;
            NumeroLigne=txtNumeroLigne.getText().toString();
        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r +testExiste  , Toast.LENGTH_SHORT).show();


            if(testExiste)
            {
                Toast.makeText(getApplicationContext(),"Cette designation de l' article est  déja ajoutée ",Toast.LENGTH_LONG).show();

            }else{
                TestArticleBarreSaisi testArticleBarreSaisi=new TestArticleBarreSaisi();
                testArticleBarreSaisi.execute("");


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

                        String query = "Select * from Article where Designation='"+des+"'  and CodeArticle!='"+NumeroLigne+"'";

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
