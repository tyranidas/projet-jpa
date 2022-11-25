package createTable;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import classJO.Athlete;



public class DataBaseAthlete {

	
	@SuppressWarnings("unlikely-arg-type")
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Statement stmt = DataBase.connectionDB();

		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		ArrayList<Athlete> listAth = new ArrayList<Athlete>();
		Set<Athlete> setAth = new LinkedHashSet<>();
		
	
		
		for (String l : lines) {
			

			Integer intAge = 0;
			Integer naissance  = 0;
			Integer taille = 0;
			Integer poids = 0;
			
			l = l.replace("'", "\\'");
			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
				
			}
			
			
			
			Integer DateJO = Integer.parseInt(arrayS[9]);
			if (arrayS[3].matches("-?\\d+"))
			{
			intAge = Integer.parseInt(arrayS[3]);
			naissance  = DateJO - intAge;		
			}
			
			if (arrayS[4].matches("-?\\d+"))
			{
				taille = Integer.parseInt(arrayS[4]);
			}
			
			if (arrayS[5].matches("-?\\d+"))
			{
			poids = Integer.parseInt(arrayS[5]);
		
			}
			
			Athlete athlete = new Athlete (arrayS[1], "pouet", arrayS[2], naissance, taille, poids );
			
			
			setAth.add(athlete);
			
			
		}
		
		listAth.addAll(setAth);
	
		for (Athlete a : listAth)
		{
			if (a.equals(listAth)==false)
			{
				setAth.add(a);
			}
		}
		
		listAth.clear();
		listAth.addAll(setAth);
		
		for (Athlete a : listAth)
		{
			String createData = "INSERT INTO ATHLETE (nom, prenom,  genre, annee_Naissance, taille, poids)" + "VALUES ('" + a.getNom() + "','" + a.getPrenom() + "','"
				+ a.getGenre() + "','" + a.getAnnee_Naissance()  + "','" + a.getTaille() + "','" + a.getPoids() + "')";
		stmt.execute(createData);
		}
	
		
		
	
		
	
		
	
		
		
		

		
		 
		 
		
	
			
		
		}
		
		

	
}
