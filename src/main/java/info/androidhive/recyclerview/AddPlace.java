package info.androidhive.recyclerview;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPlace extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    EditText name, latitude, longitude;
    ImageButton image;
    Button add;

    public Uri selectedImage;

    private PlaceDBHelper mDatabase;
    private static int RESULT_LOAD_IMG = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);

        name = (EditText) findViewById(R.id.newname);
        latitude = (EditText) findViewById(R.id.newlatitude);
        longitude = (EditText) findViewById(R.id.newlongitude);
        image = (ImageButton) findViewById(R.id.addimage);
        add = (Button) findViewById(R.id.addbutton);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent output = new Intent();
                Log.v("name", name.getText().toString());
                output.putExtra("name", name.getText().toString());
                output.putExtra("latitude", latitude.getText().toString());
                output.putExtra("longitude", longitude.getText().toString());
                setResult(RESULT_OK, output);

                PlaceDBHelper db = new PlaceDBHelper(getApplicationContext());
                try {
                    String nameT = name.getText().toString();
                    String latitudeT = latitude.getText().toString();
                    String longitudeT = longitude.getText().toString();
                    db.addPlace(nameT, Double.parseDouble(latitudeT), Double.parseDouble(longitudeT), nameT + latitudeT + longitudeT + ".jpg");
                    Log.v("add db", selectedImage.toString());
                } catch (Exception e) {
                    Log.v("error", "add to database");
                }


                finish();
            }
        });
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new ImageGetterAsyncTask().execute();

            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    private class ImageGetterAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            final List<Intent> cameraIntents = new ArrayList();
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntents.add(galleryIntent);
            cameraIntents.add(cameraIntent);

            final Intent intentChooser = new Intent();
            final Intent chooserIntent = Intent.createChooser(intentChooser, "Select Source");

            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

            startActivityForResult(chooserIntent, RESULT_LOAD_IMG);//YOUR_SELECT_PICTURE_REQUEST_CODE);

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            //Log.v("after photo",data.getAction());

            if(resultCode == RESULT_OK) {

                Log.v("inresult","inresult");
               Log.v("reqco",data+"");


                if(data != null && data.getExtras() == null){
                    selectedImage = data.getData();
                    Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                    saveToInternalStorage(bmp);

                }else if(data.getExtras() != null) {
                    Log.v("cam photo",":");
                    Bitmap bmp =  (Bitmap) data.getExtras().get("data");
                    saveToInternalStorage(bmp);
                }
            }
            else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
            }
            return;
        } else {
            getlocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getlocation();
        }
    }

    private void getlocation() {
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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude.setText(String.valueOf(mLastLocation.getLatitude()));
            longitude.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage){
        // path to /data/data/yourapp/app_data/imageDir
        // Create imageDir
        File mypath=new File(MainActivity.directory,name.getText().toString()+latitude.getText().toString()+longitude.getText().toString()+".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
            image.setImageBitmap(bitmapImage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
