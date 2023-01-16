package com.example.greenbru;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class MyAdapter extends FirebaseRecyclerAdapter<AlertsHelperClass, MyAdapter.myviewholder>
{

    Button go_to_specific_item;
    public MyAdapter(@NonNull FirebaseRecyclerOptions<AlertsHelperClass> options) {
        super(options);
    }

    //We link the helper and the xml
    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull AlertsHelperClass model) {

        holder.nametext.setText(model.getTitle());
        holder.coursetext.setText(model.getDescription());
        Glide.with(holder.img1.getContext()).load(model.getImageURL()).into(holder.img1);
        //Pour montrer le individuele holder
       /* go_to_specific_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()

                        .replace(R.id.wrapper, new descfrag(model.getTitle(), model.getDescription(), model.getImageURL()))
                        .addToBackStack(null)
                        .commit();

            }
        });
        */

    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign, parent, false);
        myviewholder myViewHolder = new myviewholder(view);

        view.findViewById(R.id.select_item_row).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We show the selected item
                int position = myViewHolder.getAdapterPosition();
                AlertsHelperClass model = getItem(position);
                AppCompatActivity activity = (AppCompatActivity)v.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wrapper, new descfrag(model.getTitle(), model.getDescription(), model.getImageURL()))
                        .addToBackStack(null)
                        .commit();
                //We hide the list of fragments to show the fragment that we selected
                View fragmentToHide = activity.findViewById(R.id.recview);
                fragmentToHide.setVisibility(View.GONE);
            }
        });
        return myViewHolder;
    }

    public class myviewholder extends RecyclerView.ViewHolder
    {
        ImageView img1;
        TextView nametext, coursetext;
        public myviewholder(@NonNull View itemView) {
            super(itemView);

            img1 = itemView.findViewById(R.id.img1);
            nametext = itemView.findViewById(R.id.nametext);
            coursetext = itemView.findViewById(R.id.coursetext);
        }
    }
}
