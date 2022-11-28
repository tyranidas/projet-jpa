package createTable;

import org.mariadb.jdbc.Connection;

import classJO.Athlete;
import classJO.Epreuve;
import classJO.Pays;
import classJO.Sport;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DataBase {

	public static void main(String args[]) throws SQLException, ClassNotFoundException, IOException 
	{

		Statement stmt = connectionDB();

		List<String> createTable = new ArrayList<String>();

		String createAthlete = "CREATE TABLE ATHLETE " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ " nom VARCHAR(50), " + " prenom VARCHAR(50), " + "genre CHAR(1), " + "annee_Naissance INTEGER(4),"
				+ "taille INTEGER(3)," + "poids INTEGER(3)" + ")";

		createTable.add(createAthlete);

		String createPays = "CREATE TABLE PAYS " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ "CIO_Code  CHAR (3)," + " nom_FR VARCHAR(50), " + " nom_ENG VARCHAR(50), "
				+ "code_ISO_Alpha3 CHAR(3)," + "obsolète CHAR(1)" + ")";

		createTable.add(createPays);

		String createSport = "CREATE TABLE SPORT " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ " nom_FR VARCHAR(50), " + "nom_ENG VARCHAR(50) " + ")";

		createTable.add(createSport);

		String createEpreuve = "CREATE TABLE EPREUVE " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ " nom_FR VARCHAR(50), " + "nom_ENG VARCHAR(50), " + "id_Sport INTEGER(11),"
				+ "CONSTRAINT FK_EPREUVE_SPORT FOREIGN KEY (id_Sport) REFERENCES SPORT(ID)" + ")";
		createTable.add(createEpreuve);

		String createEquipe = "CREATE TABLE EQUIPE " + "(id  INTEGER PRIMARY KEY AUTO_INCREMENT," + " nom VARCHAR(50), "
				+ "id_Pays INTEGER(11)," + "CONSTRAINT FK_EQUIPE_PAYS FOREIGN KEY (id_Pays) REFERENCES PAYS(id)" + ")";
		createTable.add(createEquipe);

		String createCompet = "CREATE TABLE COMPETITION " + "(id  INTEGER PRIMARY KEY AUTO_INCREMENT,"
				+ " edition INTEGER(4), " + "saison VARCHAR (6)," + "ville VARCHAR(100)," + "id_Pays INTEGER(11)" + ")";
		createTable.add(createCompet);

		String createMedaille = "CREATE TABLE MEDAILLE " + "(id  INTEGER PRIMARY KEY AUTO_INCREMENT,"
				+ " type VARCHAR(6), " + "id_Athlete INTEGER(11)," + "id_Epreuve INTEGER(11),"
				+ "id_Competition INTEGER(11),"
				+ "CONSTRAINT FK_MED_ATH FOREIGN KEY (id_Athlete) REFERENCES ATHLETE(id),"
				+ "CONSTRAINT FK_MED_EPR FOREIGN KEY (id_Epreuve) REFERENCES EPREUVE(id),"
				+ "CONSTRAINT FK_MED_COMP FOREIGN KEY (id_Competition) REFERENCES COMPETITION(id)" + ")";
		createTable.add(createMedaille);

		String createPaysAth = "CREATE TABLE PAYS_ATHLETE " + "(id_Athlete INTEGER(11)," + "id_Pays INTEGER(11),"
				+ "CONSTRAINT FK_PAY_ATH_ATH FOREIGN KEY (id_Athlete) REFERENCES ATHLETE(id),"
				+ "CONSTRAINT FK_PAY_ATH_PAY FOREIGN KEY (id_Pays) REFERENCES PAYS(id),"
				+ "CONSTRAINT PK_ATH_PAYS PRIMARY KEY (id_Athlete, id_Pays)" + ")";
		createTable.add(createPaysAth);

		String createEquipeCompet = "CREATE TABLE EQUIPE_COMPETITION " + "(id_Equipe INTEGER(11),"
				+ "id_Competition INTEGER(11),"
				+ "CONSTRAINT FK_EQUI_COMP_EQUI FOREIGN KEY (id_Equipe) REFERENCES EQUIPE(id),"
				+ "CONSTRAINT FK_EQUI_COMP_COMP FOREIGN KEY (id_Competition) REFERENCES COMPETITION(id),"
				+ "CONSTRAINT PK_EQUI_COMP PRIMARY KEY (id_Equipe, id_Competition)" + ")";
		createTable.add(createEquipeCompet);

		String createEquipeAthlet = "CREATE TABLE EQUIPE_ATHLETE " + "(id_Equipe INTEGER(11),"
				+ "id_Athlete INTEGER(11),"
				+ "CONSTRAINT FK_EQUI_ATH_EQUI FOREIGN KEY (id_Equipe) REFERENCES EQUIPE(id),"
				+ "CONSTRAINT FK_EQUI_ATH_ATH FOREIGN KEY (id_Athlete) REFERENCES ATHLETE(id),"
				+ "CONSTRAINT PK_EQUI_ATH PRIMARY KEY (id_Equipe, id_Athlete)" + ")";
		createTable.add(createEquipeAthlet);

		String createSportComp = "CREATE TABLE SPORT_COMPETITION " + "(id_Sport INTEGER(11),"
				+ "id_Competition INTEGER(11),"
				+ "CONSTRAINT FK_SPO_COMP_SPO FOREIGN KEY (id_Sport) REFERENCES SPORT(id),"
				+ "CONSTRAINT FK_SPO_COMP_COMP FOREIGN KEY (id_Competition) REFERENCES COMPETITION(id),"
				+ "CONSTRAINT PK_SPO_COMP PRIMARY KEY (id_Sport, id_Competition)" + ")";
		createTable.add(createSportComp);

		String createEpreuvComp = "CREATE TABLE EPREUVE_COMPETITION " + "(id_Epreuve INTEGER(11),"
				+ "id_Competition INTEGER(11),"
				+ "CONSTRAINT FK_EPR_COMP_EPR FOREIGN KEY (id_Epreuve) REFERENCES EPREUVE(id),"
				+ "CONSTRAINT FK_EPR_COMP_COMP FOREIGN KEY (id_Competition) REFERENCES COMPETITION(id),"
				+ "CONSTRAINT PK_EPR_COMP PRIMARY KEY (id_Epreuve, id_Competition)" + ")";
		createTable.add(createEpreuvComp);

	/*	stmt.execute("DROP TABLE EPREUVE_COMPETITION");
		stmt.execute("DROP TABLE SPORT_COMPETITION");
		stmt.execute("DROP TABLE EQUIPE_ATHLETE");
		stmt.execute("DROP TABLE EQUIPE_COMPETITION");
		stmt.execute("DROP TABLE PAYS_ATHLETE");
		stmt.execute("DROP TABLE MEDAILLE");
		stmt.execute("DROP TABLE COMPETITION");
		stmt.execute("DROP TABLE EQUIPE");
		stmt.execute("DROP TABLE EPREUVE");
		stmt.execute("DROP TABLE SPORT");
		stmt.execute("DROP TABLE PAYS");
		stmt.execute("DROP TABLE ATHLETE");*/

	for (String req : createTable) {
			stmt.execute(req);
		}
	/*	Pays.traiterPays();
		Epreuve.traiterEpreuve();
		Sport.traiterSport();
		Athlete.traiterAth();*/
		

	}
	
	public static void createDBBRute() throws ClassNotFoundException, SQLException, IOException
	{
		connectionDB();
		
		recupFichier("athlete_epreuves");
		
		String createAthlete = "CREATE TABLE DB " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ " id"
				+ " nom VARCHAR(50), " + " prenom VARCHAR(50), " + "genre CHAR(1), " + "annee_Naissance INTEGER(4),"
				+ "taille INTEGER(3)," + "poids INTEGER(3)" + ")";
		
	}
	

	public static Statement connectionDB() throws ClassNotFoundException, SQLException 
	{
		ResourceBundle config = ResourceBundle.getBundle("propriete");
		String driver = config.getString("database.driver");
		String url = config.getString("database.url");
		String user = config.getString("database.user");
		String mdp = config.getString("database.password");
		Class.forName(driver);
		Connection connect = (Connection) DriverManager.getConnection(url, user, mdp);
		Statement stmt = connect.createStatement();
		return stmt;
	}

	public static List<String> recupFichier(String nomFichier  ) throws IOException 
	{
		Path path = Paths.get(".\\src\\main\\resources\\" + nomFichier + ".csv");
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		lines.remove(0);
		return lines;
		
		
	}
	
	public static EntityManager connectionDBem ()
	{
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Jeux_Olympiques");
		EntityManager em = entityManagerFactory.createEntityManager();
		return em;
	}
	
}
