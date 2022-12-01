package classJO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import createTable.DataBase;

/**
 * @author Manuel Rougé
 *
 */
@Entity
@NamedQueries({
		/**
		 * Requête préparée pour un athlete selon son nom, son prénom (ou null), sa
		 * taile, son genre et son année de naissance
		 */
		@NamedQuery(name = "findAthByNom", query = "SELECT a FROM Athlete as a where a.nom =:nom "
				+ " and (a.prenom=:prenom or a.prenom IS NULL )"
				+ "and a.annee_Naissance = :naissance and a.taille = :taille " + "and a.genre = :genre"),
		/** Requête préparée pour une epreuve selon son nom anglais */
		@NamedQuery(name = "findEpreuveByNom", query = "SELECT e FROM Epreuve as e where e.nom_ENG=:ep"),
		/**
		 * Requête préparée pour une competition selon son année d'édition et sa ville
		 */
		@NamedQuery(name = "findCompetbyEditionAndVille", query = "SELECT c FROM Competition as c where c.ville=:ville and c.edition=:edition") })
/**
 * classe relié à la table Medaille
 */
@Table(name = "MEDAILLE")

public class Medaille {
	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/**
	 * colonne type de la medaille
	 */
	@Column(name = "type", length = 6)
	private String type;
	/**
	 * liaison Plusieurs medaille pour 1 athlete sur la colonne id_athlete
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Id_Athlete")
	private Athlete athlete;
	/**
	 * liaison Plusieurs medaille pour 1 athlete sur la colonne id_Epreuve
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Id_Epreuve")
	private Epreuve epreuve;
	/**
	 * liaison Plusieurs medaille pour 1 compétition sur la colonne id_Competition
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Id_Competition")
	private Competition competition;

	/**
	 * Constructeur vide de la classe Medaille
	 */
	public Medaille() {
	}

	/**
	 * method qui permet de remplir la table Equipe de la base de donnée
	 */
	public static void traiterMedaille(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		/** Appel de la méthode recupFichier pour traiter le document csv */
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		/** On lit chaque ligne du document */
		for (int j = 0; j < 100000; j++)
		// for (String s : lines)
		{	
			/** On divise la ligne en autant de partie que de colonne du tableau csv */
			String med = " ";
			String s = lines.get(j);
			String[] arrayA = new String[15];
			for (int i = 0; i < s.split(";").length; i++) {
				arrayA[i] = s.split(";")[i];
			}
			med = arrayA[14];
			/** on remplie uniquement si la ligne contient une médaille */
			if (med.equals("NA") == false) {
				/** on récupère l'athlete de la ligue sur la BDD*/
				Athlete athlete = Athlete.createOneAth(s);
				/** on instancie l'athlete trouvé */
				Athlete ath = em.createNamedQuery("findAthByNom", Athlete.class).setParameter("nom", athlete.getNom())
						.setParameter("prenom", athlete.getPrenom())
						.setParameter("naissance", athlete.getAnnee_Naissance())
						.setParameter("taille", athlete.getTaille()).setParameter("genre", athlete.getGenre())
						.getSingleResult();
				
				/** on récupère les infos pour l'épreuve */
				String sport = arrayA[12];
				String ep = arrayA[13];
				ep = ep.replaceFirst(sport, "").trim();
				//* On instancie l'épreuve trouvée
				Epreuve epreuve = em.createNamedQuery("findEpreuveByNom", Epreuve.class).setParameter("ep", ep)
						.getSingleResult();
				/** on récupère les infos pour la compétition */
				String ville = arrayA[11];
				Integer annee = Integer.parseInt(arrayA[9]);
				/** on instancie la compétition trouvée */
				Competition compet = em.createNamedQuery("findCompetbyEditionAndVille", Competition.class)
						.setParameter("ville", ville).setParameter("edition", annee).getSingleResult();
				/** on instancie la médaille et on lui ajoute ses infos */
				Medaille medaille = new Medaille();
				medaille.setAthlete(ath);
				medaille.setCompetition(compet);
				medaille.setEpreuve(epreuve);
				medaille.setType(med);
				/** on ajoute la médaille à la BDD */
				em.persist(medaille);
			}
		}
	}

	/**
	 * @return Id de la médaille
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return le type de médaille (or, argent, bronze)
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return l'athlète qui l'a gagné
	 */
	public Athlete getAthlete() {
		return athlete;
	}

	/**
	 * @param athlete
	 */
	public void setAthlete(Athlete athlete) {
		this.athlete = athlete;
	}

	/**
	 * @return dans quelle épreuve a été gagnée la médaille 
	 */
	public Epreuve getEpreuve() {
		return epreuve;
	}

	/**
	 * @param epreuve
	 */
	public void setEpreuve(Epreuve epreuve) {
		this.epreuve = epreuve;
	}
	/**
	 * @return dans quelle compétition a été gagnée la médaille 
	 */
	public Competition getCompetition() {
		return competition;
	}

	/**
	 * @param competition
	 */
	public void setCompetition(Competition competition) {
		this.competition = competition;
	}

}
