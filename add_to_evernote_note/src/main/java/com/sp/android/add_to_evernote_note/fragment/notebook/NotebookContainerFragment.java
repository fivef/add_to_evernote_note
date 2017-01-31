package com.sp.android.add_to_evernote_note.fragment.notebook;

import android.support.v4.app.FragmentTransaction;

import com.sp.android.add_to_evernote_note.R;
import com.sp.android.add_to_evernote_note.fragment.AbstractContainerFragment;
import com.sp.android.add_to_evernote_note.fragment.EmptyFragment;
import com.sp.android.add_to_evernote_note.task.FindNotebooksTask;
import com.sp.android.add_to_evernote_note.task.FindTagsTask;
import com.sp.android.add_to_evernote_note.util.ViewUtil;
import com.evernote.edam.type.Notebook;

import net.vrallev.android.task.TaskResult;

import java.util.List;

/**
 * @author rwondratschek
 */
public class NotebookContainerFragment extends AbstractContainerFragment {

    @Override
    protected void loadData() {
        new FindTagsTask().start(this, "personal");
        new FindNotebooksTask().start(this, "personal");
    }

    @Override
    public void onFabClick() {
        new CreateNotebookDialogFragment().show(getChildFragmentManager(), CreateNotebookDialogFragment.TAG);
    }

    @TaskResult(id = "personal")
    public void onFindNotebooks(List<Notebook> notebooks) {
        mSwipeRefreshLayout.setRefreshing(false);

        if (notebooks == null || notebooks.isEmpty()) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, EmptyFragment.create("notebooks"))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, NotebookListFragment.create(notebooks))
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }
    }

    @TaskResult
    public void onCreateNewNotebook(Notebook notebook) {
        // called from CreateNoteDialogFragment
        if (notebook != null) {
            refresh();
        } else {
            ViewUtil.showSnackbar(mSwipeRefreshLayout, "Create notebook failed");
        }
    }
}
