package httpclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dtos.Game;
import dtos.GameByGamePlayerStat;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;

public class StatHttpClient {

    private static final DateTimeFormatter FROM_GAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TO_GAME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private StatHttpClient() {
    }

    private static final String baseUrl = "https://api.nhle.com/stats/rest/";
    private static final HttpClient client = HttpClient.newBuilder().build();

    public static GenericStat<GameByGamePlayerStat> requestGameByGamePlayerStat(Year fromYear, Year toYear)
            throws IOException, InterruptedException {
        String timeRangeParams = makeGameTimeRangeParam(fromYear, toYear);

        String url = baseUrl
                + "skaters?isAggregate=false&reportType=basic&isGame=true&reportName=skatersummary&"
                + timeRangeParams;

        return makeStatRequest(url);
    }

    public static GenericStat<Game> requestGamesStat(Year fromYear, Year toYear) throws IOException, InterruptedException {
        String timeRangeParams = makeGameTimeRangeParam(fromYear, toYear);

        String url = baseUrl
                + "team?isAggregate=false&reportType=basic&isGame=true&reportName=teamsummary&"
                + timeRangeParams;

        return makeStatRequest(url);
    }

    private static String makeGameTimeRangeParam(Year fromYear, Year toYear) throws UnsupportedEncodingException {
        LocalDate fromDate = fromYear.atMonth(Month.JANUARY).atDay(1);
        LocalDateTime toDateTime = toYear.atMonth(Month.DECEMBER).atDay(31).atTime(LocalTime.MAX);

        String timeRangeParams = String.format(
                "leagueId=133 and gameDate>=\"%s\" and gameDate<=\"%s\" and gameTypeId=2",
                fromDate.format(FROM_GAME_DATE_FORMATTER),
                toDateTime.format(TO_GAME_DATE_FORMATTER)
        );

        return "cayenneExp=" + URLEncoder.encode(timeRangeParams, StandardCharsets.UTF_8.toString());
    }

    public static GenericStat<Team> requestTeamStat(Year fromYear, Year toYear) throws IOException, InterruptedException {
        String params = makeUrlParamsString(fromYear, toYear);
        String url = baseUrl + "team?reportName=teamsummary&" + params;

        return makeStatRequest(url);
    }

    public static GenericStat<Skater> requestSkatersStat(Year fromYear, Year toYear) throws IOException, InterruptedException {
        String params = makeUrlParamsString(fromYear, toYear);

        String url = baseUrl + "skaters?reportName=skatersummary&" + params;

        return makeStatRequest(url);
    }

    private static <D> GenericStat<D> makeStatRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            System.err.println(response);
            throw new IOException("Response code is not OK: " + response.statusCode());
        }

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readerFor(GenericStat.class)
                .readValue(response.body());
    }

    private static String makeUrlParamsString(Year fromYear, Year toYear) {
        String fromSeasonId = String.format("%s%s", fromYear, fromYear.plusYears(1));
        String toSeasonId = String.format("%s%s", toYear.minusYears(1), toYear);

        String timeRangeCondition =
                String.format("leagueId=133 and gameTypeId=2 and seasonId>=%s and seasonId<=%s", fromSeasonId, toSeasonId);
        try {
            timeRangeCondition = URLEncoder.encode(timeRangeCondition, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return "isAggregate=false&reportType=basic&isGame=false&cayenneExp=" + timeRangeCondition;
    }
}
