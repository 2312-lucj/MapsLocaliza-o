package br.com.etecia.mapslocateuser;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private final String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private LocationManager locationManager;

    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Permissoes.validarPermissoes(permissoes, this, 1);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        locationListener = location -> {
            Log.d("Localização", "onLocationChanged: " + location.toString());


            double latitude = location.getLatitude();
            double longitude = location.getLongitude();


            mMap.clear();


            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

            try {

                List<Address> listaEndereco = geocoder.getFromLocation(latitude, longitude, 1);


                //List<Address> listaEndereco = geocoder.getFromLocationName(enderecoLocal, 1);


                if (listaEndereco != null && listaEndereco.size() > 0) {

                    Address endereco = listaEndereco.get(0);
                    //Log.d("local", "onLocationChanged: " + endereco.getAddressLine(0));



                    double lat = endereco.getLatitude();
                    double lon = endereco.getLongitude();
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


                    LatLng localUsuario = new LatLng(lat, lon);

                    mMap.addMarker(new MarkerOptions()
                            .position(localUsuario)
                            .title("Local atual")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.loc))
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localUsuario, 15));


                    Log.d("local", "onLocationChanged: " + endereco.toString());
                    //txtNomeEndereco.setText(endereco.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000, 10,
                    locationListener
            );
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {

            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {

                validacaoUsuario();

            }

            else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0, 0,
                            locationListener
                    );
                }

            }
        }
    }

    private void validacaoUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissão negada!!!");
        builder.setMessage("Para utilizar o App é necessário aceitar as permissões!!!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialogInterface, i) -> finish());
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}