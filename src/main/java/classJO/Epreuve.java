package classJO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import createTable.DataBase;

/**
 * @author Manuel Rougé
 *
 */
@Entity
/**
 * classe relié à la table Epreuve
 */
@Table(name = "Epreuve")
public class Epreuve {

	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/**
	 * colonne nom_FR de l'équipe de taille 100
	 */
	@Column(name = "nom_FR", length = 100)
	private String nom_FR;
	/**
	 * colonne nom _ENG de l'équipe de taille 100 et non null
	 */
	@Column(name = "nom_ENG", length = 100, nullable = false)
	private String nom_ENG;
	/**
	 * liaison Plusieurs Epreuve pour 1 sport sur la colonne Id_Sport
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Id_Sport")
	private Sport sport;
	/**
	 * liaison plusieurs epreuves pour plusieurs competition sur la table
	 * EPREUVE_COMPETITION
	 */
	@ManyToMany
	@JoinTable(name = "EPREUVE_COMPETITION", joinColumns = @JoinColumn(name = "id_Epreuve", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"))
	private List<Competition> competition;
	/**
	 * liaison une épreuve pour plusieurs médailles
	 */
	@OneToMany(mappedBy = "epreuve")
	private List<Medaille> medaille;

	/**
	 * Constructeur vide de la classe Epreuve
	 */
	public Epreuve() {

	}

	/**
	 * method qui permet de remplir la table Epreuve de la base de donnée
	 */
	public static void traiterEpreuve(EntityManager em) throws ClassNotFoundException, SQLException, IOException {

		HashMap<String, String> mapEpreuve = new HashMap<>();
		/**
		 * Appel de la méthode recupFichier pour traiter le document csv lié aux
		 * épreuves
		 */
		List<String> lines = DataBase.recupFichier("liste_des_epreuves");
		/** Appel de la méthode recupFichier pour traiter le document csv */
		List<String> linesAth = DataBase.recupFichier("athlete_epreuves");
		/** On lit chaque ligne du document */
		for (String ath : linesAth) {
			/** on récupère les infos important pour les épreuves */
			String[] arrayA = new String[15];
			for (int i = 0; i < ath.split(";").length; i++) {
				arrayA[i] = ath.split(";")[i];
			}
			String sport = arrayA[12];
			String ep = arrayA[13];
			ep = ep.replaceFirst(sport, "").trim();
			/** on pousse dans une map pour éviter les doublons */
			mapEpreuve.put(ep, sport);

		}
		List<Epreuve> listEp = new ArrayList<Epreuve>();
		/** on parcours la map */
		Iterator<Entry<String, String>> it = mapEpreuve.entrySet().iterator();
		while (it.hasNext()) {
			/**
			 * on récupère le sport de la base de donnée lié à l'équipe qu'on parcours
			 * actuellement
			 */
			Map.Entry<String, String> entry = (Entry<String, String>) it.next();
			String requete = "SELECT * FROM Sport as s where s.nom_ENG=?";
			@SuppressWarnings("unchecked")
			List<Sport> classSport = em.createNativeQuery(requete, Sport.class).setParameter(1, entry.getValue())
					.getResultList();
			/** on instancie l'épreuve */
			Epreuve epreuve = new Epreuve();
			/** on lui ajoute son nom anglais */
			epreuve.setNom_ENG(entry.getKey());
			/** on vérifie si le sport existe */
			if (classSport.size() > 0) {
				/** on lui ajoute son sport */
				epreuve.setSport(classSport.get(0));

			}

			/** si l'épreuve à un nom anglais ont l'ajoute à la BDD */
			if (epreuve.getNom_ENG() != null && !(epreuve.getNom_ENG().isEmpty())) {

				em.persist(epreuve);
				/** et à la liste des épreuves */
				listEp.add(epreuve);
			}

		}
		/**
		 * pour lui ajouter son nom français, on parcours la liste des épreuves
		 * préalablement ajoutée
		 */
		for (Epreuve e : listEp) {
			/** on parcours le fichier de traduction des épreuves */
			for (String l : lines) {
				/** on le divise en nom anglais / nom français */
				String[] arrayS = new String[2];
				for (int i = 0; i < l.split(";").length; i++) {
					arrayS[i] = l.split(";")[i];

				}
				String nom_Fr = arrayS[1];
				String nom_En = arrayS[0];
				/** si le nom français n'existe pas on lui donne le nom anglais */
				if (nom_Fr == null || nom_Fr.length() == 0) {
					nom_Fr = nom_En;
				}
				/** si le nom anglais existe on ajoute le nom français à la BDD */
				if (nom_En != null && nom_En.length() > 0)
					if (nom_En.equals(e.getNom_ENG())) {
						e.setNom_FR(nom_Fr);
					}
			}
		}
	}

	/**
	 * @return le nom français de l'épreuve
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
	 * @return le nom anglais de l'épreuve
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
	 * @return le sport associé à l'épreuve (utilisé la méthod) 
	 */
	public Sport getSport() {
		return sport;
	}

	/**
	 * @param sport
	 */
	public void setSport(Sport sport) {
		this.sport = sport;
	}
}
