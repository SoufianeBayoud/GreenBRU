package com.example.greenbru;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {
    private Button SignalButton;
    TextView user_textView, role;
    RoundedImageView profileImage;
    ImageView imageView;
    String url;
    private static final int PERMISSION_CODE = 1234;


    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private MapboxMap mapboxMap;
    private MapView mapView;

    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private double latitude;
    private double longitude;
    SharedPreferences sharedPreferences;
    private static final String SHARED_NAME = "user";
    private static final String USERNAME= "username";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.map_main);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        sharedPreferences = getSharedPreferences(SHARED_NAME, MODE_PRIVATE);
        String name = sharedPreferences.getString(USERNAME, null);


        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        //It's for the view in the header (user_textView)
        user_textView = headerView.findViewById(R.id.username_navigation_header);
        role = headerView.findViewById(R.id.role_navigation_layer);
        profileImage = headerView.findViewById(R.id.imageProfile);
        user_textView.setText(name);


        if (user_textView.getText().toString() != "Admin") {
            role.setText("User");
            role.setTextColor(Color.parseColor("#32CD32"));
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child(user_textView.getText().toString()).child("imageURL");
        //nameFromLoginActivity
        View inflatedView = getLayoutInflater().inflate(R.layout.fragment_settings, null);
        imageView = (ImageView) inflatedView.findViewById(R.id.profile_picture_settings);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                url = dataSnapshot.getValue().toString();

                if (url != null) {
                    Picasso.get().load(url).into(profileImage);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        SignalButton = (Button) findViewById(R.id.signal_btn);
        SignalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    latitude = lastKnownLocation.getLatitude();
                    longitude = lastKnownLocation.getLongitude();
                    Intent intent = new Intent(MapActivity.this, SignalGarbageActivity.class);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);

                } else{
                    Toast.makeText(MapActivity.this,"Unknown location",Toast.LENGTH_SHORT).show();
                }
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // GPS is not enabled
                    Toast.makeText(MapActivity.this,"GPS is not enabled",Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(MapActivity.this,"Latitude: " + latitude + " Longitude: " + longitude,Toast.LENGTH_SHORT).show();

            }
        });

        //We call the navigationView of the navigationheader/menu
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.getMenu().findItem(R.id.menuLogout).setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

            return true;
        });

        navigationView.getMenu().findItem(R.id.menuMap).setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent);

            return true;
        });
        FrameLayout wrapper = findViewById(R.id.wrapper);
        navigationView.getMenu().findItem(R.id.menuProfile).setOnMenuItemClickListener(menuItem -> {
            Bundle data = getIntent().getExtras();
            navController.navigate(R.id.action_menuProfile, data);
            wrapper.setVisibility(View.GONE);


            return true;
        });

        navigationView.getMenu().findItem(R.id.menuSettings).setOnMenuItemClickListener(menuItem -> {
            Bundle data = getIntent().getExtras();
            navController.navigate(R.id.action_menuSettings, data);
            wrapper.setVisibility(View.GONE);

            return true;
        });

        navigationView.getMenu().findItem(R.id.menuSupport).setOnMenuItemClickListener(menuItem -> {
            wrapper.setVisibility(View.GONE);

            return true;
        });

        findViewById(R.id.imageMenu).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.openDrawer(GravityCompat.START);

                    }
                }
        );


    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        MapActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                addDestinationIconSymbolLayer(style);
                mapboxMap.addOnMapClickListener(MapActivity.this);


                List<Feature> symbolLayerIconFeatureList = new ArrayList<>();


                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                        // Add the SymbolLayer icon image to the map style
                        .withImage(ICON_ID, BitmapFactory.decodeResource(
                                MapActivity.this.getResources(), R.drawable.red_marker))

                        // Adding a GeoJson source for the SymbolLayer icons.
                        .withSource(new GeoJsonSource(SOURCE_ID,
                                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

                        // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                        // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                        // the coordinate point. This is offset is not always needed and is dependent on the image
                        // that you use for the SymbolLayer icon.
                        .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(
                                        iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconIgnorePlacement(true),
                                        iconOffset(new Float[]{0f, -9f})

                                )
                        )

                        .withSource(new GeoJsonSource("selected-marker"))

                        .withLayer(new SymbolLayer("selected-marker-layer", "selected-marker")
                                .withProperties(PropertyFactory.iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconOffset(new Float[]{0f, -9f})
                                )));

                //Vaste markers voor de 3 RecyParks in Brussel
                List<MarkerOptions> RecycleContainerList = new ArrayList<>();
                RecycleContainerList.add(new MarkerOptions().position(new LatLng(50.8040133, 4.2944579)).setTitle("RecyPark North"));
                RecycleContainerList.add(new MarkerOptions().position(new LatLng(50.8002823, 4.3031717)).setTitle("Container Park Forest"));
                RecycleContainerList.add(new MarkerOptions().position(new LatLng(50.8002955, 4.2966056)).setTitle("RecyPark South"));

                mapboxMap.addMarkers(RecycleContainerList);

                GetMarkersFromDatabase();

            }


        });
    }

    private void addDestinationIconSymbolLayer(Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id", BitmapFactory.decodeResource(MapActivity.this.getResources(),
                R.drawable.red_marker));

        GeoJsonSource geoJsonSource = new GeoJsonSource(SOURCE_ID);
        loadedMapStyle.addSource(geoJsonSource);

        SymbolLayer destinationSymbolLayer = new SymbolLayer(LAYER_ID, SOURCE_ID);
        destinationSymbolLayer.withProperties(iconImage(ICON_ID),
                iconAllowOverlap(true),
                iconIgnorePlacement(true));
        loadedMapStyle.addLayer(destinationSymbolLayer);


    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Enable the most basic pulsing styling by ONLY using
