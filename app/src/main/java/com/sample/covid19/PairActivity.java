package com.sample.covid19;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PairActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseDatabase database;

    List<FirebasePost> fires = new ArrayList<>();
    BoardAdapter2 arrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newlist);
        database = FirebaseDatabase.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayAdapter = new BoardAdapter2();
        recyclerView.setAdapter(arrayAdapter);

        database.getReference().child("pair").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fires.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    fires.add(get);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "데이터 로드 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    class BoardAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class CustomViewHolder extends RecyclerView.ViewHolder{
            TextView textView, textView2;
            ImageView imageView;

            public CustomViewHolder(View view) {
                super(view);
                textView = (TextView)view.findViewById(R.id.bt1);
                textView2 = (TextView)view.findViewById(R.id.bt2);

                imageView = (ImageView)view.findViewById(R.id.iv2);
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list2, parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((CustomViewHolder)holder).textView.setText(fires.get(position).name);
            ((CustomViewHolder)holder).textView2.setText(fires.get(position).content);

            Glide.with(holder.itemView.getContext()).load(fires.get(position).imageUrl).into(((CustomViewHolder)holder).imageView);
        }

        @Override
        public int getItemCount() {
            return fires.size();
        }
    }
}
