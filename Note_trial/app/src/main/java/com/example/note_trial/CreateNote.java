package com.example.note_trial;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.note_trial.databinding.ActivityCreateNoteBinding;
import com.example.note_trial.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateNote extends AppCompatActivity {
    EditText createTitle,createContent;
    FloatingActionButton saveNote;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    ProgressBar progressBar;
    ActivityCreateNoteBinding binding;
    Model model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        binding = DataBindingUtil.setContentView(this,R.layout.activity_create_note);
        Toolbar toolbar=findViewById(R.id.toolbarofcreatenote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=findViewById(R.id.progressbarofcreatenote);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user =FirebaseAuth.getInstance().getCurrentUser();
        String userId= user.getUid();
        saveNote=findViewById(R.id.savenote);

        saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.createtitleofnote.getText().toString();
                String content = binding.createcontentofnote.getText().toString();

                if(title.isEmpty() || content.isEmpty())
                {
                    Toast.makeText(CreateNote.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                   //Reference
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes/"+userId);
                    Map<String ,Object> note= new HashMap<>();
                    note.put("title",title);
                    note.put("content",content);

                    reference.push().setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreateNote.this, "Note created successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateNote.this,NotesActivity.class));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                          //  Toast.makeText(getApplicationContext(),"Failed To Create Note",Toast.LENGTH_SHORT).show();
                            Toast.makeText(CreateNote.this, "Failed To Create Note", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);


                        }
                    });
                }
            }
        });

    }
}