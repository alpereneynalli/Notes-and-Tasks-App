package tr.project.notesapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import tr.project.notesapp.fragments.NotesFragment;
import tr.project.notesapp.fragments.TasksFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NotesFragment();
            case 1:
                return new TasksFragment();
            default:
                return new NotesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
