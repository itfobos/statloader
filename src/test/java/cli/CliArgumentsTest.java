package cli;

import org.junit.Test;

import java.io.File;
import java.time.Year;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CliArgumentsTest {
    private static final String[] BORDER_OPTS = {"--fromSeason", "2018", "--toSeason", "2019"};

    private final static File FILE = new File("/tmp/abc/out.txt");


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
        String[] gamesOpts = {"--players", "--playersOutFile=" + FILE.getPath()};

        String[] args = Stream.of(BORDER_OPTS, gamesOpts).flatMap(Stream::of).toArray(String[]::new);

        CliArguments arguments = CliArguments.fromArgs(args);

        assertTrue(arguments.argumentsAreCorrect());

        assertTrue(arguments.isPlayerStatRequired());
        assertEquals(FILE, arguments.getPlayerStatOutFile());
    }

    @Test
    public void requireTeamsStatTest() {
        String[] gamesOpts = {"--teams", "--teamsOutFile", FILE.getPath()};
        String[] args = Stream.of(BORDER_OPTS, gamesOpts).flatMap(Stream::of).toArray(String[]::new);

        CliArguments arguments = CliArguments.fromArgs(args);

        assertTrue(arguments.argumentsAreCorrect());

        assertTrue(arguments.isTeamsStatRequired());
        assertEquals(FILE, arguments.getTeamsStatOutFile());
    }

    @Test
    public void requireGamesStatTest() {
        String[] gamesOpts = {"--games", "--gamesOutFile", FILE.getPath()};
        String[] args = Stream.of(BORDER_OPTS, gamesOpts).flatMap(Stream::of).toArray(String[]::new);

        CliArguments arguments = CliArguments.fromArgs(args);

        assertTrue(arguments.argumentsAreCorrect());

        assertTrue(arguments.isGamesStatRequired());
        assertEquals(FILE, arguments.getGamesStatOutFile());
    }
}
