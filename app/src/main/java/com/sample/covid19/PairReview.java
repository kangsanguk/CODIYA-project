package com.sample.covid19;
import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PairReview extends AppCompatActivity implements View.OnClickListener {
    private FirebaseStorage storage;
    private FirebaseDatabase database;


    Button btn_Insert, b;

    ImageView imageView;
    private static final int GALLERY_CODE = 11;
    private String imagePath;

    EditText e1, e2;


    private List<FirebasePost> fires = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
        Log.i("TAG", "Bucket = " + opts.getStorageBucket());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pair);

        /*권한*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);
        }

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();

        btn_Insert = (Button) findViewById(R.id.btn_insert2);
        btn_Insert.setOnClickListener(this);
        b = (Button)findViewById(R.id.b1);
        b.setOnClickListener(this);

        imageView = (ImageView) findViewById(R.id.img2);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        e1 = (EditText) findViewById(R.id.n1);
        e2 = (EditText) findViewById(R.id.n2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_insert2:
                try{
                    upload(imagePath);
                    Toast.makeText(getApplicationContext(), "등록 완료!", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "입력을 하지 않은 항목이 있습니다!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            case R.id.b1:
                Intent i = new Intent(this, PairActivity.class);
                startActivity(i);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_CODE) {

            imagePath = getPath(data.getData());
            File f = new File(imagePath);
            imageView.setImageURI(Uri.fromFile(f));
        }
    }
    public String getPath(Uri uri){
        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }

    private void upload(String uri){
        StorageReference StorageRef = storage.getReferenceFromUrl("gs://covid19-715b1.appspot.com");

        Uri file = Uri.fromFile(new File(uri));
        StorageReference riversRef = StorageRef.child("pair/"+file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> d = taskSnapshot.getStorage().getDownloadUrl();
                while (!d.isSuccessful());
                Uri downloadUrl = d.getResult();
                PairPost f = new PairPost();
                f.imageUrl = String.valueOf(downloadUrl);
                f.name = e1.getText().toString();
                f.content = e2.getText().toString();
                database.getReference().child("pair").push().setValue(f);
            }

        });
    }
}
