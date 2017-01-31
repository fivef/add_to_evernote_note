package com.sp.android.add_to_evernote_note.task;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.asyncclient.EvernoteSearchHelper;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwondratschek
 */
public class FindNotesTask extends BaseTask<List<NoteRef>> {

    private final EvernoteSearchHelper.Search mSearch;

    @SuppressWarnings("unchecked")
    public FindNotesTask(int offset, int maxNotes, @Nullable Notebook notebook, @Nullable LinkedNotebook linkedNotebook, @Nullable String query) {
        super((Class) List.class);

        NoteFilter noteFilter = new NoteFilter();
        noteFilter.setOrder(NoteSortOrder.UPDATED.getValue());

        if (!TextUtils.isEmpty(query)) {
            noteFilter.setWords(query);
        }

        if (notebook != null) {
            noteFilter.setNotebookGuid(notebook.getGuid());
        }

        //TODO: let the user choose a tag he wants here we just use favorit as tag to filter by
        List<String> list = new ArrayList();
        list.add("03750a45-c107-4e8b-afbf-29fa22777244");
        noteFilter.setTagGuids(list);
        //noteFilter.setTagGuids();

        mSearch = new EvernoteSearchHelper.Search()
                .setOffset(offset)
                .setMaxNotes(maxNotes)
                .setNoteFilter(noteFilter);

        if (linkedNotebook != null) {
            mSearch.addLinkedNotebook(linkedNotebook);
        } else {
            mSearch.addScope(EvernoteSearchHelper.Scope.PERSONAL_NOTES);
        }

    }

    @Override
    protected List<NoteRef> checkedExecute() throws Exception {
        EvernoteSearchHelper.Result searchResult = EvernoteSession.getInstance()
                .getEvernoteClientFactory()
                .getEvernoteSearchHelper()
                .execute(mSearch);

        return searchResult.getAllAsNoteRef();
    }
}
