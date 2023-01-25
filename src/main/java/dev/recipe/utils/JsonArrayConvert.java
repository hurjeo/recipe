package dev.recipe.utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class JsonArrayConvert {
    private JsonArrayConvert() {}

    public static String[] arrayConvert(JSONArray jsonArray){
        List<String> list = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++){
            list.add(jsonArray.getString(i));
        }
        String[] str = list.toArray(new String[0]);

        return str;
    }
}
