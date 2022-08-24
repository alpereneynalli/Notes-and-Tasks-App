package tr.project.notesapp.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import tr.project.notesapp.R;
import tr.project.notesapp.models.Task;

public class TaskAdapter extends FirestoreRecyclerAdapter<Task, TaskAdapter.TaskHolder> {

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Context context;


    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Task> options, Context context) {
        super(options);
        this.context = context;
    }


    @Override
    protected void onBindViewHolder(@NonNull TaskHolder holder, int position, @NonNull Task model) {

        String taskId = getSnapshots().getSnapshot(position).getId();
        Calendar calendar = Calendar.getInstance();

        holder.taskName.setText(model.getTaskName());
        holder.checkBox.setChecked(toBoolean(model.getStatus()));
        holder.taskDueDate.setText("Due on " + model.getDueDate());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("userTasks").document(taskId).update("status", 1);
                } else {
                    firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("userTasks").document(taskId).update("status", 0);

                }
            }
        });

        holder.task_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        view.getContext(), R.style.BottomSheetDialogTheme);

                View bottomSheetView = LayoutInflater.from(view.getContext())
                        .inflate(R.layout.add_task_layout,
                                (LinearLayout) view.findViewById(R.id.add_task_layout));

                EditText taskName = bottomSheetView.findViewById(R.id.et_taskName);
                TextView setDueDate = bottomSheetView.findViewById(R.id.tv_setDueDate);
                TextView saveTask = bottomSheetView.findViewById(R.id.tv_saveTask);

                taskName.setText(model.getTaskName());
                setDueDate.setText(model.getDueDate());

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
                            }
                        }, YEAR, MONTH, DAY);
                        datePickerDialog.show();
                    }
                });

                saveTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DocumentReference documentReference = firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("userTasks").document(taskId);
                        Map<String, Object> task = new HashMap<>();
                        task.put("taskName", taskName.getText().toString().trim());
                        task.put("dueDate", setDueDate.getText().toString().trim());
                        task.put("status", model.getStatus());
                        task.put("date", calendar.getTime());

                        documentReference.set(task).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                bottomSheetDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to save the note. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                return true;
            }
        });
    }

    private boolean toBoolean(int status) {
        return status != 0;
    }


    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,
                parent, false);


        return new TaskHolder(view);
    }

    public void deleteTask(int position) {
        String taskId = getSnapshots().getSnapshot(position).getId();
        DocumentReference documentReference = firebaseFirestore.collection("tasks").document(firebaseUser.getUid()).collection("userTasks").document(taskId);
        documentReference.delete();
    }

    public Context taskAdapterContext() {
        return context;
    }


    class TaskHolder extends RecyclerView.ViewHolder {

        MaterialCheckBox checkBox;
        TextView taskDueDate;
        TextView taskName;
        LinearLayout task_layout;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.taskBox);
            taskDueDate = itemView.findViewById(R.id.tv_taskDueDate);
            task_layout = itemView.findViewById(R.id.task_layout);
            taskName = itemView.findViewById(R.id.taskName);
        }
    }
}
