package com.nperfetuo.SpeciesExplorer;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class SpeciesRecyclerAdapter extends FirestoreRecyclerAdapter<Species, SpeciesRecyclerAdapter.SpeciesViewHolder> {

    private final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final OnItemClickListener listener;

    SpeciesRecyclerAdapter(FirestoreRecyclerOptions<Species> options, OnItemClickListener listener) {
        super(options);
        this.listener = listener;
    }

    SpeciesRecyclerAdapter(FirestoreRecyclerOptions<Species> options) {
        super(options);
        this.listener = null;
    }

    class SpeciesViewHolder extends RecyclerView.ViewHolder {
        final CardView view;
        final TextView commonName;
        final ImageView imageFile;

        SpeciesViewHolder(CardView v) {
            super(v);
            view = v;
            commonName = v.findViewById(R.id.common_name);
            imageFile = v.findViewById(R.id.main_image);
        }
    }

    @Override
    public void onBindViewHolder(final SpeciesViewHolder holder, @NonNull int position, @NonNull final Species species) {
        holder.commonName.setText(species.getCommonName());
        StorageReference image = mStorageRef.child(species.getImageFile());

        GlideApp.with(holder.view.getContext())
                .load(image)
                .into(holder.imageFile);
        if (listener != null) {
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(holder.getAbsoluteAdapterPosition());
                }
            });
        }
    }

    @Override
    public SpeciesViewHolder onCreateViewHolder(ViewGroup parent,
                                                int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.species_list_item, parent, false);

        return new SpeciesViewHolder(v);
    }
}