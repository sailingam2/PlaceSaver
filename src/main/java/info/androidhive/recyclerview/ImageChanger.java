package info.androidhive.recyclerview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageChanger extends AppCompatActivity {

    String name;
    double latitude;
    double longitude;
    Uri selectedImage;
    Button returnButton;

    private static final int RESULT_LOAD_IMG = 0 ;
    ImageButton image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_changer);
        Intent imageChanger = getIntent();
        this.name = imageChanger.getStringExtra("name");
        this.latitude = imageChanger.getDoubleExtra("latitude",0.0);
        this.longitude = imageChanger.getDoubleExtra("longitude",0.0);
        image = (ImageButton)findViewById(R.id.imageButton);
        returnButton = (Button)findViewById(R.id.returnButton);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageGetterAsyncTask().execute();
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",1);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
            try{
                if(resultCode == RESULT_OK){

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
            }catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                        .show();
            }

    }

    private void saveToInternalStorage(Bitmap bitmapImage){
        // path to /data/data/yourapp/app_data/imageDir
        // Create imageDir
        File mypath=new File(MainActivity.directory,name+latitude+longitude+".jpg");

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
}
