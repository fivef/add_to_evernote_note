package com.evernote.android.demo.task;

import android.util.Log;

import com.evernote.android.demo.R;
import com.evernote.android.demo.util.ViewUtil;
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

        //note.setContent(content + "\n" + m_string_to_append);


        /*
        StringWriter sw = new StringWriter();
        String updated_note_content = "";
        try {


            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(content));
            Document doc = builder.parse(is);

            // Get the root element
            Node data= doc.getFirstChild();

            Node actual_note_content = doc.getElementsByTagName("en-note").item(0);

            // I am not doing any thing with it just for showing you
            String acutal_note_content_string = actual_note_content.getTextContent();

            //TODO: check if acutal note content string is not empty, perhaps even recheck with the note to make sure not to delete the note

            actual_note_content.setTextContent(acutal_note_content_string + "<div></br></div><div>" + m_string_to_append + "</div>");
            //Element div = doc.createElement("div");
            //div.appendChild(doc.createTextNode()
            //Element br = doc.createElement(("br"));

            //actual_note_content.appendChild(div);

            //actual_note_content.appendChild("<div></br></div><div>" + m_string_to_append + "</div>");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "en-note");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://xml.evernote.com/pub/enml2.dtd");

            transformer.transform(new DOMSource(doc),new StreamResult(sw));

            updated_note_content = sw.toString();

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        */


        boolean add_empty_line = true;

        String empty_line = "";

        if (add_empty_line){
            empty_line = "<div><br /></div>";
        }

        String resulting_content = first_part + empty_line + "<div>" + m_string_to_append + "</div>" + second_part;

        Log.w("evernote_demo", "resulting_content= " + resulting_content);

        //TODO: somehow the resulting_content should be checkd for enml confirmity (see bottom of https://dev.evernote.com/doc/articles/enml.php)

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

        Log.w("evernote_demo", "updated!!! with " + m_string_to_append);


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

    protected Note updateNote(Note note) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException
    {

        EvernoteNoteStoreClient noteStoreClient = EvernoteSession.getInstance().getEvernoteClientFactory().getNoteStoreClient();
        return noteStoreClient.updateNote(note);

    }


    public NoteRef getNoteRef() {
        return mNoteRef;
    }
}
