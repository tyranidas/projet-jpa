package classJO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
		 * Requête préparée pour sélectionner toutes les équipes depuis la base de
		 * donnée
		 */
		@NamedQuery(name = "findAllEquipe", query = "Select e FROM Equipe as e"),
		/**
		 * Requête préparée pour un athlete selon son nom, son prénom, sa taile, son
		 * poids, son genre et son année de naissance
		 */
		@NamedQuery(name = "findAthByNomForEq", query = "SELECT a FROM Athlete as a where a.nom =:nom "
				+ " and a.prenom=:prenom " + "and a.taille = :taille " + "and a.poids = :poids "
				+ "and a.genre = :genre " + "and a.annee_Naissance = :age"),
		/**
		 * Requête préparée pour un athlete selon son nom, s'il n'a pas de prénom (C'est
		 * Juste) , sa taile, son poids, son genre et son année de naissance
		 */
		@NamedQuery(name = "findAthByNomForEqIfNull", query = "SELECT a FROM Athlete as a where a.nom =:nom "
				+ " and  a.prenom IS NULL " + "and a.taille = :taille " + "and a.poids = :poids "
				+ "and a.genre = :genre " + "and a.annee_Naissance = :age") })
/**
 * classe relié à la table Equipe
 */
@Table(name = "Equipe")

public class Equipe {

	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/**
	 * colonne nom de l'équipe de taille 100 et non null
	 */
	@Column(name = "nom", length = 100, nullable = false)
	private String nom;

	/**
	 * liaison Plusieurs Equipe pour 1 pays sur la colonne Id_Pays
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Id_Pays")
	private Pays equiPays;

	/**
	 * liaison plusieurs équipes pour plusieurs athlete sur la table EQUIPE_ATHLETE
	 */
	@ManyToMany
	@JoinTable(name = "EQUIPE_ATHLETE", joinColumns = @JoinColumn(name = "id_Equipe", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Athlete", referencedColumnName = "id"))
	private List<Athlete> athlete;

	/**
	 * laison plusieurs équipes pour plusieurs compétition sur la table
	 * EQUIPE_COMPETITION
	 */
	@ManyToMany
	@JoinTable(name = "EQUIPE_COMPETITION", joinColumns = @JoinColumn(name = "id_Equipe", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"))
	private List<Competition> competition;

	/**
	 * Constructeur vide de la classe Equipe
	 */
	public Equipe() {
	}

	/**
	 * method qui permet de remplir la table Equipe de la base de donnée
	 */
	public static void traiterEquipe(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		/** Appel de la méthode recupFichier pour traiter le document csv */
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		HashMap<String, String> mapEqui = new HashMap<>();
		/** On lit chaque ligne du document */
		for (String l : lines) {

			/** On divise la ligne en autant de partie que de colonne du tableau csv */
			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
			}
			/** On récupère le nom de l'équipe */
			String nom = arrayS[6];
			/**
			 * On enlève les guillements qui peuvent perturber les futures requetes JPA/SQL
			 */
			nom = nom.replace("\"", "");
			/** On récupère le pays de l'équipe */
			String pays = arrayS[7];
			/** On les ajoute a une HasHmap pour éviter les doublons */
			mapEqui.put(nom, pays);
		}

		/**
		 * on parcours la HashMap contenant toutes les équipes et leur pays respectifs
		 */
		Iterator<Entry<String, String>> it = mapEqui.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Entry<String, String>) it.next();
			/** on récupère les informations du pays de l'équipe dans la base de donnée */
			String requete = "SELECT * FROM Pays as p where p.CIO_Code=?";

			/** On instancie l'équipe */
			Equipe equipe = new Equipe();
			equipe.setNom(entry.getKey());
			@SuppressWarnings("unchecked")
			List<Pays> classPays = em.createNativeQuery(requete, Pays.class).setParameter(1, entry.getValue())
					.getResultList();
			;
			/** on ajoute le pays à l'équipe s'il n'est pas vide */
			if (classPays.size() > 0) {
				equipe.setPays(classPays.get(0));
			}
			/** On ajoute l'équipe à la base de donnée */
			em.persist(equipe);
		}
	}

	/** méthode pour récupérer l'athlète lié à l'équipe */
	public static void recupAth(EntityManager em) throws ClassNotFoundException, SQLException {
		/** on récupère toutes les équipes */
		List<Equipe> allEq = em.createNamedQuery("findAllEquipe", Equipe.class).getResultList();
		/** Pour chaque équipe */
		for (int i = 0; i < 100; i++) {
//allEq.size()
			Statement stmt = DataBase.connectionDB();
			System.out.println(allEq.get(i).getNom());
			/** On récupère les athlètes étant dans cette équipe */
			String query = "Select name, height, weight, sex, age, year from donnee_brute where team = \""
					+ allEq.get(i).getNom() + "\"";
			ResultSet rs = stmt.executeQuery(query);
			List<Athlete> listAth = Athlete.findAth(rs, em);
			/** On met à jour l'équipe */
			allEq.get(i).setAthlete(listAth);
		}
	}

	/**
	 * @return athlete
	 */
	public List<Athlete> getAthlete() {
		return athlete;
	}

	/**
	 * @param athlete
	 */
	public void setAthlete(List<Athlete> athlete) {
		this.athlete = athlete;
	}

	/**
	 * @return pays
	 */
	public Pays getPays() {
		return equiPays;
	}

	/**
	 * @param pays
	 */
	public void setPays(Pays pays) {
		this.equiPays = pays;
	}

	/**
	 * @return Nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}

	/**
	 * vérifie si déjà existant
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Equipe other = (Equipe) obj;
		return Objects.equals(nom, other.nom) && Objects.equals(equiPays, other.equiPays);
	}

}
