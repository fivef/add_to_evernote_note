package com.sp.android.add_to_evernote_note.task;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;

import java.util.List;

/**
 * @author rwondratschek
 */
public class FindTagsTask extends BaseTask<List<Tag>> {

    @SuppressWarnings("unchecked")
    public FindTagsTask() {
        super((Class) List.class);
    }

    @Override
    protected List<Tag> checkedExecute() throws Exception {
        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();

        List<Tag> tags = noteStoreClient.listTags();
        return tags;
    }
}
