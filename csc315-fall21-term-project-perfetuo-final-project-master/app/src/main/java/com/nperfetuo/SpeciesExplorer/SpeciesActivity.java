package com.nperfetuo.SpeciesExplorer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SpeciesActivity extends AppCompatActivity {

    private static final String TAG = "SpeciesActivity";
    public static final String COMMON_NAME = "commonName";
    public static final String SCIENTIFIC_NAME = "scientificName";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE_FILE = "imageFile";
    public static final String SEEN = "seen";

    private final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public String commonName;
    public String scientificName;
    public String description;
    public String imageFile;
    public boolean seen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);
        Intent intent = getIntent();
        commonName = intent.getStringExtra(COMMON_NAME);
        scientificName = intent.getStringExtra(SCIENTIFIC_NAME);
        description = intent.getStringExtra(DESCRIPTION);
        imageFile = intent.getStringExtra(IMAGE_FILE);
        seen = intent.getBooleanExtra(SEEN, false);
        TextView commonNameTextView = findViewById(R.id.common_name);
        TextView scientificNameTextView = findViewById(R.id.scientific_name);
        TextView descriptionTextView = findViewById(R.id.description);
        ImageView mainImage = findViewById(R.id.main_image);
        commonNameTextView.setText(commonName);
        scientificNameTextView.setText(scientificName);
        descriptionTextView.setText(description);
        StorageReference image = mStorageRef.child(imageFile);
        GlideApp.with(this)
                .load(image)
                .into(mainImage);
    }
}
