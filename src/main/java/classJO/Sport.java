package classJO;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import createTable.DataBase;

/**
 * @author Manuel Rougé
 *
 */
@Entity
/**
 * classe relié à la table Sport
 */
@Table(name = "SPORT")
public class Sport {

	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/**
	 * colonne nom français du sport de taille 50 et non null
	 */
	@Column(name = "nom_FR", length = 50, nullable = false)
	private String nom_FR;
	/**
	 * colonne nom anglais du sport de taille 50 et non null
	 */
	@Column(name = "nom_ENG", length = 50, nullable = false)
	private String nom_ENG;

	/** laison un sport contenant une liste d'épreuve */
	@OneToMany(mappedBy = "sport")
	private List<Epreuve> epreuve;

	/**
	 * laison plusieurs sports pour plusieurs compétition sur la table
	 * SPORT_COMPETITION
	 */
	@ManyToMany
	@JoinTable(name = "SPORT_COMPETITION", joinColumns = @JoinColumn(name = "id_Sport", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"))
	private List<Competition> competition;

	/**
	 * Constructeur vide de la classe Sport
	 */
	public Sport() {

	}

	/**
	 * method qui permet de remplir la table Sport de la base de donnée
	 */
	public static void traiterSport(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		/**
		 * Appel de la méthode recupFichier pour traiter le document csv lié aux noms
		 * des sports
		 */
		List<String> lines = DataBase.recupFichier("liste des sports");
		/** On lit chaque ligne du document */
		for (String l : lines) {
			/** On divise la ligne en autant de partie que de colonne du tableau csv */
			l = l.replace("'", "\\'");
			String[] arrayS = new String[2];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
			}
			/** On récupère les noms anglais et français du sport */
			String nom_Fr = arrayS[1];
			String nom_En = arrayS[0];
			/** On instancie le sport */
			Sport sport = new Sport();
			/** si le nom français n'existe pas on lui donne le nom anglais */
			if (nom_Fr == null || nom_Fr.length() == 0) {
				nom_Fr = nom_En;
			}
			
			sport.setNom_FR(nom_Fr);
			sport.setNom_ENG(nom_En);
			/** On ajoute le sport à la BDD*/
			em.persist(sport);

		}
	}

	/**
	 * @return nom français du sport
	 */
	public String getNom_FR() {
		return nom_FR;
	}

	/**
	 * @param nom_FR
	 */
	public void setNom_FR(String nom_FR) {
		this.nom_FR = nom_FR;
	}

	/**
	 * @return nom anglais du sport
	 */
	public String getNom_ENG() {
		return nom_ENG;
	}

	/**
	 * @param nom_ENG
	 */
	public void setNom_ENG(String nom_ENG) {
		this.nom_ENG = nom_ENG;
	}

	/**
	 * @return la liste des épreuves de ce sport 
	 */
	public List<Epreuve> getEpreuve() {
		return epreuve;
	}

	/**
	 * @param epreuve
	 */
	public void setEpreuve(List<Epreuve> epreuve) {
		this.epreuve = epreuve;
	}

	/**
	 * @return la liste des compétition contenant ce sport
	 */
	public List<Competition> getCompetition() {
		return competition;
	}

	/**
	 * @param competition
	 */
	public void setCompetition(List<Competition> competition) {
		this.competition = competition;
	}

	/**
	 * affiche quel sport c'est (français et anglais)
	 */
	@Override
	public String toString() {
		return "Sport [nom_FR=" + nom_FR + ", nom_ENG=" + nom_ENG + "]";
	}

}
