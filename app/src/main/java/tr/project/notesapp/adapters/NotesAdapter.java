package tr.project.notesapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;

import tr.project.notesapp.R;
import tr.project.notesapp.activities.EditNoteActivity;
import tr.project.notesapp.models.Note;
import tr.project.notesapp.utils.Utils;

public class NotesAdapter extends FirestoreRecyclerAdapter<Note, NotesAdapter.NoteHolder> {

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Context context;

    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {

        String documentId = getSnapshots().getSnapshot(position).getId();
        holder.tv_title.setText(model.getTitle());
        holder.tv_content.setText(model.getContent());
        holder.tv_date.setText(model.getDate());
        holder.tv_content.setVisibility(View.VISIBLE);
        holder.tv_date.setVisibility(View.VISIBLE);
        holder.lock.setVisibility(View.VISIBLE);

        if (model.isPasswordBool()) {
            holder.tv_content.setVisibility(View.GONE);
            holder.tv_date.setVisibility(View.GONE);
        } else {
            holder.lock.setVisibility(View.GONE);
        }

        holder.note_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), EditNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", model.getTitle());
                bundle.putString("content", model.getContent());
                bundle.putString("date", model.getDate());
                bundle.putString("docId", documentId);
                bundle.putBoolean("editmode", false);
                bundle.putBoolean("passwordBool", model.isPasswordBool());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (model.isPasswordBool()) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.noteclickdialog);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialogbox);
                    AppCompatButton open = dialog.findViewById(R.id.openNoteBtn);
                    AppCompatButton back = dialog.findViewById(R.id.backNoteBtn);
                    EditText password = dialog.findViewById(R.id.et_noteOpenPassword);

                    open.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String passwordStr = password.getText().toString().trim();
                            try {
                                if (!model.getPassword().equals(Utils.encryptString(passwordStr))) {
                                    Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    bundle.putString("password", passwordStr);
                                    intent.putExtras(bundle);
                                    view.getContext().startActivity(intent);
                                }
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
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
                    bundle.putString("password", "");
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
            }
        });

        holder.note_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        view.getContext(), R.style.BottomSheetDialogTheme);

                View bottomSheetView = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.bottom_sheet_layout,
                                (LinearLayout) view.findViewById(R.id.hold_options));

                LinearLayout deleteBtn = bottomSheetView.findViewById(R.id.deleteLayout);
                LinearLayout editBtn = bottomSheetView.findViewById(R.id.editLayout);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DocumentReference documentReference = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("userNotes").document(documentId);

                        if (model.isPasswordBool()) {
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.noteclickdialog);
                            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialogbox);
                            AppCompatButton delete = dialog.findViewById(R.id.openNoteBtn);
                            delete.setText("DELETE");
                            AppCompatButton back = dialog.findViewById(R.id.backNoteBtn);
                            EditText password = dialog.findViewById(R.id.et_noteOpenPassword);


                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String passwordStr = password.getText().toString().trim();
                                    try {
                                        if (!model.getPassword().equals(Utils.encryptString(passwordStr))) {
                                            Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog.dismiss();
                                            deleteDocumentReference(documentReference);
                                        }
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
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
                            deleteDocumentReference(documentReference);
                        }
                        bottomSheetDialog.dismiss();
                    }
                });

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        Intent intent = new Intent(view.getContext(), EditNoteActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", model.getTitle());
                        bundle.putString("content", model.getContent());
                        bundle.putString("date", model.getDate());
                        bundle.putString("docId", documentId);
                        bundle.putBoolean("editmode", true);
                        bundle.putBoolean("passwordBool", model.isPasswordBool());

                        if (model.isPasswordBool()) {
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.noteclickdialog);
                            dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialogbox);
                            AppCompatButton open = dialog.findViewById(R.id.openNoteBtn);
                            AppCompatButton back = dialog.findViewById(R.id.backNoteBtn);
                            EditText password = dialog.findViewById(R.id.et_noteOpenPassword);

                            open.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String passwordStr = password.getText().toString().trim();
                                    try {
                                        if (!model.getPassword().equals(Utils.encryptString(passwordStr))) {
                                            Toast.makeText(context, "Wrong password!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            dialog.dismiss();
                                            bundle.putString("password", passwordStr);
                                            intent.putExtras(bundle);
                                            view.getContext().startActivity(intent);
                                        }
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
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
                            bundle.putString("password", "");
                            intent.putExtras(bundle);
                            view.getContext().startActivity(intent);
                        }
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                return true;
            }
        });

    }

    private void deleteDocumentReference(DocumentReference documentReference) {
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "FAILED TO DELETE!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,
                parent, false);

        return new NoteHolder(view);
    }


    class NoteHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_content;
        TextView tv_date;
        ImageView lock;
        LinearLayout note_layout;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_noteCardTitle);
            tv_content = itemView.findViewById(R.id.tv_noteCardContentPreview);
            tv_date = itemView.findViewById(R.id.tv_noteCardDate);
            lock = itemView.findViewById(R.id.img_noteLock);
            note_layout = itemView.findViewById(R.id.note_layout);
        }
    }
}
