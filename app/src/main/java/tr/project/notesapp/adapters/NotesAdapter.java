package tr.project.notesapp.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import tr.project.notesapp.R;
import tr.project.notesapp.activities.EditNoteActivity;
import tr.project.notesapp.models.Note;

public class NotesAdapter extends FirestoreRecyclerAdapter<Note, NotesAdapter.NoteHolder> {

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public NotesAdapter(@NonNull FirestoreRecyclerOptions<Note> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull NoteHolder holder, int position, @NonNull Note model) {

        String documentId = getSnapshots().getSnapshot(position).getId();

        holder.tv_title.setText(model.getTitle());
        holder.tv_content.setText(model.getContent());
        holder.tv_date.setText(model.getDate());

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
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
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

                        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "FAILED TO DELETE!", Toast.LENGTH_SHORT).show();
                            }
                        });
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
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                return true;
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
        LinearLayout note_layout;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_noteCardTitle);
            tv_content = itemView.findViewById(R.id.tv_noteCardContentPreview);
            tv_date = itemView.findViewById(R.id.tv_noteCardDate);
            note_layout = itemView.findViewById(R.id.note_layout);
        }
    }
}
