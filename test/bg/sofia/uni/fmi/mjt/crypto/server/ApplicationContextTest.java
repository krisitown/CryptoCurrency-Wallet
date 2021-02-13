package bg.sofia.uni.fmi.mjt.crypto.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationContextTest {
    @Test
    public void applicationContext_dependenciesLoadProperly(){
        ApplicationContext applicationContext = new ApplicationContext();
        assertNotNull(applicationContext.getCoinService());
        assertNotNull(applicationContext.getHasher());
        assertNotNull(applicationContext.getUserService());
    }
}
