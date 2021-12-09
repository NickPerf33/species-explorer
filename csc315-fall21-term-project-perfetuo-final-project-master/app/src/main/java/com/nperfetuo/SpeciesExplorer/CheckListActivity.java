package com.nperfetuo.SpeciesExplorer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CheckListActivity extends AppCompatActivity {

    private static final String TAG = "CheckListActivity";
    public static final String CURRENT_USER = "currentUser";
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private String currentUser;
    private ArrayList<String> seenList;

    private ArrayAdapter<Species> frog_adapter;
    private ArrayAdapter<Species> snake_adapter;
    private ArrayAdapter<Species> lizard_adapter;
    private ArrayAdapter<Species> salamander_adapter;
    private ArrayAdapter<Species> turtle_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        if (savedInstanceState != null) {
            currentUser = savedInstanceState.getString("currentUser");
        }
        Intent intent = getIntent();
        currentUser = intent.getStringExtra(CURRENT_USER);

        ListView frogsListView = findViewById(R.id.frogsListView);
        ListView snakesListView = findViewById(R.id.snakesListView);
        ListView lizardsListView = findViewById(R.id.lizardsListView);
        ListView salamandersListView = findViewById(R.id.salamandersListView);
        ListView turtlesListView = findViewById(R.id.turtlesListView);
        frog_adapter = new SpeciesAdapter(this, new ArrayList<Species>());
        snake_adapter = new SpeciesAdapter(this, new ArrayList<Species>());
        lizard_adapter = new SpeciesAdapter(this, new ArrayList<Species>());
        salamander_adapter = new SpeciesAdapter(this, new ArrayList<Species>());
        turtle_adapter = new SpeciesAdapter(this, new ArrayList<Species>());
        frogsListView.setAdapter(frog_adapter);
        snakesListView.setAdapter(snake_adapter);
        lizardsListView.setAdapter(lizard_adapter);
        salamandersListView.setAdapter(salamander_adapter);
        turtlesListView.setAdapter(turtle_adapter);

        mDb.collection("amphibians")
                .whereEqualTo("order", "frog")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Species> frogs = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Species s = document.toObject(Species.class);
                        frogs.add(s);
                    }
                    frog_adapter.clear();
                    frog_adapter.addAll(frogs);
                });
        mDb.collection("reptiles")
                .whereEqualTo("order", "snake")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Species> snakes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Species s = document.toObject(Species.class);
                        snakes.add(s);
                    }
                    snake_adapter.clear();
                    snake_adapter.addAll(snakes);
                });
        mDb.collection("reptiles")
                .whereEqualTo("order", "lizard")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Species> lizards = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Species s = document.toObject(Species.class);
                        lizards.add(s);
                    }
                    lizard_adapter.clear();
                    lizard_adapter.addAll(lizards);
                });
        mDb.collection("amphibians")
                .whereEqualTo("order", "salamander")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Species> salamanders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Species s = document.toObject(Species.class);
                        salamanders.add(s);
                    }
                    salamander_adapter.clear();
                    salamander_adapter.addAll(salamanders);
                });
        mDb.collection("reptiles")
                .whereEqualTo("order", "turtle")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Species> turtles = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Species s = document.toObject(Species.class);
                        turtles.add(s);
                    }
                    turtle_adapter.clear();
                    turtle_adapter.addAll(turtles);
                });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("currentUser", currentUser);
    }

    class SpeciesAdapter extends ArrayAdapter<Species> {

        ArrayList<Species> species;
        DocumentReference docRef = mDb.collection("users").document(currentUser);

        SpeciesAdapter(Context context, ArrayList<Species> species) {
            super(context, 0, species);
            this.species = species;
            userSeen();
        }

        public void userSeen() {
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot retrieved");
                        Map<String, Object> userSeen = document.getData();
                        seenList = (ArrayList<String>) userSeen.get("seen");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.species_list_item, parent, false);
            }

            CheckBox seen = convertView.findViewById(R.id.seen_checkbox);
            TextView speciesName = convertView.findViewById(R.id.common_name);

            Species s = species.get(position);
            if (seenList.contains(s.getCommonName())) {
                seen.setChecked(true);
            }
            seen.setOnCheckedChangeListener((compoundButton, b) -> {
                if (seen.isChecked()) {
                    docRef.update("seen", FieldValue.arrayUnion(s.getCommonName()));
                    Log.d(TAG, "checked");
                    Toast.makeText(CheckListActivity.this, "Marked as seen", Toast.LENGTH_SHORT).show();
                } else {
                    docRef.update("seen", FieldValue.arrayRemove(s.getCommonName()));
                    Log.d(TAG, "unchecked");
                    Toast.makeText(CheckListActivity.this, "Removed from seen", Toast.LENGTH_SHORT).show();
                }
            });
            speciesName.setText(s.getCommonName());
            speciesName.setOnClickListener(view -> startActivity(new Intent(CheckListActivity.this, SpeciesActivity.class)
                    .putExtra(SpeciesActivity.COMMON_NAME, s.getCommonName())
                    .putExtra(SpeciesActivity.SCIENTIFIC_NAME, s.getScientificName())
                    .putExtra(SpeciesActivity.DESCRIPTION, s.getDescription())
                    .putExtra(SpeciesActivity.IMAGE_FILE, s.getImageFile())
                    .putExtra(SpeciesActivity.SEEN, s.getSeen())));

            return convertView;
        }
    }
}
