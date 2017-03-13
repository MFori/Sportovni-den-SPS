package cz.sps_pi.sportovni_den.util;

/**
 * Created by Martin Forejt on 10.01.2017.
 * forejt.martin97@gmail.com
 */

public class Error {

    public static final int BAD_URL = 1;
    public static final int NO_RESOURCE = 2;
    public static final int BAD_REQUEST = 3;
    public static final int AUTHORIZATION = 4;
    public static final int OLD_DATA = 5;
    public static final int NO_SERVER = 6;

    private int code;
    private String message;
    private Todo[] todos;

    public Error(int code, String message) {
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

    public Todo[] getTodos() {
        return todos;
    }

    public void setTodos(Todo[] todos) {
        this.todos = todos;
    }

}
