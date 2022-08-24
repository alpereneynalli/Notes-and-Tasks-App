package tr.project.notesapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import tr.project.notesapp.R;
import tr.project.notesapp.activities.CreateNoteActivity;
import tr.project.notesapp.adapters.NotesAdapter;
import tr.project.notesapp.models.Note;

public class NotesFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private View view;
    private ImageView addNoteButton;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private NotesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_notes, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        addNoteButton = view.findViewById(R.id.img_addNoteButton);

        Query query = firebaseFirestore.collection("notes").document(firebaseUser.getUid()).collection("userNotes").orderBy("dateObj", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Note> userNotes = new FirestoreRecyclerOptions.Builder<Note>().setQuery(query, Note.class).build();

        adapter = new NotesAdapter(userNotes, getContext());
        recyclerView = view.findViewById(R.id.recycler_view_notes);
        recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateNoteActivity.class);
                startActivity(intent);
            }
        });


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}