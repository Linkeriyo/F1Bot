package com.linkeriyo.f1bot.listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class ConsoleInputListener implements Runnable {

    JDA jda;

    public ConsoleInputListener(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        Scanner scan = new Scanner(System.in).useDelimiter("\n").useLocale(new Locale("es", "ES"));

        while (true) {
            String input = scan.next();

            if (input.equals("quit")) {
                System.err.println("Closing the bot...");
                jda.shutdown();
                System.exit(0);
            }

            if (input.equals("ping")) {
                System.out.println(jda.getGatewayPing() + "ms");
            } else {
                System.out.println("Comando desconocido: \"" + input.split(" ")[0] + "\"");
            }
        }
    }
}