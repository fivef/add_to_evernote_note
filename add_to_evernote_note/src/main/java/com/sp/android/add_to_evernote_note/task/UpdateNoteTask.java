package com.sp.android.add_to_evernote_note.task;

import android.util.Log;

import com.sp.android.add_to_evernote_note.R;
import com.sp.android.add_to_evernote_note.util.ViewUtil;
import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteClientFactory;
import com.evernote.client.android.asyncclient.EvernoteHtmlHelper;
import com.evernote.client.android.asyncclient.EvernoteNoteStoreClient;
import com.evernote.client.android.type.NoteRef;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.type.Note;
import com.evernote.thrift.TException;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @author rwondratschek
 */
public class UpdateNoteTask extends BaseTask<String> {

    private final NoteRef mNoteRef;
    private final String m_string_to_append;

    public UpdateNoteTask(NoteRef noteRef, String string_to_append) {
        super(String.class);
        mNoteRef = noteRef;
        m_string_to_append = string_to_append;
    }

    @Override
    protected String checkedExecute() throws Exception {

        Note note = mNoteRef.loadNote(true, false, false, false);

        String content = note.getContent();

        Log.w("evernote_demo", "content= " + content);

        String first_part = content.substring(0, content.length()-10);
        String second_part = content.substring(content.length()-10);

        Log.w("evernote_demo", "first_part= " + first_part);
        Log.w("evernote_demo", "second_part= " + second_part);


        boolean add_empty_line = true;

        String empty_line = "";

        if (add_empty_line){
            empty_line = "<div><br /></div>";
        }

        if(m_string_to_append != null && m_string_to_append != "") {

            String html_string_to_append = stringToHTML(m_string_to_append);

            String resulting_content = first_part + empty_line + html_string_to_append + second_part;

            Log.w("evernote_demo", "resulting_content= " + resulting_content);

            //TODO: somehow the resulting_content should be checked for enml confirmity (see bottom of https://dev.evernote.com/doc/articles/enml.php)

            note.setContent(resulting_content);


            try {
                Note updated_note = updateNote(note);
            } catch (EDAMUserException e) {
                e.printStackTrace();
                return null;
            } catch (EDAMSystemException e) {
                e.printStackTrace();
                return null;
            } catch (TException e) {
                e.printStackTrace();
                return null;
            } catch (EDAMNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            Log.w("evernote_demo", "appended to note: " + m_string_to_append);

        }else{
            Log.w("evernote_demo", "No Data received, note was not updated!");
        }


        EvernoteClientFactory clientFactory = EvernoteSession.getInstance().getEvernoteClientFactory();

        EvernoteHtmlHelper htmlHelper;
        if (mNoteRef.isLinked()) {
            htmlHelper = clientFactory.getLinkedHtmlHelper(mNoteRef.loadLinkedNotebook());
        } else {
            htmlHelper = clientFactory.getHtmlHelperDefault();
        }

        Response response = htmlHelper.downloadNote(mNoteRef.getGuid());
        //return htmlHelper.parseBody(response);
        return note.getContent();

    }

    public static String stringToHTML(String input){

        //replace newlines with </div><div> and append <div> at the beginning and </div> at the end of the sting
        String output_string = "<div>" + input.replaceAll("(\r\n|\n)", "</div><div>") + "</div>";

        //add <br /> inside empty divs to correctly handle blank lines
        output_string = output_string.replaceAll("<div></div>", "<div><br /></div>");

        return output_string;
    }

    protected Note updateNote(Note note) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException
    {

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        return noteStoreClient.updateNote(note);

    }


    public NoteRef getNoteRef() {
        return mNoteRef;
    }
}
