package httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dtos.GenericStat;
import dtos.Skater;
import dtos.Team;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Year;

public class StatHttpClient {

    private StatHttpClient() {
    }

    private static final String baseUrl = "https://api.nhle.com/stats/rest/";
    private static final HttpClient client = HttpClient.newBuilder().build();


    public static GenericStat<Team> requestTeamStat(Year fromYear, Year toYear) throws IOException, InterruptedException {
        String params = makeUrlParamsString(fromYear, toYear);
        String url = baseUrl + "team?reportName=teamsummary&" + params;

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
        GenericStat stat = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readerFor(GenericStat.class)
                .readValue(response.body());

        return stat;
    }

    public static GenericStat<Skater> requestSkatersStat(Year fromYear, Year toYear) throws IOException, InterruptedException {
        String params = makeUrlParamsString(fromYear, toYear);

        String url = baseUrl + "skaters?reportName=skatersummary&" + params;

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
        GenericStat<Skater> stat = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readerFor(GenericStat.class)
                .readValue(response.body());

        return stat;
    }

    private static String makeUrlParamsString(Year fromYear, Year toYear) {
        String fromSeasonId = String.format("%s%s", fromYear, fromYear.plusYears(1));
        String toSeasonId = String.format("%s%s", toYear.minusYears(1), toYear);

        String timeBordersCondition =
                String.format("leagueId=133 and gameTypeId=2 and seasonId>=%s and seasonId<=%s", fromSeasonId, toSeasonId);
        try {
            timeBordersCondition = URLEncoder.encode(timeBordersCondition, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "isAggregate=false&reportType=basic&isGame=false&cayenneExp=" + timeBordersCondition;
    }
}
