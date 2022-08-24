package tr.project.notesapp.activities;

import android.os.Bundle;
import android.text.InputType;
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

public class EditNoteActivity extends AppCompatActivity {

    private ImageView img_backbtn;
    private TextView tv_editMode;
    private TextView tv_deleteNode;
    private TextView tv_saveNote;
    private EditText et_editNoteTitle;
    private EditText et_editNoteContent;
    private TextView tv_editNoteDate;
    private boolean editmode;
    private Bundle bundle;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        img_backbtn = findViewById(R.id.img_backButtonEditNote);
        tv_editMode = findViewById(R.id.tv_editMode);
        tv_deleteNode = findViewById(R.id.tv_deleteNote);
        tv_saveNote = findViewById(R.id.tv_saveNote);
        et_editNoteTitle = findViewById(R.id.et_editNoteTitle);
        et_editNoteContent = findViewById(R.id.et_editNoteContent);
        tv_editNoteDate = findViewById(R.id.tv_editNoteDate);

        bundle = getIntent().getExtras();
        tv_editNoteDate.setText(bundle.getString("date"));
        et_editNoteTitle.setText(bundle.getString("title"));
        et_editNoteContent.setText(bundle.getString("content"));
        editmode = bundle.getBoolean("editmode");
        setEditMode(editmode);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        img_backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        tv_editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditMode(!editmode);
            }
        });

        tv_saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newTitle = et_editNoteTitle.getText().toString().trim();
                String newContent = et_editNoteContent.getText().toString().trim();
                Date date = new Date();
                String strDate = Utils.dateToString(date);

                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("userNotes").document(bundle.getString("docId"));
                Map<String, Object> note = new HashMap<>();
                note.put("title", newTitle);
                note.put("content", newContent);
                note.put("date", strDate);
                note.put("dateObj", date);
                note.put("password", bundle.getString("password"));
                note.put("passwordBool", bundle.getBoolean("passwordBool"));

                editNoteDocument(documentReference, note);
            }
        });

        tv_deleteNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("userNotes").document(bundle.getString("docId"));
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "FAILED TO DELETE!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    private void setEditMode(Boolean mode) {
        if (mode) {
            tv_editMode.setText("EDIT MODE\nON");
            tv_editMode.setTextColor(getResources().getColor(R.color.titleText));
            et_editNoteTitle.setInputType(InputType.TYPE_CLASS_TEXT);
            et_editNoteTitle.setEnabled(true);
            et_editNoteContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            et_editNoteContent.setEnabled(true);
            editmode = true;
        } else {
            tv_editMode.setText("EDIT MODE\nOFF");
            tv_editMode.setTextColor(getResources().getColor(R.color.darkTextGrey));
            et_editNoteTitle.setInputType(InputType.TYPE_NULL);
            et_editNoteTitle.setEnabled(false);
            et_editNoteContent.setInputType(InputType.TYPE_NULL);
            et_editNoteContent.setEnabled(false);
            et_editNoteTitle.setSingleLine(false);
            et_editNoteContent.setSingleLine(false);
            editmode = false;
        }
    }

    private void editNoteDocument(DocumentReference documentReference, Map<String, Object> note){
        documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditNoteActivity.this, "Failed to save note", Toast.LENGTH_SHORT).show();
            }
        });
    }
}