package cz.sps_pi.sportovni_den.entity;

/**
 * Created by Martin Forejt on 07.01.2017.
 * forejt.martin97@gmail.com
 */

public class User {
    public static final int TYPE_PLAYER = 1;
    public static final int TYPE_REFEREE = 2;
    public static final int TYPE_ANONYM = 3;

    private int type;
    private String name;
    private String password;
    private String teamName;
    private String email;
    private Team team = null;
    private String apiKey;
    private Integer id;

    public int getType() {
        return type;
    }

    public User setType(int type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getTeamName() {
        return teamName;
    }

    public User setTeamName(String teamName) {
        this.teamName = teamName;
        return this;
    }

    public Team getTeam() {
        if (team == null) team = Team.valueOf(teamName);

        return team;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