// the `.pulseEnabled()` method
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .pulseEnabled(true)
                    .build();

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build());

// Enable to make component visible
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onExplanationNeeded(List<String> list) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onPermissionResult(boolean b) {
        if (b) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "Permission is not granted", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void GetMarkersFromDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://greenbru-5e1b0-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference("Signals");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot markerSnapshot : snapshot.getChildren()) {

                    double lat = markerSnapshot.child("lat").getValue(Double.class);
                    double lng = markerSnapshot.child("longitude").getValue(Double.class);

                    LatLng latLng = new LatLng(lat, lng);
                    MakeMarker(latLng);

                    mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")

                            // Add the SymbolLayer icon image to the map style
                            .withImage(ICON_ID, BitmapFactory.decodeResource(
                                    MapActivity.this.getResources(), R.drawable.red_marker))

                            // Adding a GeoJson source for the SymbolLayer icons.
                            .withSource(new GeoJsonSource(SOURCE_ID,
                                    FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))

                            // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                            // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                            // the coordinate point. This is offset is not always needed and is dependent on the image
                            // that you use for the SymbolLayer icon.
                            .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                    .withProperties(
                                            iconImage(ICON_ID),
                                            iconAllowOverlap(true),
                                            iconIgnorePlacement(true),
                                            iconOffset(new Float[]{0f, -9f})

                                    )
                            )

                            .withSource(new GeoJsonSource("selected-marker"))

                            .withLayer(new SymbolLayer("selected-marker-layer", "selected-marker")
                                    .withProperties(PropertyFactory.iconImage(ICON_ID),
                                            iconAllowOverlap(true),
                                            iconOffset(new Float[]{0f, -9f})
                                    )));


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("tag", "Failed to read value.", error.toException());
            }

        });
    }
    public void MakeMarker(@NonNull LatLng point){
        Feature markerFeature = Feature.fromGeometry(Point.fromLngLat(point.getLongitude(), point.getLatitude()));

        markerFeature.addNumberProperty("lng", point.getLongitude());
        markerFeature.addNumberProperty("lat", point.getLatitude());
        symbolLayerIconFeatureList.add(markerFeature);
    }
    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        mapView.getMapAsync(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
