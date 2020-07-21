package com.example.i2s_creation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ConsultationArticle extends AppCompatActivity {

    String querySearch = "";
    ConnectionClass connectionClass;
    String user, password, base, ip;
    TextView spinTVA,spinUnite;
    TextView edtDesignation,edtCodeBare,edtPrixAchat,edtPrixVente,edtFamille,txtdate;
    TextView txtNumeroLigne,txtPrixGros,txtValeurUnite;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation_article);
progressBar=(ProgressBar)findViewById(R.id.progressBar) ;

        connectionClass = new ConnectionClass();
        Intent intent=getIntent();
        String d=intent.getStringExtra("Designation");
        String NumeroLigne=intent.getStringExtra("NumeroLigne");



        querySearch = " select *,isnull(FamilleArticle.Libelle,'') as familleArt, \n" +
                "isnull( convert(numeric(18,2),Article.ValeurUniteVente),1) as UniteValeur " +
                "    ,convert(numeric(18,3),Article.PrixAchatHT*(1+TauxTVA/100)) as Achat from\n" +
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
        txtNumeroLigne=(TextView)findViewById(R.id.txt_num);
        spinTVA=(TextView)findViewById(R.id.spin_tva);
        spinUnite=(TextView)findViewById(R.id.spin_unite);
        edtCodeBare=(TextView)findViewById(R.id.edt_code_barre);
        edtDesignation=(TextView)findViewById(R.id.edt_designation);
        edtPrixAchat=(TextView)findViewById(R.id.est_prix_achat);
        edtPrixVente=(TextView)findViewById(R.id.edt_prixvente);
        edtFamille=(TextView)findViewById(R.id.edt_famille);
        txtdate=(TextView)findViewById(R.id.txt_date);
        txtPrixGros=(TextView)findViewById(R.id.edt_prixgros);
        txtValeurUnite=(TextView)findViewById(R.id.txt_valeur_unite);







        FillList fillList =new FillList();
        fillList.execute("");
        Button btAnnuler =(Button)findViewById(R.id.btretour);
        btAnnuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent=new Intent(getApplicationContext(),MenuHome.class);
                startActivity(intent);

            }
        });

    }


    public class FillList extends AsyncTask<String, String, String> {
        List<Map<String, String>> prolist = new ArrayList();
        String z = "", CodeUnite = "",PrixGros="",UniteValeur="";
        String CodeTVA = "", famille = "", prixvente = "", prixachat = "", CodeBare = "", Designation,NumeroLigne,datecreation="";


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
            edtFamille.setText(famille);
            edtPrixAchat.setText(prixachat);
            edtPrixVente.setText(prixvente);
            spinUnite.setText(CodeUnite);

            spinTVA.setText(CodeTVA);
            txtNumeroLigne.setText(NumeroLigne);
            txtdate.setText(datecreation);
            txtPrixGros.setText(PrixGros);
            txtValeurUnite.setText(UniteValeur);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
