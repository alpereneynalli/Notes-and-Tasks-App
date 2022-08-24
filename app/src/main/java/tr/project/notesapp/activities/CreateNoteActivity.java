package tr.project.notesapp.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
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
    private TextView notePasswordBtn;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private boolean password;
    private String passwordHash = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        backbtn = findViewById(R.id.img_backButtonEditNote);
        saveBtn = findViewById(R.id.img_saveButtonCreateNote);
        notePasswordBtn = findViewById(R.id.tv_notePasswordBtn);
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

        notePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!password) {
                    password = true;
                    notePasswordBtn.setText("NOTE PASSWORD\nON");
                    notePasswordBtn.setTextColor(getResources().getColor(R.color.titleText));
                } else {
                    password = false;
                    notePasswordBtn.setText("NOTE PASSWORD\nOFF");
                    notePasswordBtn.setTextColor(getResources().getColor(R.color.darkTextGrey));
                }
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

                    if (password) {
                        Dialog dialog = new Dialog(CreateNoteActivity.this);
                        dialog.setContentView(R.layout.notepassworddialog);
                        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialogbox);
                        AppCompatButton save = dialog.findViewById(R.id.saveNotePasswordBtn);
                        AppCompatButton back = dialog.findViewById(R.id.saveNotePasswordBackBtn);
                        EditText password0 = dialog.findViewById(R.id.et_notePassword);
                        EditText password1 = dialog.findViewById(R.id.et_notePasswordAgain);

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String password0Str = password0.getText().toString().trim();
                                String password1Str = password1.getText().toString().trim();

                                if (password0Str.isEmpty() || password1Str.isEmpty()) {
                                    Toast.makeText(CreateNoteActivity.this, "Please fill both password fields.", Toast.LENGTH_SHORT).show();
                                } else if (!password0Str.equals(password1Str)) {
                                    Toast.makeText(CreateNoteActivity.this, "Passwords are not the same.", Toast.LENGTH_SHORT).show();
                                } else {
                                    try {
                                        passwordHash = Utils.encryptString(password0Str);
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    }
                                    note.put("password", passwordHash);
                                    note.put("passwordBool", true);
                                    addNoteDocument(documentReference, note);
                                }
                            }
                        });

                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    } else {
                        note.put("password", passwordHash);
                        note.put("passwordBool", false);
                        addNoteDocument(documentReference, note);
                    }
                }
            }
        });
    }

    private void addNoteDocument(DocumentReference documentReference, Map<String, Object> note){
        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(CreateNoteActivity.this, NotesAndTasksActivity.class);
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