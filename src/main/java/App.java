import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dtos.Skater;
import dtos.SkatersStat;
import httpclient.StatHttpClient;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) throws IOException, InterruptedException {
        SkatersStat skatersStat = StatHttpClient.requestSkatersStat();


        CsvMapper mapper = (CsvMapper) new CsvMapper().registerModule(new JavaTimeModule());
        CsvSchema schema = mapper.schemaFor(Skater.class).withUseHeader(true);

        FileOutputStream tempFileOutputStream = new FileOutputStream("/home/ilya/tmp/skaters.csv");
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(tempFileOutputStream, 1024);
        OutputStreamWriter writerOutputStream = new OutputStreamWriter(bufferedOutputStream, StandardCharsets.UTF_8);

        mapper.writer(schema).writeValue(writerOutputStream, skatersStat.data);
    }
}
