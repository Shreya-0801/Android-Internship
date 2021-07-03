package com.example.note_trial;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.example.note_trial.databinding.ActivityNotesBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {
    FloatingActionButton createNote;
    private FirebaseAuth auth;
    RecyclerView recyclerView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser firebaseUser;
    FirebaseDatabase database;
    DatabaseReference reference;
    ActivityNotesBinding binding;
    FirebaseRecyclerAdapter<Model,NoteViewHolder> noteAdapter;
    Model model;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_notes);
        auth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        String userId= firebaseUser.getUid();
        database = FirebaseDatabase.getInstance();
        getSupportActionBar().setTitle("All Notes");
        createNote=findViewById(R.id.createnotefab);
        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,CreateNote.class));
            }
        });
        //Query // reference
        reference= FirebaseDatabase.getInstance().getReference("notes/"+userId);
       // Query query =reference.child("notes").orderByChild("title");
        FirebaseRecyclerOptions<Model> allusernotes = new FirebaseRecyclerOptions.Builder<Model>().setQuery(reference,Model.class).build();
        noteAdapter = new FirebaseRecyclerAdapter<Model, NotesActivity.NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NotesActivity.NoteViewHolder holder, int position, @NonNull Model model) {
                ImageView popupbutton = holder.itemView.findViewById(R.id.menupopbutton);
                int colourcode = getRandomColor();
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colourcode,null));

                holder.notetitle.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());
//
                String docId=noteAdapter.getRef(position).getKey();

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(v.getContext(),notedetails.class);
                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("content",model.getContent());
                       intent.putExtra("noteId",docId);

                        v.getContext().startActivity(intent);



                    }
                });
                popupbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu= new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent= new Intent(v.getContext(),EditNoteActivity.class);
                                intent.putExtra("title",model.getTitle());
                                intent.putExtra("content",model.getContent());
                                intent.putExtra("noteID",docId);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes/"+userId);
                                reference.child(docId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(NotesActivity.this, "This note is deleted", Toast.LENGTH_SHORT).show();

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull  Exception e) {
                                        Toast.makeText(NotesActivity.this, "Failed to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });




            }



            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NotesActivity.NoteViewHolder(view);
            }
        };
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(noteAdapter);


    }


    public class NoteViewHolder extends RecyclerView.ViewHolder {
         TextView notetitle;
         TextView notecontent;
        LinearLayout mnote;
        public NoteViewHolder(@NonNull  View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.notetitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            mnote=itemView.findViewById(R.id.note);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout:
                auth.signOut();
                finish();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(noteAdapter!=null)
        {
            noteAdapter.stopListening();
        }
    }
    private int getRandomColor() {
        List<Integer> colorcode=new ArrayList<>();
        colorcode.add(R.color.gray);
        colorcode.add(R.color.pink);
        colorcode.add(R.color.lightgreen);
        colorcode.add(R.color.skyblue);
        colorcode.add(R.color.color1);
        colorcode.add(R.color.color2);
        colorcode.add(R.color.color3);

        colorcode.add(R.color.color4);
        colorcode.add(R.color.color5);
        colorcode.add(R.color.green);

        Random random=new Random();
        int number=random.nextInt(colorcode.size());
        return colorcode.get(number);
    }

}