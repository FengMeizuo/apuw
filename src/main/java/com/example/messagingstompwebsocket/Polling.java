package com.example.messagingstompwebsocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Polling {
    private static Map<String, HttpExchange> subscribers = new HashMap<>();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/subscribe", new SubscribeHandler());
        server.createContext("/publish", new PublishHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server running on port 8080");
    }

    static class SubscribeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String id = String.valueOf(Math.random());
            subscribers.put(id, t);
            t.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
            t.getResponseHeaders().add("Cache-Control", "no-cache, must-revalidate");

            t.close();
        }
    }

    static class PublishHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String message = getStringFromInputStream(t.getRequestBody());
            for (String id : subscribers.keySet()) {
                HttpExchange res = subscribers.get(id);
                res.sendResponseHeaders(200, message.getBytes().length);
                res.getResponseBody().write(message.getBytes());
                res.close();
            }
            subscribers.clear();
            t.sendResponseHeaders(200, 0);
            t.close();
        }
    }

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
