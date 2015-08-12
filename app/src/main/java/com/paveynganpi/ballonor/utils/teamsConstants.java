package com.paveynganpi.ballonor.utils;

/**
 * Created by paveynganpi on 7/17/15.
 */
public class TeamsConstants {

    public static String[] eplTeams = {"Chelsea", "Man U", "Man C", "Arsenal", "Tottenham", "Liverpool",
            "Newcastle", "Southampton", "Stoke", "C Palace", "A Villa", "Leicester", "WBA", "WHU"};

    public static String[] laLigaTeams = {"R Madrid", "Barcelona", "A Madrid", "Valencia", "Seville"};

    public static String[] serieATeams = {"Juve", "A Milan", "I Milan", "Rome"};

    public static String[] ligue1Teams = {"Paris", "Lyon", "Marseille", "Bordeaux", "Monaco"};

    public static String[] bundesligaTeams = {"Munich", "Wolfsburg", "Dortmund", "Leverkusen", "Bremen"};

    public static String[] majorLeagueSoccer = {"DC", "New England", "Columbus", "New York", "Kansas",
                                                "Seattle", "LA", "Dallas", "San Jose", "Chicago"};



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
            return majorLeagueSoccer;
        }
        return null;
    }

}
