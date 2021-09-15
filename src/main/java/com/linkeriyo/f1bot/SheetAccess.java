package com.linkeriyo.f1bot;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class SheetAccess {

    Sheets service;
    final String sheetId = "1yKnDX7a_JZL6dYqWGCr55fGSIL6gLSnnvMDS7wqAsXg";
    JSONObject spreadsheetRanges = F1Bot.getConfig().getJSONObject("spreadsheetRanges");
    JSONObject firstEditionRanges = spreadsheetRanges.getJSONObject("firstEditionRanges");
    JSONObject secondEditionRanges = spreadsheetRanges.getJSONObject("secondEditionRanges");


    public SheetAccess() throws GeneralSecurityException, IOException {
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream("files/client_secret.json")) {
            credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);
        }
        final HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String APPLICATION_NAME = "Linkeribot";
        final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private List<List<Object>> getGridFromRange(String range) {
        try {
            return service.spreadsheets().values().get(sheetId, range).execute().getValues();
        } catch (IOException e) {
            return null;
        }
    }

    public List<List<Object>> getPilotGrid() {
        return getGridFromRange(secondEditionRanges.getString("pilotsGrid"));
    }

    public List<List<Object>> getPilotScores() {
        return getGridFromRange(secondEditionRanges.getString("pilotsScores"));
    }

    public List<List<Object>> getTeamGrid() {
        return getGridFromRange(secondEditionRanges.getString("teamsGrid"));
    }

    public List<List<Object>> getPilotStatsGrid() {
        return getGridFromRange(spreadsheetRanges.getString("pilotsStats"));
    }

    public List<List<Object>> getTeamStatsGrid() {
        return getGridFromRange(spreadsheetRanges.getString("teamsStats"));
    }

    public Sheets getService() {
        return service;
    }
}
