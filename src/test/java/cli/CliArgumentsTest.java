package cli;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.time.Year;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CliArgumentsTest {
    private static final String[] BORDER_OPTS = {"--fromSeason", "2018", "--toSeason", "2019"};
    private CliArguments arguments;

    @Before
    public void setUp() {
        arguments = CliArguments.fromArgs(BORDER_OPTS);
    }

    @Test
    public void timeBordersPositiveTest() {
        Year fromYear = Year.of(2018);
        Year toYear = Year.of(2019);

        CliArguments arguments = CliArguments.fromArgs("--fromSeason", fromYear.toString(), "--toSeason", toYear.toString());

        assertTrue(arguments.argumentsAreCorrect());

        assertEquals(fromYear, arguments.getFromSeason());
        assertEquals(toYear, arguments.getToSeason());
    }

    @Test
    public void requirePlayersStatTest() {
        final File file = new File("/tmp/abc/out.txt");

        String[] gamesOpts = {"--games", "--gamesOutFile", file.getPath()};
        String[] args = Stream.of(BORDER_OPTS, gamesOpts).flatMap(Stream::of).toArray(String[]::new);

        CliArguments arguments = CliArguments.fromArgs(args);

        assertTrue(arguments.argumentsAreCorrect());

        assertTrue(arguments.isGamesStatRequired());
        assertEquals(file, arguments.getGamesStatOutFile());
    }

    @Test
    public void requireTeamsStatTest() {
        final File file = new File("/tmp/abc/out.txt");

        String[] gamesOpts = {"--teams", "--teamsOutFile", file.getPath()};
        String[] args = Stream.of(BORDER_OPTS, gamesOpts).flatMap(Stream::of).toArray(String[]::new);

        CliArguments arguments = CliArguments.fromArgs(args);

        assertTrue(arguments.argumentsAreCorrect());

        assertTrue(arguments.isTeamsStatRequired());
        assertEquals(file, arguments.getTeamsStatOutFile());
    }

    @Test
    public void gamesTeamsStatTest() {
        final File file = new File("/tmp/abc/out.txt");

        String[] gamesOpts = {"--games", "--gamesOutFile", file.getPath()};
        String[] args = Stream.of(BORDER_OPTS, gamesOpts).flatMap(Stream::of).toArray(String[]::new);

        CliArguments arguments = CliArguments.fromArgs(args);

        assertTrue(arguments.argumentsAreCorrect());

        assertTrue(arguments.isGamesStatRequired());
        assertEquals(file, arguments.getGamesStatOutFile());
    }
}
