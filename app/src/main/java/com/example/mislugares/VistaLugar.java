package com.example.mislugares;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.text.DateFormat;

/**
 * Created by ripper on 2/02/17.
 */

public class VistaLugar extends AppCompatActivity {

    private long id;
    private Lugar lugar;
    private ImageView imageView;
    private Uri uriFoto;
    final static int RESULTADO_EDITAR = 1;
    final static int RESULTADO_GALERIA = 2;
    final static int RESULTADO_FOTO = 3;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_lugar);

        Bundle extras = getIntent().getExtras();
        id = extras.getLong("id", -1);
        lugar = Lugares.elemento((int) id);
        imageView = (ImageView)findViewById(R.id.foto);

        actualizarVistas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vista_lugar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent i = new Intent((Intent.ACTION_SEND));
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, lugar.getNombre() + " - " + lugar.getUrl());
                startActivity(i);
                return true;
            case R.id.accion_llegar:
                verMapa(null);
                return true;
            case R.id.accion_editar:
                Intent in = new Intent(this, EdicionLugar.class);
                in.putExtra("id", id);
                startActivityForResult(in, RESULTADO_EDITAR);
                return true;
            case R.id.accion_borrar:
                confirmacionBorrado(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void confirmacionBorrado(View v){
        new AlertDialog.Builder (this)
                .setTitle("Borrado de lugar")
                .setMessage("¿Estás seguro que quieres eliminar este lugar?")
                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Lugares.borrar((int) id);
                        finish();
                    }})
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void actualizarVistas(){
        TextView nombre = (TextView)findViewById(R.id.nombre);
        nombre.setText(lugar.getNombre());

        ImageView logo_tipo = (ImageView)findViewById(R.id.logo_tipo);
        logo_tipo.setImageResource(lugar.getTipo().getRecurso());

        TextView tipo = (TextView)findViewById(R.id.tipo);
        tipo.setText(lugar.getTipo().getTexto());

        if (lugar.getDireccion() == "") {
            findViewById(R.id.direccion).setVisibility(View.GONE);
        } else {
            TextView direccion = (TextView)findViewById(R.id.direccion);
            direccion.setText(lugar.getDireccion());
        }

        if (lugar.getTelefono() == 0){
            findViewById(R.id.telefono).setVisibility(View.GONE);
        } else {
            TextView telefono = (TextView)findViewById(R.id.telefono);
            telefono.setText(Integer.toString(lugar.getTelefono()));
        }

        if (lugar.getUrl() == ""){
            findViewById(R.id.url).setVisibility(View.GONE);
        } else {
            TextView url = (TextView)findViewById(R.id.url);
            url.setText(lugar.getUrl());
        }

        if (lugar.getComentario() == ""){
            findViewById(R.id.comentario).setVisibility(View.GONE);
        } else {
            TextView comentario = (TextView)findViewById(R.id.comentario);
            comentario.setText(lugar.getComentario());
        }

        TextView fecha = (TextView)findViewById(R.id.fecha);
        fecha.setText((DateFormat.getDateInstance().format(new Date(lugar.getFecha()))));

        TextView hora = (TextView)findViewById(R.id.hora);
        hora.setText(DateFormat.getTimeInstance().format(new Date(lugar.getFecha())));

        RatingBar valoracion = (RatingBar)findViewById(R.id.valoracion);
        valoracion.setRating(lugar.getValoracion());
        valoracion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float valor, boolean fromUser) {
                lugar.setValoracion(valor);
            }
        });

        ponerFoto(imageView, lugar.getFoto());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_EDITAR) {
            actualizarVistas();
            findViewById(R.id.scrollView1).invalidate();
        } else if (requestCode == RESULTADO_GALERIA && resultCode == Activity.RESULT_OK) {
            lugar.setFoto(data.getDataString());
            ponerFoto(imageView, lugar.getFoto());
        } else if (requestCode == RESULTADO_FOTO && resultCode == Activity.RESULT_OK && lugar != null && uriFoto != null) {
            lugar.setFoto(uriFoto.toString());
            ponerFoto(imageView, lugar.getFoto());
        }
    }

    public void verMapa (View v){
        Uri uri;
        double lat = lugar.getPosicion().getLatitud();
        double lon = lugar.getPosicion().getLongitud();

        if (lat != 0 || lon != 0) {
            uri = Uri.parse("geo:"+ lat + "," + lon);
        } else {
            uri = Uri.parse("geo:0,0?q=" + lugar.getDireccion());
        }
        Intent i = new Intent (Intent.ACTION_VIEW, uri);
        startActivity(i);
    }

    public void llamadaTelefono (View v){
        startActivity(new Intent (Intent.ACTION_DIAL, Uri.parse("tel:"+ lugar.getTelefono())));
    }

    public void pgWeb(View v){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lugar.getUrl())));
    }

    public void galeria(View v){
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(i, RESULTADO_GALERIA);
    }

    protected void ponerFoto (ImageView imagen, String uri){
        if (uri != null){
            imageView.setImageBitmap(reduceBitmap(this, uri, 1024, 1024));
        } else {
            imagen.setImageBitmap(null);
        }
    }

    private Bitmap reduceBitmap(Context contexto, String uri, int maxAncho, int maxAlto) {

        try {
            final BitmapFactory.Options opciones = new BitmapFactory.Options();
            opciones.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.parse(uri)), null, opciones);
            opciones.inSampleSize = (int) Math.max(
                    Math.ceil(opciones.outWidth / maxAncho),
                    Math.ceil(opciones.outHeight / maxAlto));
            opciones.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(contexto.getContentResolver().openInputStream(Uri.parse(uri)),null,opciones);
        } catch (FileNotFoundException e) {
            Toast.makeText(contexto, "Fichero/recurso no encontrado", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }

    public void tomarFoto(View v) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        uriFoto = Uri.fromFile(
                new File(Environment.getExternalStorageDirectory() + File.separator
                        + "/Download/img_" + (System.currentTimeMillis() / 1000) + ".jpg"));  //He puesto el directorio tal cual lo pone en mi emulador
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
        startActivityForResult(intent, RESULTADO_FOTO);
    }

    public void eliminarFoto(View v){
        lugar.setFoto(null);
        ponerFoto(imageView,null);
    }
}

