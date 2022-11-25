package jpa;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import classJO.Athlete;
import classJO.Competition;
import classJO.Epreuve;
import classJO.Equipe;
import classJO.Medaille;
import classJO.Pays;
import classJO.Sport;
import createTable.DataBase;

public class ConnexionJPA {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		EntityManager em = DataBase.connectionDBem();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

	
		Pays.traiterPays(em);
	Equipe.traiterEquipe(em);
	Sport.traiterSport(em);
		Epreuve.traiterEpreuve(em);
		Competition.traiterCompet(em);
	//	Athlete.recupAth(em);
	//	Medaille.traiterMedaille(em);
		transaction.commit();
	}

}
