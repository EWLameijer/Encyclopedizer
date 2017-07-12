package encyclopedizer;

import static org.junit.Assert.*;
import org.junit.jupiter.api.Test;


/**
 * Created by Eric-Wubbo on 08-07-17.
 */
public class EntryTest {

    @Test
    public void testEntryConstructor() {
        String randomTerm = "blueberry";
        String randomData = "tastes good. Also is blue.";
        String randomEntry = randomTerm + ":" + randomData;

        Entry testEntry = new Entry(randomEntry);
        assertEquals(testEntry.getName(), randomTerm);
        assertEquals(testEntry.getDescription(), randomData);
    }
}