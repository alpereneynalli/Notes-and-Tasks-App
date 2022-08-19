package tr.project.notesapp.utils;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import tr.project.notesapp.R;
import tr.project.notesapp.adapters.TaskAdapter;

public class TasksTouchHelper extends ItemTouchHelper.SimpleCallback {

    private TaskAdapter adapter;

    public TasksTouchHelper(TaskAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position = viewHolder.getBindingAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            Dialog dialog = Utils.deleteDialog(adapter.taskAdapterContext(), "Are you sure you want to delete this task?");
            AppCompatButton yes = dialog.findViewById(R.id.delete_yes_button);
            AppCompatButton no = dialog.findViewById(R.id.delete_no_button);

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.deleteTask(position);
                    dialog.dismiss();
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_forever_24)
                .addSwipeLeftBackgroundColor(Color.RED)
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

}
