package com.depauw.locallocator;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.depauw.locallocator.database.BorderVertex;
import com.depauw.locallocator.database.DBHelper;
import com.depauw.locallocator.database.Local;
import com.depauw.locallocator.database.Worksite;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.depauw.locallocator.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.PolyUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int COLOR_BABY_BLUE = 0x7766cbf0;
    private static final int COLOR_PURPLE = 0x77ccb7dc;
    private static final int COLOR_GREEN = 0x776bbd82;
    private static final int COLOR_DARK_CYAN = 0x776bbdae;
    private static final int COLOR_BEIGE = 0x77d9e0a9;
    private static final int COLOR_ORANGE = 0x77f2b797;
    private static final int COLOR_YELLOW = 0x77f2ee97;

    private GoogleMap mMap;
    private PlacesClient placesClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private ActivityMapsBinding binding;
    private ArrayList<Local> locals;
    private ArrayList<Worksite> worksites;

    GoogleMap.OnMapClickListener focus_map_onMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {

        }
    };

    /**
     * Toggles the visibility of the polygons
     */
    private View.OnClickListener button_toggle_locals_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean changeTo = !locals.get(0).getPolygon().isVisible();
            for(int i = 0; i < locals.size(); i++){
                locals.get(i).getPolygon().setVisible(changeTo);
            }
        }
    };

    /**
     * Toggles the visibility of the worksites
     */
    private View.OnClickListener button_toggle_worksites_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean changeTo = !worksites.get(0).getMarker().isVisible();
            for(int i = 0; i < worksites.size(); i++){
                worksites.get(i).getMarker().setVisible(changeTo);
            }
        }
    };

    /**
     * Handles retrieving the website url from the database given the current local.
     * Redirects the user to the website of the current local within the devices
     * default browser.
     */
    private View.OnClickListener button_website_onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int local = Integer.parseInt(binding.textviewLocal.getText().toString().substring(6));

            DBHelper dbHelper = DBHelper.getInstance(MapsActivity.this);
            String websiteUrl = dbHelper.getWebsiteByLocal(local);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
            startActivity(intent);
        }
    };

    /**
    * Takes the input string from the search bar and locates the coordinates of the best match location
    */
    private SearchView.OnQueryTextListener search_text_onQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            String location = binding.searchviewAddress.getQuery().toString();
            List<Address> addressList = null;
            if(location != null || location.equals("")) {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                if (geocoder.isPresent()) {
                    //expensive!
                    try {
                        //no backend service available?
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(addressList != null && addressList.size() > 0) {
                    Address address = addressList.get(0);
                    LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(latlng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));

                    // determine local and adjust panel accordingly
                    Local selectedLocal = determineLocal(latlng);

                    selectLocal(selectedLocal);
                }
                else {
                    resetPanel();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage("Sorry! :(\nWe couldn't find that location");
                    builder.setTitle("Location Not Found");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        resetPanel();

        DBHelper db = DBHelper.getInstance(MapsActivity.this);
        locals = db.getLocals();
        locals.get(0).setColor(COLOR_ORANGE);
        locals.get(1).setColor(COLOR_DARK_CYAN);
        locals.get(2).setColor(COLOR_BABY_BLUE);
        locals.get(3).setColor(COLOR_GREEN);
        locals.get(4).setColor(COLOR_PURPLE);
        locals.get(5).setColor(COLOR_GREEN);
        locals.get(6).setColor(COLOR_YELLOW);
        locals.get(7).setColor(COLOR_BEIGE);
        locals.get(8).setColor(COLOR_ORANGE);
        locals.get(9).setColor(COLOR_YELLOW);
        locals.get(10).setColor(COLOR_PURPLE);
        locals.get(11).setColor(COLOR_GREEN);
        locals.get(12).setColor(COLOR_DARK_CYAN);
        locals.get(13).setColor(COLOR_ORANGE);
        locals.get(14).setColor(COLOR_PURPLE);
        locals.get(15).setColor(COLOR_BEIGE);
        locals.get(16).setColor(COLOR_ORANGE);
        locals.get(17).setColor(COLOR_YELLOW);
        locals.get(18).setColor(COLOR_BABY_BLUE);
        locals.get(19).setColor(COLOR_BABY_BLUE);

        // Initialize the Places SDK
        Places.initialize(getApplicationContext(), "@string/google_maps_key");
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        binding.searchviewAddress.setOnQueryTextListener(search_text_onQueryTextListener);
        binding.buttonWebsite.setOnClickListener(button_website_onClickListener);
        binding.buttonToggleLocals.setOnClickListener(button_toggle_locals_onClickListener);
        binding.buttonToggleWorksites.setOnClickListener(button_toggle_worksites_onClickListener);

        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Move camera to Illinois
        LatLng illinois = new LatLng(40.121542, -89.147832);
        //mMap.addMarker(new MarkerOptions().position(illinois).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(illinois, 6.5f));
        // Add jurisdiction lines
        for(int i = 0; i < locals.size(); i++) {
            addBorderLines(mMap, locals.get(i));
        }

        // Add worksites
        addWorksites(mMap);

        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                //indicate local
                String clickedLocal = polygon.getTag().toString();
                Local local = locals.get(0);
                for(int i = 0; i < locals.size(); i++) {
                    String currLocal = String.valueOf((locals.get(i).getIdentifier()));
                    if(currLocal.equals(clickedLocal)) {
                        local = locals.get(i);
                    }
                }
                selectLocal(local);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                String title = marker.getTitle();
                for(int i = 0; i < worksites.size(); i++) {
                    Worksite currWorksite = worksites.get(i);
                    if(title.matches(currWorksite.getName())){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                        builder.setMessage(currWorksite.getInformation());
                        builder.setTitle("Local: " + currWorksite.getLocal() + "\n" + title);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setIcon(R.drawable.marker);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        return false;
                    }
                }

                return false;
            }
        });
    }

    public void addBorderLines(GoogleMap googleMap, Local local) {
        DBHelper dbHelper = DBHelper.getInstance(MapsActivity.this);
        ArrayList<BorderVertex> borderVertices = dbHelper.getBordersByLocal(local.getIdentifier());
        ArrayList<LatLng> latLngVertices = new ArrayList<>();
        if(borderVertices != null) {
            for (int i = 0; i < borderVertices.size(); i++) {
                BorderVertex borderVertex = borderVertices.get(i);
                LatLng latLng = new LatLng(borderVertex.getLatitude(), borderVertex.getLongitude());
                latLngVertices.add(latLng);
            }

            Polygon pl1 = googleMap.addPolygon(new PolygonOptions()
                    .clickable(true)
                    .addAll(latLngVertices)
                    .strokeColor(Color.BLACK));
            pl1.setTag(local.getIdentifier());
            pl1.setFillColor(local.getColor());

            local.setPolygon(pl1);
        }
    }

    public void addWorksites(GoogleMap googleMap) {
        DBHelper dbHelper = DBHelper.getInstance(MapsActivity.this);
        worksites = dbHelper.getWorksites();

        for(int i = 0; i < worksites.size(); i++) {
            Worksite currWorksite = worksites.get(i);
            String title = currWorksite.getName();
            float latitude = currWorksite.getLatitude();
            float longitude = currWorksite.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(title));
            currWorksite.setMarker(marker);
        }
    }

    public Local determineLocal(LatLng latLng) {
        ArrayList<Local> possibleLocals = new ArrayList<>();
        Local selectedLocal = new Local(999, Color.BLACK);

        double lat = latLng.latitude;
        double lng = latLng.longitude;
        // determine if the location is even a possibility for any locals
        for(int i = 0; i < locals.size(); i++){
            Local currLocal = locals.get(i);
            // for a location to be within a local its lat and lng must fall
            // within the ranges for both lat and lng for that local
            if(lat >= currLocal.getMinLat() &&
               lat <= currLocal.getMaxLat() &&
               lng >= currLocal.getMinLng() &&
               lng <= currLocal.getMaxLng()){
                possibleLocals.add(currLocal);
            }
        }
        if(possibleLocals.size() == 1){
            selectedLocal = possibleLocals.get(0);
        }
        else if(possibleLocals.size() > 1){
            for(int i = 0; i < possibleLocals.size(); i++) {
                if(PolyUtil.containsLocation(latLng, possibleLocals.get(i).getPolygon().getPoints(), true)){
                    selectedLocal = possibleLocals.get(i);
                }
            }
        }
        return selectedLocal;
    }

    public void resetPanel() {
        binding.textviewLocal.setText("Search for a location...");
        binding.buttonWebsite.setVisibility(View.INVISIBLE);
        binding.textviewFounded.setVisibility(View.INVISIBLE);
        binding.textviewPhone.setVisibility(View.INVISIBLE);
        binding.textviewAddress.setVisibility(View.INVISIBLE);
    }

    public void selectLocal(Local local) {
        binding.textviewLocal.setText("Local " + local.getIdentifier());

        binding.buttonWebsite.setVisibility(View.VISIBLE);

        binding.textviewFounded.setVisibility(View.VISIBLE);
        binding.textviewFounded.setText("Founded: " + local.getFounded());

        binding.textviewPhone.setVisibility(View.VISIBLE);
        binding.textviewPhone.setText("+1 " + local.getPhone());

        binding.textviewAddress.setMaxWidth(400);
        binding.textviewAddress.setVisibility(View.VISIBLE);
        binding.textviewAddress.setText(local.getAddress());
    }
}