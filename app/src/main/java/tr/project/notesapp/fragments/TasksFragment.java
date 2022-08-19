package tr.project.notesapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import tr.project.notesapp.R;
import tr.project.notesapp.adapters.TaskAdapter;
import tr.project.notesapp.models.Task;
import tr.project.notesapp.utils.TasksTouchHelper;


public class TasksFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private View view;
    private ImageView addTaskButton;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private String dueDate = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        addTaskButton = view.findViewById(R.id.img_addTaskButton);

        Query query = firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("userTasks").orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Task> userTasks = new FirestoreRecyclerOptions.Builder<Task>().setQuery(query, Task.class).build();

        adapter = new TaskAdapter(userTasks, getActivity());
        recyclerView = view.findViewById(R.id.recycler_view_to_do_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TasksTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        Calendar calendar = Calendar.getInstance();

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        view.getContext(), R.style.BottomSheetDialogTheme);

                View bottomSheetView = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.add_task_layout,
                                (LinearLayout) view.findViewById(R.id.add_task_layout));

                EditText taskName = bottomSheetView.findViewById(R.id.et_taskName);
                TextView setDueDate = bottomSheetView.findViewById(R.id.tv_setDueDate);
                TextView saveTask = bottomSheetView.findViewById(R.id.tv_saveTask);

                setDueDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int MONTH = calendar.get(Calendar.MONTH);
                        int YEAR = calendar.get(Calendar.YEAR);
                        int DAY = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(bottomSheetView.getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfWeek) {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                                calendar.set(year, month, dayOfWeek);
                                String dateString = sdf.format(calendar.getTime());
                                setDueDate.setText(dateString);
                                dueDate = dateString;
                            }
                        }, YEAR, MONTH, DAY);
                        datePickerDialog.show();
                    }
                });

                saveTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = taskName.getText().toString().trim();

                        if (name.isEmpty() || dueDate.isEmpty()) {
                            Toast.makeText(view.getContext(), "Please give a task name and set a due date.", Toast.LENGTH_SHORT).show();
                        } else {
                            DocumentReference documentReference = firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("userTasks").document();
                            Map<String, Object> task = new HashMap<>();
                            task.put("taskName", name);
                            task.put("dueDate", dueDate);
                            task.put("status", 0);
                            task.put("date", calendar.getTime());

                            documentReference.set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    bottomSheetDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Failed to create task", Toast.LENGTH_SHORT).show();
                                }
                            });

                            dueDate = "";
                        }
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
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