package com.paveynganpi.ballonor.utils;

/**
 * Created by paveynganpi on 7/17/15.
 */
public class TeamsConstants {

    public static String[] eplTeams = {"Chelsea FC", "Manchester United", "Manchester City", "Arsenal FC", "Everton FC", "Tottenham Hotspurs", "Liverpool FC",
            "Newcastle United", "Southampton FC"};
    public static String[] laLigaTeams = {"Real Madrid CF", "Barcelona FC", "Atlético Madrid", "Valencia CF", "Sevilla FC", "Villarreal CF"};
    public static String[] serieATeams = {"Juventus", "AC Milan", "Inter Milan", "As Roma"};
    public static String[] ligue1Teams = {"Paris Saint Germain", "Olympic Lyonais", "Olympic de Marseille", "Lille", "Bordeaux"};
    public static String[] bundesligaTeams = {"Bayern Munchen", "Wolfsbug", "Borussia Dortmund", "Bayer Leverkusen", "Schalke"};
    public static String[] eredivisieTeams = {"Ajax Amsterdam", "PSV"};

    public static String[] getTeam(String teamName) {

        if(teamName.equals(LeaguesConstants.getLeagueNames()[0])){
            return eplTeams;
        }
        else if(teamName.equals(LeaguesConstants.getLeagueNames()[1])){
            return bundesligaTeams;
        }
        else if(teamName.equals(LeaguesConstants.getLeagueNames()[2])){
            return serieATeams;
        }
        else if(teamName.equals(LeaguesConstants.getLeagueNames()[3])){
            return ligue1Teams;
        }
        else if(teamName.equals(LeaguesConstants.getLeagueNames()[4])){
            return laLigaTeams;
        }
        else if(teamName.equals(LeaguesConstants.getLeagueNames()[5])){
            return eredivisieTeams;
        }
        return null;
    }

    public static String getTeamImageUrl() {
        return "http://pbs.twimg.com/profile_images/532484112821415938/HGleGRL5_normal.jpeg";
    }
}
