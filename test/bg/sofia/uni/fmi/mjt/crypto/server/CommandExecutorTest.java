package bg.sofia.uni.fmi.mjt.crypto.server;

import bg.sofia.uni.fmi.mjt.crypto.commands.Command;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.InsufficientFundsException;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.UnsupportedCommandArgumentsException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidUsernameException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandExecutorTest {
    @Mock
    private CommandFactory mockedFactory;

    @Mock
    private Command mockedCommand;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private RequestHandler requestHandler;

    private CommandExecutor commandExecutor;

    @Before
    public void setUp(){
        commandExecutor = new CommandExecutor(applicationContext, requestHandler);
        commandExecutor.setCommandFactory(mockedFactory);
    }

    @Test
    public void executeCommand_commandThrowsInvalidUsernameException_expectedInvalidUsernameMessage(){
        when(mockedFactory.createCommand(new String[]{"some", "arguments"})).thenReturn(mockedCommand);
        when(mockedCommand.execute()).thenThrow(InvalidUsernameException.class);

        String result = commandExecutor.executeCommand("some arguments");

        assertNotNull(result);
        assertTrue(result.contains("username is invalid"));
    }

    @Test
    public void executeCommand_commandThrowsInvalidPasswordException_expectedInvalidPasswordMessage(){
        when(mockedFactory.createCommand(new String[]{"some", "arguments"})).thenReturn(mockedCommand);
        when(mockedCommand.execute()).thenThrow(InvalidPasswordException.class);

        String result = commandExecutor.executeCommand("some arguments");

        assertNotNull(result);
        assertTrue(result.contains("password is invalid"));
    }

    @Test
    public void executeCommand_commandThrowsInsufficientFundsException_expectedInsufficientFundsMessage(){
        when(mockedFactory.createCommand(new String[]{"some", "arguments"})).thenReturn(mockedCommand);
        when(mockedCommand.execute()).thenThrow(InsufficientFundsException.class);

        String result = commandExecutor.executeCommand("some arguments");

        assertNotNull(result);
        assertTrue(result.contains("not enough funds"));
    }

    @Test
    public void executeCommand_commandThrowsNotLoggedInException_expectedNotLoggedInMessage(){
        when(mockedFactory.createCommand(new String[]{"some", "arguments"})).thenReturn(mockedCommand);
        when(mockedCommand.execute()).thenThrow(NotLoggedInException.class);

        String result = commandExecutor.executeCommand("some arguments");

        assertNotNull(result);
        assertTrue(result.contains("Not logged in"));
    }

    @Test
    public void executeCommand_factoryThrowsUnsupportedCommandArgException_expectedImproperUseMessage(){
        when(mockedFactory.createCommand(new String[] {"some", "arguments"})).thenThrow(UnsupportedCommandArgumentsException.class);

        String result = commandExecutor.executeCommand("some arguments");

        assertNotNull(result);
        assertTrue(result.contains("command was used improperly"));
    }

    @Test
    public void executeCommand_factoryThrowsCommandNotFoundException_expectedNotFoundMessage() {
        when(mockedFactory.createCommand(new String[]{"some", "arguments"})).thenThrow(CommandNotFoundException.class);

        String result = commandExecutor.executeCommand("some arguments");

        assertNotNull(result);
        assertTrue(result.contains("Could not find command"));
    }

    @Test
    public void executeCommand_successfulExecution_expectedResponseReturned(){
        when(mockedFactory.createCommand(any())).thenReturn(mockedCommand);
        when(mockedCommand.execute()).thenReturn("SUCCESS");

        String result = commandExecutor.executeCommand("some argument");

        assertEquals("SUCCESS", result);
    }
}
