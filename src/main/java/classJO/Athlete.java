package classJO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
/** Requête préparée pour un pays selon son code CIO*/
@NamedQueries({
	@NamedQuery(name="findPaysByName", query = "SELECT p FROM Pays as p where p.cio_Code =:pays"),
})
@Table(name="ATHLETE")
public final class Athlete {

	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	/**
	 * colonne nom de l'athlete de taille 50 et non null
	 */
	@Column (name="nom", length = 50, nullable = false)	
	private String nom;
	
	/**
	 * colonne prénom de l'athlete de taille 100 
	 */
	@Column (name="prenom", length = 100)	
	private String prenom;
	
	/**
	 * colonne genre de l'athlete de taille 1
	 */
	@Column (name="genre", length = 1)	
	private String genre;
	
	/**
	 * colonne année de naissance de l'athlete de taille 4
	 */
	@Column (name="annee_Naissance", length = 4)	
	private Integer annee_Naissance;
	
	/**
	 * colonne taile de l'athlete de taille 3 en cm
	 */
	@Column (name="taille", length = 3)	
	private Double taille;
	/**
	 * colonne poids de l'athlete de taille 3 en kg
	 */
	@Column (name="poids", length = 3)	
	private Double poids;
	
	/**
	 * liste des médailles gagnées par l'athlete
	 */
	@OneToMany(mappedBy = "athlete")
	private List<Medaille> medaille;
	/**
	 * liste des équipes où l'athlete a été
	 */
	@ManyToMany
	@JoinTable (name="EQUIPE_ATHLETE",
	joinColumns= @JoinColumn(name="id_Athlete", referencedColumnName ="id"),
	inverseJoinColumns= @JoinColumn(name="id_Equipe", referencedColumnName = "id")
	)
	private List<Equipe> equipe;
	/**
	 * liste des pays quel'athlete a représenté
	 */
	@ManyToMany
	@JoinTable (name="PAYS_ATHLETE",
	joinColumns= @JoinColumn(name="id_Athlete", referencedColumnName ="id"),
	inverseJoinColumns= @JoinColumn(name="id_Pays", referencedColumnName = "id")
	)
	private List<Pays> pays;
	
	/**
	 * Constructeur vide de la classe Athlete
	 */
	public Athlete()
	{
	}
	
	/** method qui permet de remplir la table Athlete de la base de donnée */
	@SuppressWarnings("unlikely-arg-type")
	public static void traiterAth (EntityManager em) throws IOException
	{
		/** Appel de la méthode recupFichier pour traiter le document csv */
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		ArrayList<Athlete> listAth = new ArrayList<Athlete>();
		Set<Athlete> setAth = new LinkedHashSet<>();
		/** On lit chaque ligne du document */	
		for (String l : lines) {
			/** Appel de la méthode createOneAth pour créer UN athlete */
			Athlete athlete = createOneAth(l);
			listAth.add(athlete);
		}
		/** On élimine les doublons */
		for (Athlete a : listAth)
		{
			if (a.equals(listAth)==false)
			{
				setAth.add(a);
			}
		}
		listAth.clear();
		listAth.addAll(setAth);
		/** On créer l'athlète dans la base de donnée */
		for (Athlete a : listAth)
	{
		em.persist(a);
	}
	}
	/** Méthode pour créer un Athlete selon une ligne du fichier vsv */
	public static Athlete createOneAth (String l) {
		
		String[] arrayS = new String[15];
		/** on divise la ligne en 15 selon les ";" */
		for (int i = 0; i < l.split(";").length; i++) {
			arrayS[i] = l.split(";")[i];
			
		}
		/** instantiation des 7 paramètres de la classe Athlete */
		String nom = arrayS[1];
		String prenom = null;
		Integer intAge = 0;
		Integer naissance  = 0;
		double taille = 0;
		double poids = 0;
		String genre = arrayS[2];
		
		/** On récupère la date des JO afin de calculer l'année de naissance selon l'age */
		Integer DateJO = Integer.parseInt(arrayS[9]);
		if (arrayS[3].matches("-?\\d+"))
		{
		intAge = Integer.parseInt(arrayS[3]);
		naissance  = DateJO - intAge;		
		}
		/** si la taille n'est pas connue on la remplace par 0 sinon on récupère celle du fichier csv */
		if (!arrayS[4].equals("NA"))
		{
			arrayS[4] = arrayS[4].replace(".", ".5");
			taille = Double.parseDouble(arrayS[4]);
		}
		/** si le poids n'est pas connu on la remplace par 0 sinon on récupère celle du fichier csv */
		if (!arrayS[5].equals("NA"))
		{
			arrayS[5] = arrayS[5].replace(".", ".1");
			poids = Double.parseDouble(arrayS[5]);
		}
		/** gestion du nom prénom */
		nom = nom.trim();
		/** On identifie s'il y a un nom entre parenthèse */
		int lastParentheseO = nom.lastIndexOf("(");
		if (lastParentheseO >0)
		{
			/** on supprime ce nom entre parenthèse */
		nom = nom.substring(0, lastParentheseO);
		}
		nom = nom.trim();
		/** on identifie la dernière partie du nom pour l'assigner au nom */
		int lastSpace = nom.lastIndexOf(" ");
		/* si plusieurs partie, la dernière est le nom, tout ce qu'il y avant est le prénom */
		if (lastSpace > 0)
		{
			prenom = nom.substring(0, lastSpace);
			nom =  nom.substring(lastSpace);
		}
		nom = nom.trim();
		/** instanciation de l'athlete */
		Athlete athlete = new Athlete ();
		athlete.setAnnee_Naissance(naissance);
		athlete.setGenre(genre);
		athlete.setNom(nom);
		athlete.setPrenom(prenom);
		athlete.setPoids(poids);
		athlete.setTaille(taille);
		
		return athlete;
	}
	
