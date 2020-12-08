package com.example.santiago.adopet;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class NewPet extends AppCompatActivity {
    final int REQUEST_IMAGE_GET = 1;
    final int REQUEST_IMAGE_CAPTURE=2;
    private static final String TAG = "Registro";
    private ImageView Photo;
    private RadioGroup Type,Sex;
    private CheckBox Vaccine;
    private TextInputEditText Name,Direccion,Ciudad,Raza;
    private EditText Telefono,Age;
    private Uri PhotoUri,download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_pet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Photo=findViewById(R.id.foto);
        Photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent,REQUEST_IMAGE_GET);
                }

            }
        });
        Type=findViewById(R.id.animal);
        Sex=findViewById(R.id.sexo);
        Vaccine=findViewById(R.id.vaccine);
        Name=findViewById(R.id.name);
        Direccion=findViewById(R.id.direccion);
        Ciudad=findViewById(R.id.ciudad);
        Raza=findViewById(R.id.raza);
        Telefono=findViewById(R.id.telefono);
        Age=findViewById(R.id.age);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crear();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            PhotoUri = data.getData();
            Context context=Photo.getContext();
            Glide.with(context)
                    .load(PhotoUri)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.ic_account_circle_black_380dp)
                            .dontAnimate()
                            .fitCenter()
                            .circleCrop())
                    .into(Photo);
        }

    }
    private void crear(){
        if(!validateForm())return;
        final String sex,tipo;
        if(Sex.getCheckedRadioButtonId()==R.id.macho){
            sex="Macho";
        }else{
            sex="Hembra";
        }
        if(Type.getCheckedRadioButtonId()==R.id.perro){
            tipo="Dog";
        }else {
            tipo="Cat";
        }
        FirebaseStorage storage =FirebaseStorage.getInstance();
        StorageReference reference=storage.getReference();
        final StorageReference photoref=reference.child("Photos/"+PhotoUri.getLastPathSegment());
        photoref.putFile(PhotoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                photoref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        download=uri;
                        DatabaseReference db=FirebaseDatabase.getInstance().getReference();
                        String Key=db.child("Mascotas").push().getKey();
                        Mascota mascota=new Mascota(Name.getText().toString(),sex,tipo,Integer.parseInt(Age.getText().toString()),Vaccine.isChecked(),download.toString(),Raza.getText().toString(),Direccion.getText().toString(),Ciudad.getText().toString(),Telefono.getText().toString());
                        Map<String,Object>Masc=mascota.toMap();
                        Map<String,Object>CUpdate=new HashMap<>();
                        CUpdate.put("Mascotas/"+Key,Masc);
                        db.updateChildren(CUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Fallo obtener url");
                    }
                });
            }
        });

    }
    private boolean validateForm(){
        boolean res=true;
        if (PhotoUri==null){
            res=false;
            Snackbar.make(findViewById(R.id.new_pet_view),"ingrese una foto de la mascota",Snackbar.LENGTH_LONG).show();
        }
        Log.d("check",String.valueOf(Type.getCheckedRadioButtonId()));
        if(Name.getText()==null){
            res=false;
            Name.setError("Requerido");
        }
        if(Direccion.getText()==null){
            res=false;
            Direccion.setError("Requerido");
        }
        if(Ciudad.getText()==null){
            Ciudad.setError("Requerido");
            res=false;
        }
        if(Raza.getText()==null){
            Raza.setError("Requerido");
            res=false;
        }
        if(Telefono.getText()==null){
            Telefono.setError("Requerido");
            res=false;
        }
        if(Age.getText()==null){
            Age.setError("Requerido");
            res=false;
        }
        return res;
    }
}
