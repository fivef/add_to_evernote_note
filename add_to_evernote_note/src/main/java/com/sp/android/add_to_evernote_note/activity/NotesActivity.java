package com.sp.android.add_to_evernote_note.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.sp.android.add_to_evernote_note.R;
import com.sp.android.add_to_evernote_note.fragment.note.NoteContainerFragment;
import com.sp.android.add_to_evernote_note.task.BaseTask;
import com.evernote.client.android.EvernoteSession;
import com.evernote.edam.type.LinkedNotebook;
import com.evernote.edam.type.Notebook;

import net.vrallev.android.task.TaskResult;

/**
 * @author rwondratschek
 */
public class NotesActivity extends AppCompatActivity {

    private static final String KEY_NOTEBOOK = "KEY_NOTEBOOK";
    private static final String KEY_LINKED_NOTEBOOK = "KEY_LINKED_NOTEBOOK";



    public static Intent createIntent(Context context, Notebook notebook) {
        Intent intent = new Intent(context, NotesActivity.class);
        intent.putExtra(KEY_NOTEBOOK, notebook);
        return intent;
    }

    public static Intent createIntent(Context context, LinkedNotebook linkedNotebook) {
        Intent intent = new Intent(context, NotesActivity.class);
        intent.putExtra(KEY_LINKED_NOTEBOOK, linkedNotebook);
        return intent;
    }

    private Notebook mNotebook;
    private LinkedNotebook mLinkedNotebook;
    public String mReceivedString;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!EvernoteSession.getInstance().isLoggedIn()) {
            Log.w("evernote_demo", "No user is logged in. Starting main activity!");
            return;
        }


        setContentView(R.layout.activity_notes);

        mNotebook = (Notebook) getIntent().getSerializableExtra(KEY_NOTEBOOK);
        mLinkedNotebook = (LinkedNotebook) getIntent().getSerializableExtra(KEY_LINKED_NOTEBOOK);

        Resources resources = getResources();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(resources.getColor(R.color.tb_text));

        setSupportActionBar(toolbar);

        if (!isTaskRoot()) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (mNotebook != null) {
            getSupportActionBar().setTitle(mNotebook.getName());
        } else {
            if (mLinkedNotebook != null) {
                getSupportActionBar().setTitle(mLinkedNotebook.getShareName());
                new LoadNotebookNameTask(mLinkedNotebook).start(this, "notebookName");
            }
        }


        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        mReceivedString = null;

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    Log.w("evernote_demo", "Received intent with content " + sharedText);
                    mReceivedString = sharedText;
                }


            } else if (type.startsWith("image/")) {
                Log.w("evernote_demo", "Receiving images currently not supported");
                //handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                //handleSendMultipleImages(intent); // Handle multiple images being sent
                Log.w("evernote_demo", "Receiving multiple images currently not supported");
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, NoteContainerFragment.create(mNotebook, mLinkedNotebook, mReceivedString))
                    .commit();
        }




    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TaskResult(id = "notebookName")
    public void onNotebookName(String name) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(name);
    }

    private static final class LoadNotebookNameTask extends BaseTask<String> {

        private final LinkedNotebook mLinkedNotebook;

        private LoadNotebookNameTask(LinkedNotebook linkedNotebook) {
            super(String.class);
            mLinkedNotebook = linkedNotebook;
        }

        @Override
        protected String checkedExecute() throws Exception {
            return EvernoteSession.getInstance()
                    .getEvernoteClientFactory()
                    .getLinkedNotebookHelper(mLinkedNotebook)
                    .getCorrespondingNotebook()
                    .getName();
        }
    }
}
