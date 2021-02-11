package com.redis.basicredisleaderboarddemojava.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletResponse;

@RestController
@Service
public class Repository {
    @Value("${REDIS_URL}")
    private String properies_uri;

    @Value("${redis_leaderboard}")
    private String redis_leaderboard;

    Jedis jedis;
    private void getConnection() {
        if (jedis == null) {
            String REDIS_URL = System.getenv("REDIS_URL");

            if (REDIS_URL == null) {
                REDIS_URL = properies_uri;
            }
            jedis = new Jedis(REDIS_URL);
        }
    }

    @RequestMapping(value = "/api/list/top10", produces = { "text/html; charset=utf-8" })
    @ResponseBody
    public String getGitData(HttpServletResponse response
    ) {
        jedis = new Jedis(properies_uri);
        List<JSONObject> topList = new ArrayList<>();
        AtomicInteger index = new AtomicInteger(0);
        jedis.zrevrangeWithScores(redis_leaderboard, 0, 9).forEach((k) -> {
            JSONObject json = new JSONObject();
            Map<String, String> company = jedis.hgetAll(k.getElement());
            try {
                json.put("marketCap", (int) k.getScore());
                json.put("symbol", k.getElement());
                json.put("rank", index.incrementAndGet());
                json.put("country", company.get("country"));
                json.put("company", company.get("company"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            topList.add(json);
        });
        return topList.toString();
    }


}
