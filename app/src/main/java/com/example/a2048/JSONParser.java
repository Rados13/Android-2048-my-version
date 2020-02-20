package com.example.a2048;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class JSONParser {

    private static final String mapsFileName = "Maps.json";
    private static final String mapsFilePath = "/Data/Maps.json";
    private static final String savesFileName = "Saves.json";
    private static final String savesFilePath = "/Data/Saves.json";

    static ArrayList<ArrayList<String>> getMaps(Context ctx) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        result.add(new ArrayList<String>());
        result.add(new ArrayList<String>());
        result.add(new ArrayList<String>());
        result.add(new ArrayList<String>());
        try {
            JSONArray maps = getFileJSONArray(ctx, mapsFileName);
            for (int i = 0; i < maps.length(); i++) {
                JSONObject map = maps.getJSONObject(i);
                result.get(0).add(map.getString("mapsName"));
                result.get(1).add(map.getString("highestScore"));
                result.get(2).add(Integer.valueOf(map.getInt("mapsSize")).toString());
                result.get(3).add(map.getString("mapsStructure"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    static Map getMapToPlay(Context ctx, int index, boolean holes) {
        Map mapObject = null;
        try {
            JSONArray maps = getFileJSONArray(ctx, mapsFileName);
            if (index > maps.length()) {
                throw new ArrayIndexOutOfBoundsException();
            } else {
                JSONObject map = maps.getJSONObject(index);
                mapObject = new Map(map.getString("mapsStructure"), map.getInt("mapsSize"), holes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapObject;
    }

    private static JSONArray getFileJSONArray(Context ctx, String fileName) {
        JSONArray result = null;
        try {
            File file = new File(ctx.getFilesDir().getAbsolutePath() + "/Data/" + fileName);
            if (!file.exists()) {
                createFile(ctx, fileName);
            }
            FileReader reader = new FileReader(ctx.getFilesDir().getAbsolutePath() + "/Data/" + fileName);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonObject = new JSONObject(sb.toString());
            if (fileName.equals(mapsFileName)) {
                result = jsonObject.getJSONArray("maps");
            } else {
                result = jsonObject.getJSONArray("saves");
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void createFile(Context ctx, String newFileName) {
        File file = new File(ctx.getFilesDir(), "Data");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File saveFile = new File(file, newFileName);
            FileWriter writer = new FileWriter(saveFile);
            if (mapsFileName.equals(newFileName)) {
                writer.append("{\n" +
                        "  \"maps\": [\n" +
                        "    {\n" +
                        "      \"mapsName\": \"4x4\",\n" +
                        "      \"highestScore\": \"0\",\n" +
                        "      \"mapsSize\": 4,\n" +
                        "      \"mapsStructure\": \"1111111111111111\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}");
            } else {
                writer.append("{\n" +
                        "  \"saves\": [\n" +
                        "    {\n" +
                        "      \"mapName\": \"4x4\",\n" +
                        "      \"holes\": false,\n" +
                        "      \"score\": \"0\",\n" +
                        "      \"mapSize\": 4,\n" +
                        "      \"mapStructure\": \"1111111111111111\"\n" +
                        "    }\n" +
                        "  ]\n" + "}");
            }
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            e.printStackTrace();
        }
    }

    static Long getMapHighestScore(Context ctx, int index) {
        try {
            JSONArray maps = getFileJSONArray(ctx, mapsFileName);
            if (index > maps.length()) {
                throw new ArrayIndexOutOfBoundsException();
            } else {
                JSONObject map = maps.getJSONObject(index);
                return Long.valueOf(map.get("highestScore").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void saveScore(Context ctx, int index, Long score) {
        try {
            JSONArray maps = getFileJSONArray(ctx, mapsFileName);
            if (index > maps.length()) {
                throw new ArrayIndexOutOfBoundsException();
            } else {
                JSONObject map = maps.getJSONObject(index);
                map.remove("highestScore");
                map.put("highestScore", score.toString());
                FileWriter file = new FileWriter(ctx.getFilesDir() + mapsFilePath);
                file.write("{\n" + "\"maps\":" + maps.toString() + "}");
                file.flush();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<ArrayList<String>> getSaves(Context ctx) {
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        result.add(new ArrayList<String>());
        result.add(new ArrayList<String>());
        result.add(new ArrayList<String>());
        result.add(new ArrayList<String>());
        result.add(new ArrayList<String>());
        try {
            JSONArray maps = getFileJSONArray(ctx, savesFileName);
            for (int i = 0; i < maps.length(); i++) {
                JSONObject map = maps.getJSONObject(i);
                result.get(0).add(map.getString("mapName"));
                result.get(1).add(map.getString("score"));
                result.get(2).add(Integer.valueOf(map.getInt("mapSize")).toString());
                result.get(3).add(map.getString("mapStructure"));
                result.get(4).add(Boolean.toString(map.getBoolean("holes")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    static Map getSave(Context ctx, int index) {
        Map resultMap = null;
        try {
            JSONArray maps = getFileJSONArray(ctx, savesFileName);
            if (index > maps.length()) {
                throw new ArrayIndexOutOfBoundsException();
            } else {
                JSONObject map = maps.getJSONObject(index);
                resultMap = new Map(map.getString("mapStructure"),
                        Long.valueOf(map.getString("score")), map.getInt("mapSize"),
                        map.getBoolean("holes"));
            }
        } catch (JSONException e) {
            Log.e("Login activity",e.toString());
            e.printStackTrace();
        }
        return resultMap;

    }

    static void makeSave(Context ctx, int index, Map mapToSave) {
        try {
            JSONArray maps = getFileJSONArray(ctx, savesFileName);
            if (index > maps.length()) {
                throw new ArrayIndexOutOfBoundsException();
            } else {
                JSONObject map = maps.getJSONObject(index);
                map.remove("holes");
                map.put("holes", mapToSave.getHoles());
                map.remove("score");
                map.put("score", mapToSave.getScore());
                map.remove("mapStructure");
                map.put("mapStructure", MapConverter.arrayToString(mapToSave.getMapStatus(), mapToSave.getMapSize()));
                FileWriter file = new FileWriter(ctx.getFilesDir() + savesFilePath);
                Log.e("Maps-stats",maps.toString());
                file.write("{\n" + "\"saves\":" + maps.toString() + "}");
                file.flush();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
