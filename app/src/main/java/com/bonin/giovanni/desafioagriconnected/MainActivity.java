package com.bonin.giovanni.desafioagriconnected;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainActivity extends AppCompatActivity {


    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0, Speed = 0.0, time = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private int locationRequestCode = 1000;
    private TextView txtLocation, txtSpeed, txtTime;
    private Button btnStop, btnStart;
    private ImageView circIV;

    private boolean isContinue = false;
    private boolean isFirst = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStart = (Button) findViewById(R.id.btnStart);
        circIV = (ImageView) findViewById(R.id.circIV);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(getApplicationContext(), "Location Null", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        //wayLatitude = location.getLatitude();
                        //wayLongitude = location.getLongitude();
                        Speed = location.getSpeed();

                        if (isContinue) {
                            //txtLocation.setText(String.format(Locale.US, "Lat: %s - Long: %s", wayLatitude, wayLongitude));
                            //txtSpeed.setText(String.format(Locale.US, "Speed: %s km/h", Speed*3.6));
                            //txtTime.setText(String.format(Locale.US, "Time: %s ", time));
                            circularImageBar(circIV, (int) Speed);
                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        btnStart.setOnClickListener((View v) -> {
            isContinue = true;
            getLocation();
        });

        btnStop.setOnClickListener((View v) -> {
            isContinue = false;
        });


    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    locationRequestCode);

        } else {
            if(isFirst) isFirst = false;
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(isFirst){

        }else {
            isContinue = true;
            getLocation();
        }

    }


    @Override
    public void onStop(){
        super.onStop();
            isContinue = false;
            mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(isContinue) getLocation();

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private void circularImageBar(ImageView iv2, int i) {

        Bitmap b = Bitmap.createBitmap(500, 500,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();

        paint.setColor(Color.parseColor("#c4c4c4"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(250, 250, 240, paint);

        paint.setColor(Color.parseColor("#FFDB4C"));
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL);
        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        oval.set(10,10,490,490);

        canvas.drawArc(oval, 270, ((i*360)/100), false, paint);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.parseColor("#000000"));
        paint.setTextSize(100);

        canvas.drawText(""+i+ " Km/h", 250, 250+(paint.getTextSize()/3), paint);


        iv2.setImageBitmap(b);
    }


}
