package com.example.note_trial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {
Intent data;
EditText noteTitle,noteContent;
FloatingActionButton saveEdit;
FirebaseAuth auth;
FirebaseDatabase database;
DatabaseReference reference;
FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        noteTitle=findViewById(R.id.edittitleofnote);
        noteContent=findViewById(R.id.editcontentofnote);
        saveEdit=findViewById(R.id.saveeditnote);

        data=getIntent();
        database = FirebaseDatabase.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        String userId= user.getUid();

        Toolbar toolbar=findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newtitle=noteTitle.getText().toString();
                String newcontent=noteContent.getText().toString();

                if(newtitle.isEmpty()||newcontent.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Something is empty",Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {//
                    String noteID = data.getStringExtra("noteID");
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes/"+userId);
                    Map<String ,Object> note= new HashMap<>();
                    note.put("title",newtitle);
                    note.put("content",newcontent);

                    reference.child(noteID).setValue(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditNoteActivity.this, "Note is updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditNoteActivity.this,NotesActivity.class));

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditNoteActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();



                                }
                            });
                }

            }
        });
        String notetitle=data.getStringExtra("title");
        String notecontent=data.getStringExtra("content");
        noteContent.setText(notecontent);
        noteTitle.setText(notetitle);


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}