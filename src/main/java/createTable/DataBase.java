package createTable;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.mariadb.jdbc.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class DataBase {

	public static void main(String args[]) throws SQLException, ClassNotFoundException {
		ResourceBundle config = ResourceBundle.getBundle("propriete");
		String driver = config.getString("database.driver");
		String url = config.getString("database.url");
		String user = config.getString("database.user");
		String mdp = config.getString("database.password");

		Class.forName(driver);

		Connection connect = (Connection) DriverManager.getConnection(url, user, mdp);

		Statement stmt = connect.createStatement();

		List<String> createTable = new ArrayList<String>();

		String createAthlete = "CREATE TABLE ATHLETE " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ " nom VARCHAR(50), " + "genre CHAR(1), " + "age INTEGER(2)," + "taille INTEGER(3),"
				+ "poids INTEGER(3)" + ")";

		createTable.add(createAthlete);

		String createPays = "CREATE TABLE PAYS " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ "CIO_Code  INTEGER (11)," + " nom_FR VARCHAR(50), " + " nom_ENG VARCHAR(50), "
				+ "code_ISO_Alpha3 CHAR(3)," + "obsolète BOOLEAN" + ")";

		createTable.add(createPays);

		String createSport = "CREATE TABLE SPORT " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ " nom_FR VARCHAR(50), " + "nom_ENG VARCHAR(50) " + ")";

		createTable.add(createSport);

		String createEpreuve = "CREATE TABLE EPREUVE " + "(id  INTEGER (11) PRIMARY KEY AUTO_INCREMENT,"
				+ " nom_FR VARCHAR(50), " + "nom_ENG VARCHAR(50), " + "id_Sport INTEGER(11),"
				+ "CONSTRAINT FK_EPREUVE_SPORT FOREIGN KEY (id_Sport) REFERENCES SPORT(ID)" + ")";
		createTable.add(createEpreuve);

		String createEquipe = "CREATE TABLE EQUIPE " + "(id  INTEGER PRIMARY KEY AUTO_INCREMENT," + " nom VARCHAR(50), "
				+ "CIO_Code_Pays INTEGER(11),"
				+ "CONSTRAINT FK_EQUIPE_PAYS FOREIGN KEY (CIO_Code_Pays) REFERENCES PAYS(id)" + ")";
		createTable.add(createEquipe);

		String createCompet = "CREATE TABLE COMPETITION " + "(id  INTEGER PRIMARY KEY AUTO_INCREMENT," + " year YEAR, "
				+ "saison VARCHAR (5)," + "ville VARCHAR(100)" + ")";
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
		
		stmt.execute("DROP TABLE EPREUVE_COMPETITION");
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
		stmt.execute("DROP TABLE ATHLETE");

		for (String req : createTable) {
			stmt.execute(req);
		}

	}

}
