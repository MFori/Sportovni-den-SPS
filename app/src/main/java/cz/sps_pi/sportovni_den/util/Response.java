package cz.sps_pi.sportovni_den.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Martin Forejt on 08.01.2017.
 * forejt.martin97@gmail.com
 */

public class Response {
    private boolean success;
    private String json;

    public void setResponse(String json) {
        this.json = json;
    }

    public JSONObject getData() {
        try {
            JSONObject object = new JSONObject(json);
            return object.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getData(String item) {
        try {
            JSONObject object = new JSONObject(json);
            return object.getJSONObject("data").getJSONObject(item);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getData(String item, String subItem) {
        try {
            return getData(item).getJSONObject(subItem);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray getArray(String item) {
        try {
            JSONObject object = new JSONObject(json);
            return object.getJSONObject("data").getJSONArray(item);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Error[] getErrors() {
        try {
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("errors");
            Error[] errors = new Error[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                Error error = new Error(o.getInt("code"), o.getString("message"));
                try {
                    JSONArray jsonTodo = o.getJSONArray("todo");
                    Todo[] todos = new Todo[jsonTodo.length()];
                    for (int j = 0; j < jsonTodo.length(); j++) {
                        JSONObject t = jsonTodo.getJSONObject(j);
                        Todo todo = new Todo(t.getInt("code"), t.getString("message"));
                        todos[j] = todo;
                    }
                    error.setTodos(todos);
                } catch (JSONException e) {

                }

                errors[i] = error;
            }

            return errors;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean hasError(int code) {
        if (getErrors() == null) return false;
        for (Error error : getErrors()) {
            if (error.getCode() == code) return true;
        }

        return false;
    }

    public Response addError(Error error) {
        try {
            if (json == null) initJson();
            JSONObject object = new JSONObject(json);
            JSONArray errors = object.getJSONArray("errors");
            JSONObject jsError = new JSONObject();
            jsError.put("code", error.getCode());
            jsError.put("message", error.getMessage());
            jsError.put("todo", null);
            errors.put(jsError);
            json = object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return this;
    }

    private void initJson() {
        json = "{\"data\":{}, \"errors\":[]}";
    }

    public String getRawData() {
        return json;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
