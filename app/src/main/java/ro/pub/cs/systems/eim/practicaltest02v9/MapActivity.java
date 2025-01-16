package ro.pub.cs.systems.eim.practicaltest02v9;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ghelmegioaia = new LatLng(44.614722, 22.834722);
        LatLng bucharest = new LatLng(44.4268, 26.1025);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ghelmegioaia, 10));
        googleMap.addMarker(new MarkerOptions().position(bucharest).title("Marker in Bucharest"));
    }
}