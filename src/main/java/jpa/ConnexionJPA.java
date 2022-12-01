package jpa;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import classJO.Athlete;
import classJO.Competition;
import classJO.DonneeBrut;
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

	//  DonneeBrut.traiterDDB(em);
	//	Pays.traiterPays(em);
	//	Equipe.traiterEquipe(em);
	//	Sport.traiterSport(em);
	//	Epreuve.traiterEpreuve(em);
	//	Competition.traiterCompet(em);
	//	Athlete.traiterAth(em);
	//	Medaille.traiterMedaille(em);
	//	Competition.recupEquipe(em);
	//	Competition.recupSport(em);
	//	Competition.recupEpreuve(em);
	//	Equipe.recupAth(em);
	//	Pays.recupAth(em);
		transaction.commit();
	}

}