	/** méthode pour trouver une liste d'athlete dans la base de donnée selon les résultats d'une requete*/
	@SuppressWarnings("unlikely-arg-type")
	public static List<Athlete> findAth(ResultSet rs, EntityManager em) throws SQLException
	{
		List<Athlete> listAth = new ArrayList<>();
		HashSet<Athlete> setAth = new LinkedHashSet<>();
		HashSet<String[]> listInfos = new HashSet<String[]>();
		/** On parcourt la liste de Résultat pour y récupérer les infos sur l'athlète recherché selon une requete dans la base de donnée brute*/
		while (rs.next()) {
			String[] info = new String[7];
			String nom = rs.getString(1);
			nom = nom.trim();

			int lastParentheseO = nom.lastIndexOf("(");

			if (lastParentheseO > 0) {
				nom = nom.substring(0, lastParentheseO);
			}
			nom = nom.trim();
			String prenom = null;
			int lastSpace = nom.lastIndexOf(" ");
			if (lastSpace > 0) {
				prenom = nom.substring(0, lastSpace);
				nom = nom.substring(lastSpace);
			}
			nom = nom.trim();
			info[0] = nom;
			info[1] = prenom;
			info[2] = rs.getString(2);
			info[3] = rs.getString(3);
			info[4] = rs.getString(4);
			info[5] = rs.getString(5);
			info[6] = rs.getString(6);
			listInfos.add(info);
			rs.next();
		}
		/** On parcours la liste des infos sur l'athlète */
		for (String[] s : listInfos) {
			/** on change les infos de type string pour le type approprié */
			double poids = 0;
			double taille = 0;
			if (!s[2].equals("NA"))
			{
				s[2] = s[2].replace(".", ".0");
				taille = Double.parseDouble(s[2]);
			}
			if (!s[3].equals("NA"))
			{
				s[3] = s[3].replace(".", ".0");
				System.out.println(s[3]);
			poids = Double.parseDouble(s[3]);
			}
			Integer naissance = 0;
			Integer DateJO = Integer.parseInt(s[6]);
			if (s[5].matches("-?\\d+"))
			{
			Integer intAge = Integer.parseInt(s[5]);
			naissance  = DateJO - intAge;		
			}
			/** une fois qu'on a toute les infos on instancie l'athlète */
			Athlete ath = new Athlete();
			
			/** Selon s'il a un prénom ou pas on choisit quelle requete on lance */
			if (s[1] != null)
			{
			 ath = (Athlete) em.createNamedQuery("findAthByNomForEq", Athlete.class).setParameter("nom", s[0])
					.setParameter("prenom", s[1]).setParameter("taille",  taille).setParameter("poids", poids).setParameter("genre",s[4]).setParameter("age", naissance)
					.getSingleResult();
			}
			else {
				 ath = (Athlete) em.createNamedQuery("findAthByNomForEqIfNull", Athlete.class).setParameter("nom", s[0])
						.setParameter("taille",  taille).setParameter("poids", poids).setParameter("genre",s[4]).setParameter("age", naissance)
						.getSingleResult();
			}
			listAth.add(ath);
			
			/** on élimine les doublons */
			for (Athlete a : listAth)
			{
				if (a.equals(listAth)==false)
				{
					setAth.add(a);
				}
			}
			
			listAth.clear();
			listAth.addAll(setAth);
		}
		/** on retourne la liste d'athlète demandée */
		return listAth;
	}
	
	/**
	 * @return le prenom
	 */
	public String getPrenom() {
		return prenom;
	}

	/**
	 * @param prenom
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	/**
	 * @return le nom
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
	 * @return le genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @param genre
	 */
	public void setGenre(String genre) {
		this.genre = genre;
	}

	/**
	 * @return l'année de naissance
	 */
	public Integer getAnnee_Naissance() {
		return annee_Naissance;
	}

	/**
	 * @param annee_Naissance
	 */
	public void setAnnee_Naissance(Integer annee_Naissance) {
		this.annee_Naissance = annee_Naissance;
	}

	/**
	 * @return la taille
	 */
	public Double getTaille() {
		return taille;
	}

	/**
	 * @param taille
	 */
	public void setTaille(Double taille) {
		this.taille = taille;
	}

	/**
	 * @return le poids
	 */
	public Double getPoids() {
		return poids;
	}

	/**
	 * @param poids
	 */
	public void setPoids(Double poids) {
		this.poids = poids;
	}
	
	/**
	 * @return liste de Pays
	 */
	public List<Pays> getPays() {
		return pays;
	}

	/**
	 * @param pays
	 */
	public void setPays(List<Pays> pays) {
		this.pays = pays;
	}
	/**
	 * @return une liste d'équipe
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
	 * vérifie les doublons
	 */
	@Override
	public  boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Athlete other = (Athlete) obj;
		return Objects.equals(annee_Naissance, other.annee_Naissance) && Objects.equals(genre, other.genre)
				&& Objects.equals(nom, other.nom) && Objects.equals(poids, other.poids)
				&& Objects.equals(prenom, other.prenom) && Objects.equals(taille, other.taille);
	}

	/**
	 * affiche les infos de l'athlète en String
	 */
	@Override
	public String toString() {
		return "Athlete [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", genre=" + genre + ", annee_Naissance="
				+ annee_Naissance + ", taille=" + taille + ", poids=" + poids + ", medaille=" + medaille + ", equipe="
				+ equipe + "]";
	}







	
	
	

}
