package classJO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Table;

import createTable.DataBase;

/**
 * @author Manuel Rougé
 *
 */
@Entity

@NamedQueries({
	/** Requête préparée pour récupérer tout les Pays */
		@NamedQuery(name = "findAllPays", query = "Select p FROM Pays as p"),
		/**
		 * Requête préparée pour un athlete selon son nom, son prénom, sa taile, son
		 * poids, son genre et son année de naissance
		 */
		@NamedQuery(name = "findAthByNomForPays", query = "SELECT a FROM Athlete as a where a.nom =:nom "
				+ " and a.prenom=:prenom " + "and a.taille = :taille " + "and a.poids = :poids "
				+ "and a.genre = :genre " + "and a.annee_Naissance = :age"),
		/**
		 * Requête préparée pour un athlete selon son nom, s'il n'a pas de prénom (C'est
		 * Juste) , sa taile, son poids, son genre et son année de naissance
		 */
		@NamedQuery(name = "findAthByNomForPaysIfNull", query = "SELECT a FROM Athlete as a where a.nom =:nom "
				+ " and  a.prenom IS NULL " + "and a.taille = :taille " + "and a.poids = :poids "
				+ "and a.genre = :genre " + "and a.annee_Naissance = :age") })
/**
 * classe relié à la table Pays
 */
@Table(name = "PAYS")
public class Pays {
	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/**
	 * colonne Code Cio du pays de taile 3 non null
	 */
	@Column(name = "CIO_Code", length = 3, nullable = false)
	private String cio_Code;
	/**
	 * colonne nom français du pays de taile 50 non null
	 */
	@Column(name = "nom_FR", length = 50, nullable = false)
	private String nom_FR;
	/**
	 * colonne nom anglais du pays de taile 50 non null
	 */
	@Column(name = "nom_ENG", length = 50, nullable = false)
	private String nom_ENG;
	/**
	 * colonnecode ISO du pays de taile 3 
	 */
	@Column(name = "code_ISO_Alpha3", length = 3)
	private String iso;
	/**
	 * si le pays existe encore ou pas, taile 1 et non null
	 */
	@Column(name = "obsolète", length = 1, nullable = false)
	private String obs;

	/**
	 * liaison plusieurs pays pour plusieurs athlete sur la table PAYS_ATHLETE
	 */
	@ManyToMany
	@JoinTable(name = "PAYS_ATHLETE", joinColumns = @JoinColumn(name = "id_Pays", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Athlete", referencedColumnName = "id"))
	private List<Athlete> athlete;
	/** Liste des Equipes ayant jouées pour ce pays */
	@OneToMany(mappedBy = "equiPays")
	private List<Equipe> equipe;
	/**
	 * Constructeur vide de la classe Pays
	 */
	public Pays() {

	}

	@SuppressWarnings("unlikely-arg-type")
	public static void traiterPays(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		/** On récupère la liste des pays, pas avec la méthode habituel car pas le même encodage */
		Path path = Paths.get(".\\src\\main\\resources\\wikipedia_iso_country_codes.csv");
		List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
		lines.remove(0);
		ArrayList<Pays> listPays = new ArrayList<Pays>();
		HashSet<Pays> setPays = new LinkedHashSet<>();
		/** pour chaque ligne du fichier */
		for (String l : lines) {
			l = l.replace("'", "\\'");
			/** on divise en autant de string que de colonnes */
			String[] arrayS = new String[5];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];

			}
			String cio_Code = arrayS[0];
			String nom_FR = arrayS[1];
			String nom_ENG = arrayS[2];
			String iso = arrayS[3];
			String obs = arrayS[4];
			/* on instancie le pays */
			Pays pays = new Pays();
			pays.setCio_Code(cio_Code);
			pays.setNom_FR(nom_FR);
			pays.setNom_ENG(nom_ENG);
			pays.setIso(iso);
			pays.setObs(obs);
			/** on ajoute le pays a une liste */
			listPays.add(pays);

		}
		/** on évite les doublons */
		for (Pays p : listPays) {
			if (p.equals(listPays) == false) {
				setPays.add(p);
			}
		}
		listPays.clear();
		listPays.addAll(setPays);
		/** on ajoute le pays a la BDD */
		for (Pays p : listPays) {
			em.persist(p);
		}
	}
	/** méthode pour récupérer l'athlète lié au pays */
	public static void recupAth(EntityManager em) throws ClassNotFoundException, SQLException {
		/** on récupère tous les pays */
		List<Pays> allPays = em.createNamedQuery("findAllPays", Pays.class).getResultList();
		/** Pour chaque pays */
		for (int i = 0; i < 100; i++)
		// allPays.size()
		{
			Statement stmt = DataBase.connectionDB();
			/** On récupère les athlètes étant dans ce Pays*/
			String query = "Select name, height, weight, sex, age, year from donnee_brute where noc = \""
					+ allPays.get(i).getCio_Code() + "\"";
			ResultSet rs = stmt.executeQuery(query);
			List<Athlete> listAth = Athlete.findAth(rs, em);
			/** On met à jour le pays */
			allPays.get(i).setAthlete(listAth);
		}
	}

	/**
	 * @return id
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
	 * @return code pays du CIO
	 */
	public String getCio_Code() {
		return cio_Code;
	}

	/**
	 * @param cio_Code
	 */
	public void setCio_Code(String cio_Code) {
		this.cio_Code = cio_Code;
	}

	/**
	 * @return le nom français du Pays
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
	 * @return le nom anglais du pays
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
	 * @return le code ISO du pays
	 */
	public String getIso() {
		return iso;
	}

	/**
	 * @param iso
	 */
	public void setIso(String iso) {
		this.iso = iso;
	}

	/**
	 * @return si le pays existe encore
	 */
	public String getObs() {
		return obs;
	}

	/**
	 * @param obs
	 */
	public void setObs(String obs) {
		this.obs = obs;
	}

	/**
	 * @return la liste des athlètes ayant concourru pour ce pays
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
	 * @returnla liste des équipes ayant concourru pour ce pays
	 */
	public List<Equipe> getEquipe() {
		return equipe;
	}

	/**
	 * @param equipe
	 */
	public void setEquipe(List<Equipe> equipe) {
		this.equipe = equipe;
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
		Pays other = (Pays) obj;
		return Objects.equals(cio_Code, other.cio_Code) && Objects.equals(iso, other.iso)
				&& Objects.equals(nom_ENG, other.nom_ENG) && Objects.equals(nom_FR, other.nom_FR);
	}
}
