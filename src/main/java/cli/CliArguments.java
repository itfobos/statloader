package cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CliArguments {
    private final List<String> errors = new ArrayList<>();
    private CommandLine commandLine;

    private Year fromSeason;
    private Year toSeason;

    private boolean playerStatRequired;
    private File playerStatOutFile;

    private boolean teamsStatRequired;
    private File teamsStatOutFile;

    private boolean gamesStatRequired;
    private File gamesStatOutFile;

    private CliArguments(String... args) {
        try {
            commandLine = PARSER.parse(OPTIONS, args);
        } catch (ParseException e) {
            this.errors.add(e.getMessage());
        }
    }

    private static final CommandLineParser PARSER = new DefaultParser();

    private static final Options OPTIONS = new Options();
    private static final String FROM_SEASON_OPT = "fromSeason";
    private static final String TO_SEASON_OPT = "toSeason";

    private static final String PLAYERS_OPT = "players";
    private static final String PLAYERS_OUT_FILE_OPT = "playersOutFile";

    private static final String TEAMS_OPT = "teams";
    private static final String TEAMS_OUT_FILE_OPT = "teamsOutFile";

    private static final String GAMES_OPT = "games";
    private static final String GAMES_OUT_FILE_OPT = "gamesOutFile";

    private static final HelpFormatter HELP_FORMATTER = new HelpFormatter();

    private void parseTimeBorders() {
        Objects.requireNonNull(commandLine);

        fromSeason = Year.parse(commandLine.getOptionValue(FROM_SEASON_OPT));
        toSeason = Year.parse(commandLine.getOptionValue(TO_SEASON_OPT));
    }

    private OptionFlagAndOutFile parseOptionAndOutFile(String optionName, String outFileOptionName) {
        boolean optionFlag = commandLine.hasOption(optionName);

        if (!optionFlag) return new OptionFlagAndOutFile(false, null);

        File outFile = null;
        String filePath = commandLine.getOptionValue(outFileOptionName);
        if (isEmpty(filePath)) {
            errors.add(String.format("Option '%s' is required", outFileOptionName));
        } else {
            outFile = new File(filePath);
            if (outFile.isDirectory()) {
                outFile = null;
                errors.add(String.format("Output path '%s' is a directory", filePath));
            }
        }

        return new OptionFlagAndOutFile(optionFlag, outFile);
    }

    private void parsePlayers() {
        OptionFlagAndOutFile optionFlagAndOutFile = parseOptionAndOutFile(PLAYERS_OPT, PLAYERS_OUT_FILE_OPT);

        playerStatRequired = optionFlagAndOutFile.optionFlag;
        this.playerStatOutFile = optionFlagAndOutFile.outFile;
    }


    private void parseTeams() {
        OptionFlagAndOutFile optionFlagAndOutFile = parseOptionAndOutFile(TEAMS_OPT, TEAMS_OUT_FILE_OPT);

        teamsStatRequired = optionFlagAndOutFile.optionFlag;
        teamsStatOutFile = optionFlagAndOutFile.outFile;
    }

    private void parseGames() {
        OptionFlagAndOutFile optionFlagAndOutFile = parseOptionAndOutFile(GAMES_OPT, GAMES_OUT_FILE_OPT);

        gamesStatRequired = optionFlagAndOutFile.optionFlag;
        gamesStatOutFile = optionFlagAndOutFile.outFile;
    }

    public boolean argumentsAreCorrect() {
        return errors.isEmpty();
    }

    /**
     * Prints errors to {@link System#err}
     */
    public void printErrors() {
        errors.forEach(System.err::println);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isBlank();
    }

    public static CliArguments fromArgs(String... args) {
        CliArguments result = new CliArguments(args);

        if (result.argumentsAreCorrect()) {
            result.parseTimeBorders();
            result.parseTeams();
            result.parsePlayers();
            result.parseGames();
        }

        return result;
    }

    static {
        OPTIONS.addOption(Option.builder().longOpt(FROM_SEASON_OPT)
                .required()
                .hasArg()
                .desc("Left borders of stat time range(included)")
                .build());

        OPTIONS.addOption(Option.builder().longOpt(TO_SEASON_OPT)
                .required()
                .hasArg()
                .desc("Right borders of stat time range(included)")
                .build());

        OPTIONS.addOption(Option.builder().longOpt(PLAYERS_OPT)
                .required(false)
                .hasArg(false)
                .desc("Will players stat be loaded")
                .build());

        OPTIONS.addOption(Option.builder().longOpt(PLAYERS_OUT_FILE_OPT)
                .required(false)
                .hasArg()
                .desc("Players stat output file")
                .build());

        OPTIONS.addOption(Option.builder().longOpt(TEAMS_OPT)
                .required(false)
                .hasArg(false)
                .desc("Will teams stat be loaded")
                .build());

        OPTIONS.addOption(Option.builder().longOpt(TEAMS_OUT_FILE_OPT)
                .required(false)
                .hasArg()
                .desc("Teams stat output file")
                .build());

        OPTIONS.addOption(Option.builder().longOpt(GAMES_OPT)
                .required(false)
                .hasArg(false)
                .desc("Will games stat be loaded")
                .build());

        OPTIONS.addOption(Option.builder().longOpt(GAMES_OUT_FILE_OPT)
                .required(false)
                .hasArg()
                .desc("Games stat output file")
                .build());
    }

    public static void printUsage() {
        HELP_FORMATTER.printHelp("statloader", OPTIONS);
    }

    public Year getFromSeason() {
        return fromSeason;
    }

    public Year getToSeason() {
        return toSeason;
    }

    public boolean isPlayerStatRequired() {
        return playerStatRequired;
    }

    public File getPlayerStatOutFile() {
        return playerStatOutFile;
    }

    public boolean isTeamsStatRequired() {
        return teamsStatRequired;
    }

    public File getTeamsStatOutFile() {
        return teamsStatOutFile;
    }

    public boolean isGamesStatRequired() {
        return gamesStatRequired;
    }

    public File getGamesStatOutFile() {
        return gamesStatOutFile;
    }
}
