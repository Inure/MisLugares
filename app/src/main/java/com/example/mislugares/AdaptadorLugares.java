package com.example.mislugares;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by ripper on 3/02/17.
 */

public class AdaptadorLugares extends BaseAdapter {
    private LayoutInflater inflador; //Crea layouts a partir del xml
    TextView nombre, direccion;
    ImageView foto;
    RatingBar valoracion;

    @Override
    public View getView(int posicion, View vistaReciblada, ViewGroup padre) {

        Lugar lugar = Lugares.elemento(posicion);
        if (vistaReciblada == null) {
            vistaReciblada = inflador.inflate(R.layout.elemento_lista, null);
        }

        nombre = (TextView)vistaReciblada.findViewById(R.id.nombre);
        direccion = (TextView)vistaReciblada.findViewById(R.id.direccion);
        foto = (ImageView)vistaReciblada.findViewById(R.id.foto);
        valoracion = (RatingBar)vistaReciblada.findViewById(R.id.valoracion);

        nombre.setText(lugar.getNombre());
        direccion.setText(lugar.getDireccion());

        int id = R.drawable.otros;

        switch (lugar.getTipo()) {
            case RESTAURANTE: id = R.drawable.restaurante; break;
            case BAR: id = R.drawable.bar; break;
            case COPAS: id = R.drawable.copas; break;
            case ESPECTACULO: id = R.drawable.espectaculos; break;
            case HOTEL: id = R.drawable.hotel; break;
            case COMPRAS: id = R.drawable.compras; break;
            case EDUCACION: id = R.drawable.educacion; break;
            case DEPORTE: id = R.drawable.deporte; break;
            case NATURALEZA: id = R.drawable.naturaleza; break;
            case GASOLINERA: id = R.drawable.gasolinera; break;
        }

        foto.setImageResource(id);
        foto.setScaleType(ImageView.ScaleType.FIT_END);
        valoracion.setRating(lugar.getValoracion());

        return vistaReciblada;
    }

    public AdaptadorLugares (Context contexto){
        inflador = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Lugares.size();
    }

    @Override
    public Object getItem(int posicion) {
        return Lugares.elemento(posicion);
    }

    @Override
    public long getItemId(int posicion) {
        return posicion;
    }


}
