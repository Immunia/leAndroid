package com.landeseternelles.jeu.tchatle;

/**
 * Created by Levleyth on 27/03/2015.
 */
public class Utility {

    static String getNumberLetters(int nbr)
    {
        String res = "", n = "";

        do
        {
            switch (nbr%10)
            {
                case 0: n = "zero"; break;
                case 1: n = "un"; break;
                case 2: n = "deux"; break;
                case 3: n = "trois"; break;
                case 4: n = "quatre"; break;
                case 5: n = "cinq"; break;
                case 6: n = "six"; break;
                case 7: n = "sept"; break;
                case 8: n = "huit"; break;
                case 9: n = "neuf"; break;
            }
            res = n + res;
            nbr = nbr/10;
        }while(nbr > 0);

        return res;
    }

    static String getNumberLetters(String str)
    {
        str = str.replaceAll( "_","" );
        str = str.replaceAll( "-","" );
        str = str.replaceAll( "0","zero" );
        str = str.replaceAll( "1","un" );
        str = str.replaceAll( "2","deux" );
        str = str.replaceAll( "3","trois" );
        str = str.replaceAll( "4","quatre" );
        str = str.replaceAll( "5","cinq" );
        str = str.replaceAll( "6","six" );
        str = str.replaceAll( "7","sept" );
        str = str.replaceAll( "8","huit" );
        str = str.replaceAll( "9","neuf" );

        return str;
    }
}
