package com.example.i2s_creation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.Result;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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


public class MenuHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    final Context co = this;
    GridView gridArtcile,gridRecherche;
    String querySearch = "",queryScan;
    EditText edtRecherche;
    TextView txtTotal, txtTotalDate, txtEtat, txtexiste;
    ConnectionClass connectionClass;
    String NomSociete="",NomUtilisateur="";
    String user, password, base, ip;

    Boolean testExiste = false,Actif=false;
    Button btFiltre;
    private ZXingScannerView scannerView;
    ProgressBar bar, progressBar,progressBar2,pbbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);



        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted


            ActivityCompat.requestPermissions(MenuHome.this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        edtRecherche = (EditText) findViewById(R.id.edt_recherche);
        txtTotal = (TextView) findViewById(R.id.txt_total);
        txtTotalDate = (TextView) findViewById(R.id.txt_total_date);
        txtEtat = (TextView) findViewById(R.id.txtetat);
        txtEtat.setText("Tout les Articles");
        connectionClass = new ConnectionClass();
        querySearch =         " select *,convert(numeric(18,3),Article.PrixAchatHT*(1+TauxTVA/100))  as Achat\n" +

                "     from\n" +
                "     Article\n" +
                "        inner join UniteArticle on UniteArticle.CodeUnite=Article.CodeUnite\n" +
                "         inner join TVA on TVA.CodeTVA=Article.CodeTVA  where Article.Actif=1\n" ;

///session sql
        SharedPreferences pref = getSharedPreferences("usersessionsql", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt = pref.edit();
        user = pref.getString("user", user);
        ip = pref.getString("ip", ip);
        password = pref.getString("password", password);
        base = pref.getString("base", base);
        //session user
        SharedPreferences prefuser = getSharedPreferences("usersession", Context.MODE_PRIVATE);
        SharedPreferences.Editor edt_user = pref.edit();
        NomSociete = prefuser.getString("NomSociete", NomSociete);
        NomUtilisateur = prefuser.getString("NomUtilisateur", NomUtilisateur);

        setTitle(NomSociete);
        connectionClass = new ConnectionClass();


        DoLogin doLogin=new DoLogin();
        doLogin.execute("");



        txtEtat.setText("Tout Les Articles ");
        txtEtat.setTextColor(Color.parseColor("#262626"));

        Button btAjout = (Button) findViewById(R.id.btajout);
        btAjout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), AjoutArticle.class);
                startActivity(intent);
            }
        });
        gridArtcile = (GridView) findViewById(R.id.grid_article);


        Button btRecherche = (Button) findViewById(R.id.bt_recherche);
        btRecherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEtat.setText("Liste des Articles Recherchés ");
                txtEtat.setTextColor(Color.parseColor("#0000b3"));
                String txt = edtRecherche.getText().toString();
                querySearch = " select* ,convert(numeric(18,3),Article.PrixAchatHT*(1+TauxTVA/100)) as Achat\n" +

                        "     from\n" +
                        "     Article\n" +
                        "        inner join UniteArticle on UniteArticle.CodeUnite=Article.CodeUnite\n" +
                        "         inner join TVA on TVA.CodeTVA=Article.CodeTVA \n" +
                        "        " +
                        "where Designation like'%" + txt + "%' or CodeBarre like '%"+txt+"%'" +
                        " or CodeArticle like'%"+txt+"%' and Article.Actif=1";

                FillList fillList = new FillList();
                fillList.execute("");
            }
        });

        FillList fillList = new FillList();
        fillList.execute("");
        Button btreload = (Button) findViewById(R.id.btreload);
        btreload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEtat.setText("Liste Totale des Articles ");
                txtEtat.setTextColor(Color.parseColor("#262626"));
                edtRecherche.setText("");
                querySearch =         " select *,convert(numeric(18,3),Article.PrixAchatHT*(1+TauxTVA/100))  as Achat\n" +
                        "    \n" +
                        "     from\n" +
                        "     Article\n" +
                        "        inner join UniteArticle on UniteArticle.CodeUnite=Article.CodeUnite\n" +
                        "         inner join TVA on TVA.CodeTVA=Article.CodeTVA where  Article.Actif=1 \n" ;

                FillList fillList = new FillList();
                fillList.execute("");
            }
        });
        btFiltre=(Button)findViewById(R.id.bt_filtre) ;
        btFiltre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEtat.setText("Liste des Articles sans Code Barre");
                txtEtat.setTextColor(Color.parseColor("#006600"));
                querySearch = "select *  " +
                        ",\n" +
                        "(select count(NumeroLigne)  from ArticleAndroid where   \n" +
                        "CONVERT(date, DateCreation,103) =CONVERT(date, getdate(),103) )as TotalDate,\n" +
                        "(select count(NumeroLigne)  from ArticleAndroid  )as Total " +
                        " ,convert(numeric(18,3),Article.PrixAchatHT*(1+TauxTVA/100)) as Achat\n" +
                        "    \n" +
                        "     from\n" +
                        "     Article\n" +

                        "         inner join TVA on TVA.CodeTVA=Article.CodeTVA \n" +
                        "       " +
                        "inner join UniteArticle on UniteArticle.CodeUnite=ArticleAndroid.CodeUnite" +
                        " where ArticleAndroid.CodeBarre =''" +
                        " and Article.Actif=1  order by DateCreation desc ";
                FillList fillList=new FillList();
                fillList.execute("");
            }
        });
       // TestArticle testArticle = new TestArticle();
        //testArticle.execute("");


        Button b = (Button) findViewById(R.id.btscan);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                scannerView = new ZXingScannerView(MenuHome.this);
                scannerView.setResultHandler(new ZXingScannerResultHandler());
                setContentView(scannerView);
                scannerView.startCamera();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_deconnecter) {
            SharedPreferences pref = getSharedPreferences("usersession", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putBoolean("etat", false);
            edt.commit();
            Intent inte = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(inte);
        } else
        if (id == R.id.nav_inventaire) {

            Intent inte = new Intent(getApplicationContext(), ChoixInventaire.class);
            startActivity(inte);
        } else
        if (id == R.id.nav_parametre) {
            LayoutInflater li = LayoutInflater.from(co);
            View px = li.inflate(R.layout.print, null);
            final AlertDialog.Builder alt = new AlertDialog.Builder(co);
            alt.setIcon(R.drawable.i2s);
            alt.setTitle("Paramètre");
            alt.setView(px);


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


                                        Toast.makeText(getApplicationContext(), "Vérifiez vos données ", Toast.LENGTH_LONG).show();
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

            d.show();}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer( GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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
            txtTotal.setText(Total);
            txtTotalDate.setText(TotalDate);
            progressBar.setVisibility(View.GONE);
            //CodeDemande ,CodeDemandeur ,Nom,CONVERT(date,DateCreation)AS DateCreation, Etat.Libelle AS EtatPiece ,NomUtilisateur
            String[] from = {"Designation", "PrixAchatTTC", "PrixVenteTTC", "Unite", "NumeroLigne", "CodeBarre","PrixGros"};
            int[] views = {R.id.txt_designation, R.id.txt_montant_achat, R.id.txt_montant_vente, R.id.txt_unite, R.id.txt_num, R.id.txt_code_barre,R.id.prixgros};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.item_list_article, from,
                    views);
            gridArtcile.setAdapter(ADA);
            gridArtcile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap) ADA.getItem(arg2);
                    AlertDialog.Builder alt = new AlertDialog.Builder(co);
                    //    LayoutInflater li = LayoutInflater.from(co);
                    //    View px = li.inflate(R.layout.diagprix, null);
                    //    alt.setView(px);
                    final String Designation = (String) obj.get("Designation");
                    final String NumeroLigne = (String) obj.get("NumeroLigne");
                    final String CodeBarre = (String) obj.get("CodeBarre");
                    alt.setIcon(R.drawable.i2s);
                    alt.setTitle("Article :  " + NumeroLigne);
                    alt.setMessage(Designation);

                    alt.setPositiveButton("Modifier(تغيير)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), ModificationArticle.class);
                            intent.putExtra("Designation", Designation);
                            intent.putExtra("NumeroLigne", NumeroLigne);
                            startActivity(intent);

                        }
                    });
                    alt.setNegativeButton("Consulter(تصفح )", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), ConsultationArticle.class);
                            intent.putExtra("Designation", Designation);
                            intent.putExtra("NumeroLigne", NumeroLigne);
                            startActivity(intent);
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
                    this.z = "Error in connection with SQL server";
                } else {


                    Log.e("query", querySearch);
                    ResultSet rs = con.prepareStatement(querySearch).executeQuery();
                    new ArrayList();
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<>();
                        datanum.put("Designation", rs.getString("Designation"));
                        datanum.put("PrixAchatTTC", rs.getString("Achat"));
                        datanum.put("PrixVenteTTC", rs.getString("PrixVenteTTC"));
                        datanum.put("PrixGros", rs.getString("PrixVenteTTCGros"));

                        datanum.put("Unite", rs.getString("Libelle"));
                        datanum.put("NumeroLigne", rs.getString("CodeArticle"));
                        datanum.put("CodeBarre", rs.getString("CodeBarre"));

                    //    datanum.put("Total", rs.getString("Total"));

                        Total ="0";
                     TotalDate ="0";
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

    public class TestArticle extends AsyncTask<String, String, String> {


        String z = "";
        String c, des = "";
        int lo=0;

        @Override
        protected void onPreExecute() {

            testExiste = false;
        }

        @Override
        protected void onPostExecute(String r) {
            // Toast.makeText(getApplicationContext(), r +testExiste  , Toast.LENGTH_SHORT).show();
            btFiltre.setText("Article sans Code barre ("+lo+")");

            if (testExiste) {

                Toast.makeText(getApplicationContext(), "Vous avez Des articles sans code à barre ", Toast.LENGTH_LONG).show();
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btFiltre.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_green) );
                } else {
                    btFiltre.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_green));
                }
                btFiltre.setClickable(true);

            } else {
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btFiltre.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_red) );
                } else {
                    btFiltre.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.circle_red));

                }
                btFiltre.setClickable(false);
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

                        String query = "Select * from ArticleAndroid  where  CodeBarre='' ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()) {
                            lo++;


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
    protected void onResume() {
        super.onResume();
        FillList fillList = new FillList();
        fillList.execute("");
       // TestArticle testArticle=new TestArticle();
       // testArticle.execute("");
        txtEtat.setText("Liste Totale des Articles ");
        txtEtat.setTextColor(Color.parseColor("#262626"));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            LayoutInflater li = LayoutInflater.from(co);
            View px = li.inflate(R.layout.print, null);
            final AlertDialog.Builder alt = new AlertDialog.Builder(co);
            alt.setIcon(R.drawable.i2s);
            alt.setTitle("Paramètre");
            alt.setView(px);


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


                                        Toast.makeText(getApplicationContext(), "Vérifiez vos données ", Toast.LENGTH_LONG).show();
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
        if (id == R.id.deconnecter) {

            SharedPreferences pref = getSharedPreferences("usersession", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt = pref.edit();
            edt.putBoolean("etat", false);
            edt.commit();
            Intent inte = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(inte);
            return true;


        }
        return super.onOptionsItemSelected(item);
    }



    public class DoLogin extends AsyncTask<String, String, String> {
        String z = "";
        Boolean isSuccess = false;


        String userid = NomUtilisateur;


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String r) {



            if (isSuccess) {
                if (Actif) {

                } else {


                    AlertDialog.Builder alt = new AlertDialog.Builder(co);
                    //    LayoutInflater li = LayoutInflater.from(co);
                    //    View px = li.inflate(R.layout.diagprix, null);
                    //    alt.setView(px);

                    alt.setIcon(R.drawable.i2s);
                    alt.setTitle("Erreur License    " );
                    alt.setMessage("Prière de contacter IDEAL SOFTWARE SOLUTION sur\nTel : 74 440 602 \nMail : sales@ideal2s.com) ");
                    alt.setCancelable(false);
                    alt.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences pref = getSharedPreferences("usersession", Context.MODE_PRIVATE);
                            SharedPreferences.Editor edt = pref.edit();
                            edt.putBoolean("etat", false);
                            edt.commit();
                            Intent inte = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(inte);



                        }
                    });
                    AlertDialog d = alt.create();
                    d.show();
                }

            }}

        @Override
        protected String doInBackground(String... params) {
            if (false)
                z = "Please enter User Id and Password";
            else {
                try {
                    Connection con = connectionClass.CONN(ip, password, user, base);
                    if (con == null) {
                        z = "Error in connection with SQL server";
                    } else {
                        String query = "SELECT *,(select RaisonSociale from Societe)as NomSociete from Utilisateur  " +
                                " where Utilisateur.NomUtilisateur='" + userid + "'  ";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(query);


                        if (rs.next()) {
                            boolean TEST = false;

                            NomUtilisateur = rs.getString("NomUtilisateur");
                            NomSociete = rs.getString("NomSociete");
                            Actif = rs.getBoolean("Actif");

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

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler {


        @Override
        public void handleResult(Result result) {
            String resultCode = result.toString();
            Toast.makeText(MenuHome.this, resultCode, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alt = new AlertDialog.Builder(co);
            LayoutInflater li = LayoutInflater.from(co);
            View px = li.inflate(R.layout.diag_recherche, null);
            alt.setView(px);
            gridRecherche = (GridView) px.findViewById(R.id.grid);
            pbbar = (ProgressBar) px.findViewById(R.id.pb);
            txtexiste = (TextView) px.findViewById(R.id.txtexiste);
            queryScan = "  select *,convert(numeric(18,3),Article.PrixAchatHT*(1+TauxTVA/100))  as Achat\n" +
                    "    \n" +
                    "     from\n" +
                    "     Article\n" +
                    "        inner join UniteArticle on UniteArticle.CodeUnite=Article.CodeUnite\n" +
                    "         inner join TVA on TVA.CodeTVA=Article.CodeTVA \n" +

                    "   where   CodeBarre= '" + resultCode + "' and Article.Actif=1 ";
            FillListRecherche fillListRecherche = new FillListRecherche();
            fillListRecherche.execute("");
           // alt.setIcon(R.drawable.i2s);
            alt.setTitle("Scan :  " + resultCode);


            alt.setPositiveButton("fermer", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getApplicationContext(), MenuHome.class);
                    startActivity(intent);

                }
            });

            AlertDialog d = alt.create();
            d.show();


            scannerView.stopCamera();


        }
    }

    public class FillListRecherche extends AsyncTask<String, String, String> {
        List<Map<String, String>> prolist = new ArrayList();

        String z = "", Total = "", TotalDate = "";


        /* access modifiers changed from: protected */
        public void onPreExecute() {
        pbbar.setVisibility(View.VISIBLE);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String r) {

          pbbar.setVisibility(View.GONE);

            txtexiste.setText(r);


            //CodeDemande ,CodeDemandeur ,Nom,CONVERT(date,DateCreation)AS DateCreation, Etat.Libelle AS EtatPiece ,NomUtilisateur
            String[] from = {"Designation", "PrixAchatTTC", "PrixVenteTTC", "Unite", "NumeroLigne", "CodeBarre","PrixGros"};
            int[] views = {R.id.txt_designation, R.id.txt_montant_achat, R.id.txt_montant_vente, R.id.txt_unite, R.id.txt_num, R.id.txt_code_barre,R.id.prixgros};
            final SimpleAdapter ADA = new SimpleAdapter(getApplicationContext(),
                    prolist, R.layout.item_list_article, from,
                    views);
            gridRecherche.setAdapter(ADA);
            gridRecherche.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View arg1, int arg2, long arg3) {
                    HashMap<String, Object> obj = (HashMap) ADA.getItem(arg2);
                    AlertDialog.Builder alt = new AlertDialog.Builder(co);
                    //    LayoutInflater li = LayoutInflater.from(co);
                    //    View px = li.inflate(R.layout.diagprix, null);
                    //    alt.setView(px);
                    final String Designation = (String) obj.get("Designation");
                    final String NumeroLigne = (String) obj.get("NumeroLigne");
                    final String CodeBarre = (String) obj.get("CodeBarre");
                    alt.setIcon(R.drawable.i2s);
                    alt.setTitle("Article : " +NumeroLigne);
                    alt.setMessage(Designation);

                    alt.setPositiveButton("Modifier(تغيير)", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), ModificationArticle.class);
                            intent.putExtra("Designation", Designation);
                            intent.putExtra("NumeroLigne", NumeroLigne);
                            startActivity(intent);

                        }
                    });
                    alt.setNegativeButton("Consulter(تصفح )", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), ConsultationArticle.class);
                            intent.putExtra("Designation", Designation);
                            intent.putExtra("NumeroLigne", NumeroLigne);
                            startActivity(intent);
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


                    Log.e("query", queryScan);
                    ResultSet rs = con.prepareStatement(queryScan).executeQuery();
                    new ArrayList();
                    z="l'article n'existe pas";
                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<>();
                        datanum.put("Designation", rs.getString("Designation"));
                        datanum.put("PrixAchatTTC", rs.getString("Achat"));
                        datanum.put("PrixVenteTTC", rs.getString("PrixVenteTTC"));
                        datanum.put("PrixGros", rs.getString("PrixVenteTTCGros"));

                        datanum.put("Unite", rs.getString("Libelle"));
                        datanum.put("NumeroLigne", rs.getString("CodeArticle"));
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
