package com.example.santiago.adopet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class MascViewHolder extends RecyclerView.ViewHolder {
    public TextView sexo;
    public TextView nombre;
    public TextView raza;
    public ImageView foto;

    public MascViewHolder(@NonNull View itemView) {
        super(itemView);
        sexo=itemView.findViewById(R.id.sexo);
        nombre=itemView.findViewById(R.id.name);
        raza=itemView.findViewById(R.id.raza);
        foto=itemView.findViewById(R.id.foto);
    }
    public void bindToAct(Mascota M){
        sexo.setText(M.Sex);
        nombre.setText(M.Name);
        raza.setText(M.Raza);
        if(M.Photo!=null){
            Context context=foto.getContext();
            Glide.with(context)
                    .load(M.Photo)
                    .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_account_circle_black_380dp)
                        .dontAnimate()
                        .fitCenter()
                        .circleCrop())
                    .into(foto);
        }
    }
}
