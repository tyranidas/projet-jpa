package createTable;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import classJO.Athlete;
import classJO.Competition;

public class DataBaseCompet {

	@SuppressWarnings("unlikely-arg-type")
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		Statement stmt = DataBase.connectionDB();

		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		lines.remove(0);
		ArrayList<Competition> listComp = new ArrayList<Competition>();
		HashSet<Competition> setComp = new LinkedHashSet<>();
		
		for (String l : lines) {
			l = l.replace("'", "\\'");
			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
				
			}
			Integer dateJO  = Integer.parseInt(arrayS[9]);
			
			
	
			
			String saison = arrayS[10];
			String ville = arrayS[11];
			
			Competition comp = new Competition(dateJO, saison, ville);
			listComp.add(comp);
			
			}
		
		
		
		
		for (Competition c : listComp)
		{
			
			if (c.equals(listComp) == false)
					{
				setComp.add(c);
					}
		}
		listComp.clear();
		listComp.addAll(setComp);
		
		
			
		for (Competition c : listComp)
		{
			String createData = "INSERT INTO COMPETITION (edition, saison, ville)" + "VALUES ('" + c.getYear() + "','"
					+ c.getSaison() + "','" + c.getVille() + "')";
		stmt.execute(createData);
		}

	}

}
