package com.example.faceauthentication.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.faceauthentication.MainActivity;
import com.example.faceauthentication.R;
import com.example.faceauthentication.model.ItemModel;

import java.util.List;
public class UserAdapter extends ArrayAdapter<ItemModel> {

    private Context mContext;
    private int mResource;

    public UserAdapter(@NonNull Context context, int resource, @NonNull List<ItemModel> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        String name = getItem(position).getName();
        Bitmap image = getItem(position).getImage();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        // Lookup view for data population
        ImageView imageView = convertView.findViewById(R.id.item_image);
        TextView textView = convertView.findViewById(R.id.item_name);
        Button button = convertView.findViewById(R.id.item_button);

        // Populate the data into the template view using the data object
        imageView.setImageBitmap(image);
        textView.setText(name);

        // Handle button click
        button.setOnClickListener(v -> {
            ((MainActivity)mContext).callVerification(name.replaceAll(".png",""));
            //Toast.makeText(mContext, "Button clicked for: " + name.replaceAll(".png",""), Toast.LENGTH_SHORT).show();
        });

        // Return the completed view to render on screen
        return convertView;
    }
}

