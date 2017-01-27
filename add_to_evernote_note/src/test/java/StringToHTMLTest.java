import org.junit.Test;
import java.util.regex.Pattern;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import com.sp.android.add_to_evernote_note.task.UpdateNoteTask;

public class StringToHTMLTest {

    @Test
    public void StringToHTMLOneNewlineTest() {

        String expectedString = "<div>test</div><div>test2</div>";
        String outputString = UpdateNoteTask.stringToHTML("test\ntest2");

        assertEquals(expectedString, outputString);
    }

    @Test
    public void StringToHTMLOneNewOneEmptyLineTest() {

        String expectedString = "<div>test</div><div><br /></div><div>test2</div>";
        String outputString = UpdateNoteTask.stringToHTML("test\n\ntest2");

        assertEquals(expectedString, outputString);
    }

}