package bg.sofia.uni.fmi.mjt.crypto.services;

import bg.sofia.uni.fmi.mjt.crypto.services.file.FileServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.*;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class FileServiceImplTest {
    private final static String TEST_FILE_LOCATION = "test.json";

    private FileServiceImpl fileService;

    @Before
    public void setUp(){
        fileService = new FileServiceImpl();
    }

    @Test
    public void updateFile_notNullObjectValidPath_expectedSuccessfulWrite() {
        String messageToWrite = "Hello";

        fileService.updateFile(messageToWrite, TEST_FILE_LOCATION);

        File file = new File(TEST_FILE_LOCATION);
        try (FileReader fileReader = new FileReader(file);
              BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String result = bufferedReader.readLine();

            assertNotNull(result);
            assertEquals('"' + messageToWrite + '"', result);
        } catch (IOException e) {
            e.printStackTrace();
            fail("Test encountered exception!");
        } finally {
            if(file.exists()) {
                file.delete();
            }
        }
    }
}
