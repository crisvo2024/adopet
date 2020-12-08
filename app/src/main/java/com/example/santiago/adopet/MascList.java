package com.example.santiago.adopet;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MascList extends AppCompatActivity {
    public static final String KEY="mascota";
    private static final String TAG="masc";
    private int tipo;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Mascota, MascViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private GridLayoutManager mManager;

    public MascList(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masc_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tipo=getIntent().getIntExtra(KEY,0);
        if(tipo==R.id.perro)getWindow().setBackgroundDrawableResource(R.drawable.fondo_perros);
        else getWindow().setBackgroundDrawableResource(R.mipmap.fondo_gatos);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = findViewById(R.id.dogs_list);
        mManager = new GridLayoutManager(mRecycler.getContext(),2);
        mRecycler.setLayoutManager(mManager);
        mRecycler.setHasFixedSize(true);

        Query MascQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Mascota>()
                .setQuery(MascQuery, Mascota.class)
                .build();
        mAdapter = new FirebaseRecyclerAdapter<Mascota, MascViewHolder>(options){

            @NonNull
            @Override
            public MascViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
                return new MascViewHolder(inflater.inflate(R.layout.masc_card,viewGroup,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull MascViewHolder holder, int position, @NonNull Mascota model) {
                final DatabaseReference mascRef = getRef(position);
                final String mascKey = mascRef.getKey();
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MascList.this,MascDet.class);
                        intent.putExtra(TAG,mascKey);
                        startActivity(intent);
                    }
                });
                holder.bindToAct(model);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }
    public Query getQuery(DatabaseReference databaseReference){
        if(tipo==R.id.perro){
            return databaseReference.child("Mascotas").orderByChild("Type").equalTo("Dog").limitToFirst(100);
        }else{
            return databaseReference.child("Mascotas").orderByChild("Type").equalTo("Cat").limitToFirst(100);
        }
    }
}
