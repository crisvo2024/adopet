package com.example.santiago.adopet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MascDet extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG="masc";
    private ImageView Photo;
    private TextView Name,Ciudad,Direccion, Phone,Age;
    private CheckBox Si,No;
    private String Key, phone;
    private DatabaseReference ref;
    private ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masc_det);

        Key=getIntent().getStringExtra(TAG);
        if(Key==null){
            throw new IllegalArgumentException("Debe pasar EXTRA_ACT_KEY");
        }

        ref=FirebaseDatabase.getInstance().getReference().child("Mascotas").child(Key);


        Photo=findViewById(R.id.Photo);
        Name=findViewById(R.id.name);
        Ciudad=findViewById(R.id.Ciudad);
        Direccion=findViewById(R.id.Direccion);
        Phone =findViewById(R.id.Phone);
        Si=findViewById(R.id.si);
        No=findViewById(R.id.no);
        Age=findViewById(R.id.edad);
        findViewById(R.id.telefono).setOnClickListener(this);
        findViewById(R.id.whatsapp).setOnClickListener(this);
        findViewById(R.id.apadrinar).setOnClickListener(this);
        findViewById(R.id.adoptar).setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Mascota M=dataSnapshot.getValue(Mascota.class);
                assert M != null;
                if(M.Photo!=null){
                    Context context=Photo.getContext();
                    Glide.with(context)
                            .load(M.Photo)
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_account_circle_black_380dp)
                                    .dontAnimate()
                                    .fitCenter()
                                    .circleCrop())
                            .into(Photo);
                }
                Name.setText(M.Name);
                Age.setText(M.Age+" Años");
                Ciudad.setText("Ciudad: "+M.Ciudad);
                Direccion.setText("Dirección: "+M.Direccion);
                Phone.setText("Telefono:    "+String.valueOf(M.Phone));
                phone=M.Phone;
                if (M.Vaccine)Si.setChecked(true);
                else No.setChecked(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Snackbar.make(Phone.getRootView(),"No se pudo cargar mascota",Snackbar.LENGTH_SHORT).show();
            }
        };
        ref.addValueEventListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener!=null){
            ref.removeEventListener(listener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.telefono){
            Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone));
            startActivity(intent);
        }
        if (v.getId()==R.id.whatsapp){
            Uri wha=Uri.parse("https://wa.me/"+phone);
            Intent intent=new Intent(Intent.ACTION_VIEW,wha);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        if(v.getId()==R.id.apadrinar){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("El dinero que done sera usado para el cuidado de este animal");
            AlertDialog dialog= builder.create();
            dialog.show();

        }
        if (v.getId()==R.id.adoptar){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Para continuar comuniquese al whatsapp al que sera redirigido en donde se le entrevistará para comprovar que cumple con los requisitos ")
                    .setTitle("¿Acepta las condiciones?")
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri wha=Uri.parse("https://wa.me/"+phone);
                            Intent intent=new Intent(Intent.ACTION_VIEW,wha);
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton("Declinar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog dialog= builder.create();
            dialog.show();
        }
    }
}
