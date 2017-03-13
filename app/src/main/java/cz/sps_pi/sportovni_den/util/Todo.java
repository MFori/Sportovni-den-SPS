package cz.sps_pi.sportovni_den.util;

/**
 * Created by Martin Forejt on 10.01.2017.
 * forejt.martin97@gmail.com
 */

public class Todo {
    public static final int TODO_CLEAR_CACHE = 1;
    public static final int TODO_LOGOUT = 2;
    public static final int TODO_SHOW = 3;

    private int code;
    private String message;

    public Todo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}