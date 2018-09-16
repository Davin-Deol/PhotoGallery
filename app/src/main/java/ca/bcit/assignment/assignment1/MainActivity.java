package ca.bcit.assignment.assignment1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.bcit.assignment.assignment1.database.AppDatabase;
import ca.bcit.assignment.assignment1.models.Caption;

public class MainActivity extends AppCompatActivity {

    private ImageView displayImageView;
    private ImageButton leftImageButton;
    private ImageButton rightImageButton;
    private TextView timestampTextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);

        displayImageView = (ImageView) findViewById(R.id.displayImageView);
        leftImageButton = (ImageButton) findViewById(R.id.leftButton);
        rightImageButton = (ImageButton) findViewById(R.id.rightButton);
        timestampTextView = (TextView) findViewById(R.id.timestamp);
        captionInputLayout = (TextInputLayout) findViewById(R.id.captionInputLayout);
        updateCaptionButton = (Button) findViewById(R.id.updateCaptionButton);
        searchButton = (Button) findViewById(R.id.searchButton);
        snapButton = (Button) findViewById(R.id.snapButton);

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
        } else {
            minDate = new Date(Long.MIN_VALUE);
            maxDate = new Date(Long.MAX_VALUE);
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


    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/Android/data/ca.bcit.assignment.assignment1/files/Pictures");
        photoGallery = new ArrayList<String>();

        List<Caption> captions = null;
        if (searchCaption != null)
            captions = db.captionDao().findImagesWithCaptionBetweenDates(minDate, maxDate, "%" + searchCaption + "%");
        else
            captions = db.captionDao().findImagesBetweenDates(minDate, maxDate);

        for (Caption c : captions) {
            String path = file.getPath();
            photoGallery.add(path + "/" + c.getImage());
        }

        return photoGallery;
    }

    private void displayPhoto(String path) {
        Caption caption = db.captionDao().findCaptionByImage(currentPhotoFileName);

        displayImageView.setImageBitmap(BitmapFactory.decodeFile(path));
        DateFormat dateFormat = new DateFormat();
        timestampTextView.setText(dateFormat.format("yyyy-MM-dd HH:mm:ss", caption.getWhenCreated()));

        if (caption.getCaption() != null) {
            captionInputLayout.getEditText().setText(caption.getCaption());
        } else {
            captionInputLayout.getEditText().setText("");
        }

        captionInputLayout.getEditText().clearFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (((requestCode == REQUEST_IMAGE_CAPTURE) || (requestCode == REQUEST_TAKE_PHOTO)) && resultCode == RESULT_OK) {
            Caption caption = new Caption(currentPhotoFileName);
            db.captionDao().insertCaptions(caption);

            photoGallery = populateGallery(new Date(Long.MIN_VALUE), new Date(Long.MAX_VALUE));
            currentPhotoIndex = photoGallery.size() - 1;
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
