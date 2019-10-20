package services;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dtos.GameByGamePlayerStat;
import dtos.GenericStat;
import httpclient.StatHttpClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GameByGamePlayerStatService {

    private static final int MAX_BUNCH_SIZE = 50000;

    public static void loadAndPersistData(java.time.Year fromYear, Year toYear, File outputFile) throws IOException, InterruptedException {

        final Year rightTimeBorder = toYear.plusYears(1);
        List<List<GameByGamePlayerStat>> accumulatedData = new ArrayList<>(toYear.getValue() - fromYear.getValue() + 1);

        for (Year season = fromYear; season.isBefore(rightTimeBorder); season = season.plusYears(1)) {
            System.out.println(String.format("Requesting %s season", season));
            GenericStat<GameByGamePlayerStat> statData = StatHttpClient.requestGameByGamePlayerStat(season, season);
            accumulatedData.add(statData.data);

            if (statData.data.size() >= MAX_BUNCH_SIZE) System.err.println("Data size is: " + statData.data.size());
        }

        final CsvMapper mapper = (CsvMapper) new CsvMapper().registerModule(new JavaTimeModule());
        final CsvSchema schema = mapper.schemaFor(GameByGamePlayerStat.class).withUseHeader(true);

        try (OutputStreamWriter writerOutputStream = createWriterOutputStream(outputFile)) {
            List<GameByGamePlayerStat> toPersistData = accumulatedData.stream().flatMap(Collection::stream).collect(Collectors.toList());
            mapper.writer(schema).writeValue(writerOutputStream, toPersistData);
        }
    }

    private static OutputStreamWriter createWriterOutputStream(File outputFile) throws FileNotFoundException {
        return new OutputStreamWriter(
                new BufferedOutputStream(
                        new FileOutputStream(outputFile), 4096), StandardCharsets.UTF_8);
    }
}
