package ca.bcit.assignment.assignment1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.bcit.assignment.assignment1.database.AppDatabase;
import ca.bcit.assignment.assignment1.models.Caption;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ImageView displayImageView;
    private ImageButton leftImageButton;
    private ImageButton rightImageButton;
    private TextView timestampTextView;
    private TextView locationTextView;
    private TextInputLayout captionInputLayout;
    private Button updateCaptionButton;
    private Button searchButton;
    private Button snapButton;
    private ArrayList<String> photoGallery;
    private String currentPhotoPath = null;
    private String currentPhotoFileName = null;
    private int currentPhotoIndex = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 2;
    private AppDatabase db;
    private String searchCaption = null;
    private LocationManager locationManager;
    private Geocoder geocoder;
    private ArrayList<String> locationsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        /*
        // Clear data code
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Caption[] captions = db.captionDao().loadAllCaptions();

        for (Caption c : captions) {
            File file = new File(directory, c.getImage());
            file.delete();
        }

        db.captionDao().deleteCaptions(captions);
        */

        displayImageView = (ImageView) findViewById(R.id.displayImageView);
        leftImageButton = (ImageButton) findViewById(R.id.leftButton);
        rightImageButton = (ImageButton) findViewById(R.id.rightButton);
        timestampTextView = (TextView) findViewById(R.id.timestamp);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        captionInputLayout = (TextInputLayout) findViewById(R.id.captionInputLayout);
        updateCaptionButton = (Button) findViewById(R.id.updateCaptionButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        snapButton = (Button) findViewById(R.id.snapButton);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        locationsArrayList = new ArrayList<>();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        Date minDate = null;
        Date maxDate = null;
        if (extras != null) {
            int startYear = extras.getInt("StartYear", 0);
            int startMonth = extras.getInt("StartMonth", 0);
            int startDay = extras.getInt("StartDay", 0);
            int startHour = extras.getInt("StartHour", 0);
            int startMinute = extras.getInt("StartMinute", 0);
            int endYear = extras.getInt("EndYear", 0);
            int endMonth = extras.getInt("EndMonth", 0);
            int endDay = extras.getInt("EndDay", 0);
            int endHour = extras.getInt("EndHour", 0);
            int endMinute = extras.getInt("EndMinute", 0);
            searchCaption = extras.getString("Caption", null);
            Calendar cl = Calendar.getInstance();
            cl.set(startYear, startMonth, startDay, startHour, startMinute);
            minDate = cl.getTime();
            cl.set(endYear, endMonth, endDay, endHour, endMinute);
            maxDate = cl.getTime();
            Collections.addAll(locationsArrayList, extras.getStringArray("Locations"));
        } else {
            minDate = new Date(Long.MIN_VALUE);
            maxDate = new Date(Long.MAX_VALUE);
            Collections.addAll(locationsArrayList,  db.captionDao().getAllLocations().toArray(new String[0]));
        }

        photoGallery = populateGallery(minDate, maxDate);
        if (photoGallery.size() > 0) {
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            File file = new File(photoGallery.get(currentPhotoIndex));
            currentPhotoFileName = file.getName();
            displayPhoto(currentPhotoPath);
        }

        leftImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (photoGallery.size() > 0) {
                currentPhotoIndex--;
                if (currentPhotoIndex < 0) {
                    currentPhotoIndex = (photoGallery.size() - 1);
                }
                File file = new File(photoGallery.get(currentPhotoIndex));
                currentPhotoFileName = file.getName();
                displayPhoto(photoGallery.get(currentPhotoIndex));
            }
            }
        });

        rightImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (photoGallery.size() > 0) {
                currentPhotoIndex++;
                if (currentPhotoIndex >= photoGallery.size()) {
                    currentPhotoIndex = 0;
                }
                File file = new File(photoGallery.get(currentPhotoIndex));
                currentPhotoFileName = file.getName();
                displayPhoto(photoGallery.get(currentPhotoIndex));
            }
            }
        });

        updateCaptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoGallery.size() == 0) {
                    return;
                }

                String editText = captionInputLayout.getEditText().getText().toString();
                Caption caption = db.captionDao().findCaptionByImage(currentPhotoFileName);
                caption.setCaption(editText);
                db.captionDao().updateCaptions(caption);
                Toast.makeText(getApplicationContext(), "Updated Caption", Toast.LENGTH_SHORT).show();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    protected void onDestroy() {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, MainActivity.this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Android/data/ca.bcit.assignment.assignment1/files/Pictures");
        photoGallery = new ArrayList<String>();

        List<Caption> captions = null;
        if (searchCaption != null)
            captions = db.captionDao().findImagesByCaptionDatesLocation(minDate, maxDate, "%" + searchCaption + "%", locationsArrayList.toArray(new String[0]));
        else
            captions = db.captionDao().findImagesByDatesLocation(minDate, maxDate, locationsArrayList.toArray(new String[0]));

        for (Caption c : captions) {
            String path = file.getPath();
            photoGallery.add(path + "/" + c.getImage());
            if (c.getLocation() == null) {
                c.setLocation("");
                db.captionDao().updateCaptions(c);
            }
        }

        return photoGallery;
    }

    private void displayPhoto(String path) {
        Caption caption = db.captionDao().findCaptionByImage(currentPhotoFileName);
        String captionString = caption.getCaption();
        String location = caption.getLocation();

        displayImageView.setImageBitmap(BitmapFactory.decodeFile(path));
        DateFormat dateFormat = new DateFormat();
        timestampTextView.setText(dateFormat.format("yyyy-MM-dd HH:mm:ss", caption.getWhenCreated()));

        if (captionString != null) {
            captionInputLayout.getEditText().setText(captionString);
        } else {
            captionInputLayout.getEditText().setText("");
        }

        if (!location.equals("")) {
            locationTextView.setText(location);
        } else {
            locationTextView.setText(R.string.locationDefault);
        }

        captionInputLayout.getEditText().clearFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (((requestCode == REQUEST_IMAGE_CAPTURE) || (requestCode == REQUEST_TAKE_PHOTO)) && resultCode == RESULT_OK) {
            Caption caption = new Caption(currentPhotoFileName);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location == null) {
                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.size() != 0) {
                            Address address = addresses.get(0);
                            String locationString = address.getLocality() + ", " + address.getAdminArea() + ", " + address.getCountryName();
                            caption.setLocation(locationString);
                        }
                    } catch (IOException ioe) {
                        Log.d("IO Exception Caught: ", ioe.getMessage());
                    }
                }
            }

            db.captionDao().insertCaptions(caption);

            locationsArrayList.add(caption.getLocation());

            photoGallery = populateGallery(new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE));

            if (photoGallery.size() > 0) {
                currentPhotoIndex = photoGallery.size() - 1;
            }

            currentPhotoPath = photoGallery.get(currentPhotoIndex);
            displayPhoto(currentPhotoPath);
            galleryAddPic();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                currentPhotoFileName = photoFile.getName();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ca.bcit.assignment.assignment1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
