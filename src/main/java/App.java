import cli.CliArguments;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dtos.Game;
import dtos.GameByGamePlayerStat;
import dtos.GenericStat;
import dtos.Skater;
import dtos.Team;
import httpclient.StatHttpClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class App {
    private final CliArguments cliArguments;

    private App(CliArguments cliArguments) {
        this.cliArguments = cliArguments;
    }

    private void loadAndPersistStat() throws IOException, InterruptedException {
        if (!cliArguments.argumentsAreCorrect()) {
            cliArguments.printErrors();
            CliArguments.printUsage();
            return;
        }

        System.out.println(String.format("Time range is: [%s,%s]", cliArguments.getFromSeason(), cliArguments.getToSeason()));

        if (cliArguments.isPlayerStatRequired()) {
            System.out.println("Players stat will be loaded");
            loadPlayers();
            System.out.println("Players stat has been saved in file: " + cliArguments.getPlayerStatOutFile());
        }

        if (cliArguments.isTeamsStatRequired()) {
            System.out.println("Teams stat will be loaded");
            loadTeams();
            System.out.println("Teams stat has been saved in file: " + cliArguments.getTeamsStatOutFile());
        }

        if (cliArguments.isGamesStatRequired()) {
            System.out.println("Games stat will be loaded");
            loadGames();
            System.out.println("Games stat has been saved in file: " + cliArguments.getGamesStatOutFile());
        }

        if (cliArguments.isGameByGameStatRequired()) {
            System.out.println("Games by game stat will be loaded");
            loadGameByGameStat();
            System.out.println("Games by game stat has been saved in file: " + cliArguments.getGameByGameStatOutFile());
        }
    }

    private void loadGameByGameStat() throws IOException, InterruptedException {
        GenericStat<GameByGamePlayerStat> stat = StatHttpClient.requestGameByGamePlayerStat(cliArguments.getFromSeason(), cliArguments.getToSeason());

        persisStatData(stat, GameByGamePlayerStat.class, cliArguments.getGameByGameStatOutFile());
    }

    private void loadGames() throws IOException, InterruptedException {
        GenericStat<Game> stat = StatHttpClient.requestGamesStat(cliArguments.getFromSeason(), cliArguments.getToSeason());

        persisStatData(stat, Game.class, cliArguments.getGamesStatOutFile());
    }

    private void loadTeams() throws IOException, InterruptedException {
        GenericStat<Team> stat = StatHttpClient.requestTeamStat(cliArguments.getFromSeason(), cliArguments.getToSeason());

        persisStatData(stat, Team.class, cliArguments.getTeamsStatOutFile());
    }

    private void loadPlayers() throws IOException, InterruptedException {
        GenericStat<Skater> skatersStat = StatHttpClient.requestSkatersStat(cliArguments.getFromSeason(), cliArguments.getToSeason());

        persisStatData(skatersStat, Skater.class, cliArguments.getPlayerStatOutFile());
    }

    private void persisStatData(GenericStat statData, Class schemaClass, File outputFile) throws IOException {
        CsvMapper mapper = (CsvMapper) new CsvMapper().registerModule(new JavaTimeModule());
        CsvSchema schema = mapper.schemaFor(schemaClass).withUseHeader(true);

        FileOutputStream tempFileOutputStream = new FileOutputStream(outputFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(tempFileOutputStream, 1024);
        OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, StandardCharsets.UTF_8);

        mapper.writer(schema).writeValue(writerOutputStream, statData.data);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        CliArguments cliArguments = CliArguments.fromArgs(args);

        new App(cliArguments).loadAndPersistStat();
    }
}
