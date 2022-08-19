package tr.project.notesapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tr.project.notesapp.R;
import tr.project.notesapp.utils.Utils;

public class CreateNoteActivity extends AppCompatActivity {

    private ImageView backbtn;
    private ImageView saveBtn;
    private EditText noteTitle;
    private EditText noteContent;
    private TextView noteDate;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        backbtn = findViewById(R.id.img_backButtonEditNote);
        saveBtn = findViewById(R.id.img_saveButtonCreateNote);
        noteTitle = findViewById(R.id.et_noteTitle);
        noteContent = findViewById(R.id.et_noteContent);
        noteDate = findViewById(R.id.tv_noteDate);

        Date date = new Date();
        String strDate = Utils.dateToString(date);
        noteDate.setText(strDate);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitle.getText().toString().trim();
                String content = noteContent.getText().toString().trim();

                if (title.isEmpty() || content.isEmpty()) {
                    Toast.makeText(CreateNoteActivity.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
                } else {

                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("userNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", title);
                    note.put("content", content);
                    note.put("date", strDate);
                    note.put("dateObj", date);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(CreateNoteActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateNoteActivity.this, "Failed to create note", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

}