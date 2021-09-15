package com.linkeriyo.f1bot;

import com.linkeriyo.f1bot.listeners.ConsoleInputListener;
import com.linkeriyo.f1bot.listeners.F1MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

public class F1Bot {
    private static JDA jda;
    private static SheetAccess sheetAccess;
    private static JSONObject config;

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) throws IOException, InterruptedException, GeneralSecurityException {
        config = new JSONObject(readFile("files/config.json", StandardCharsets.UTF_8));

        sheetAccess = new SheetAccess();
        jda = JDABuilder.createDefault(config.getString("token"))
                .addEventListeners(new F1MessageListener())
                .setActivity(Activity.watching("linkeriyo programming"))
                .build();
        jda.awaitReady();

        new Thread(new ConsoleInputListener(jda)).start();
    }

    public static JDA getJda() {
        return jda;
    }

    public static SheetAccess getSheetAccess() {
        return sheetAccess;
    }

    public static JSONObject getConfig() {
        return config;
    }
}
