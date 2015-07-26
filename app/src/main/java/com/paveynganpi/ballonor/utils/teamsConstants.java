package com.paveynganpi.ballonor.utils;

/**
 * Created by paveynganpi on 7/17/15.
 */
public class TeamsConstants {

    public static String[] eplTeams = {"Chelsea FC", "Manchester United", "Manchester City", "Arsenal FC", "Everton FC", "Tottenham Hotspurs", "Liverpool FC",
            "Newcastle United", "Southampton FC", "Bournemouth AFC", "Watford FC", "Norwich City FC", "Stoke City", "Crystal Palace", "Aston Villa",
            "Sunderland AFC", "Leicester City", "West Bromwich Albion", "West Ham United", "Swansea City FC"};

    public static String[] laLigaTeams = {"Real Madrid CF", "Barcelona FC", "Atlético Madrid", "Valencia CF", "Sevilla FC", "Villarreal CF",
                                            "Espanyol", "Getafe CF", "Real Sociedad"};

    public static String[] serieATeams = {"Juventus", "AC Milan", "Inter Milan", "As Roma"};

    public static String[] ligue1Teams = {"Paris Saint-Germain", "Olympique Lyonnais", "Olympique de Marseille", "Bordeaux", "Monaco AS"};

    public static String[] bundesligaTeams = {"Bayern München FC", "Wolfsburg", "Borussia Dortmund", "Bayer 04 Leverkusen", "Schalke 04 FC", "Werder Bremen"};

    public static String[] eredivisieTeams = {"Ajax Amsterdam", "PSV Eindhoven"};

    public static String[] majorLeagueSoccer = {"DC United", "New England Revolution", "Columbus Crew", "New York Red Bulls", "Sporting Kansas City",
                                                "Seattle Sounders FC", "LA Galaxy", "Real Salt Lake", "FC Dallas", "San Jose Earthquakes", "Chicago Fire"};

    public static String[] turkishSuperLig = {"Fenerbahce", "Galatasaray", "Besiktas"};


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
        else if(teamName.equals(LeaguesConstants.getLeagueNames()[6])){
            return majorLeagueSoccer;
        }
        else if(teamName.equals(LeaguesConstants.getLeagueNames()[7])){
            return turkishSuperLig;
        }
        return null;
    }

    public static String getTeamImageUrl() {
        return "http://pbs.twimg.com/profile_images/532484112821415938/HGleGRL5_normal.jpeg";
    }
}
