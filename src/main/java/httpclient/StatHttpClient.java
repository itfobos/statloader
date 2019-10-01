package httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dtos.SkatersStat;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class StatHttpClient {

    private StatHttpClient() {
    }

    private static final String baseUrl = "https://api.nhle.com/stats/rest/";
    private static final HttpClient client = HttpClient.newBuilder().build();

    public static SkatersStat requestSkatersStat() throws IOException, InterruptedException {
        //TODO: Make season as parameter
        String url = baseUrl
                + "skaters?isAggregate=false&reportType=basic&isGame=false&reportName=skatersummary&cayenneExp="
                + URLEncoder.encode("leagueId=133 and gameTypeId=2 and seasonId>=20172018 and seasonId<=20172018", StandardCharsets.UTF_8.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println(response);
            throw new IOException("Response code is not OK: " + response.statusCode());
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        SkatersStat stat = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readerFor(SkatersStat.class)
                .readValue(response.body());

        return stat;
    }
}
