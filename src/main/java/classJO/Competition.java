package classJO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
		@NamedQuery(name = "findAllComp", query = "Select c FROM Competition as c"),
		/**
		 * Requête préparée pour sélectionner la compétition depuis la base de donnée
		 * selon son id
		 */
		@NamedQuery(name = "findCompetById", query = "Select c FROM Competition as c where c.id = :id"),
		/**
		 * Requête préparée pour sélectionner le sport depuis la base de donnée selon
		 * son nom
		 */
		@NamedQuery(name = "findSportByNom2", query = "SELECT s FROM Sport as s where s.nom_ENG =:nomSport"),
		/**
		 * Requête préparée pour sélectionner l'équipe depuis la base de donnée selon
		 * son nom
		 */
		@NamedQuery(name = "findEquipeByNom2", query = "SELECT e FROM Equipe as e where e.nom= :name"),
		/**
		 * Requête préparée pour sélectionner l'épreuve depuis la base de donnée selon
		 * son nom
		 */
		@NamedQuery(name = "findEpreuveByNom2", query = "SELECT ep FROM Epreuve as ep where ep.nom_ENG=:ep"), })
/**
 * classe relié à la table Compétition
 */
@Table(name = "COMPETITION")
public class Competition {

	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * colonne édition de la compétition de taille 4 et non null
	 */
	@Column(name = "edition", length = 4, nullable = false)
	private Integer edition;
	/**
	 * colonne saison de la compétition de taille 6 et non null
	 */
	@Column(name = "saison", length = 6, nullable = false)
	private String saison;
	/**
	 * colonne ville de la compétition de taille 100 et non null
	 */
	@Column(name = "ville", length = 100, nullable = false)
	private String ville;

	/**
	 * liaison plusieurs compétitions pour plusieurs épreuves sur la table
	 * EPREUVE_COMPETITION
	 */
	@ManyToMany
	@JoinTable(name = "EPREUVE_COMPETITION", joinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Epreuve", referencedColumnName = "id"))
	private List<Epreuve> epreuves;
	/**
	 * liaison plusieurs compétitions pour plusieurs sports sur la table
	 * SPORT_COMPETITION
	 */
	@ManyToMany
	@JoinTable(name = "SPORT_COMPETITION", joinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Sport", referencedColumnName = "id"))
	private List<Sport> sports;
	/**
	 * liaison une compétition pour plusieurs médailles.
	 */
	@OneToMany(mappedBy = "competition")
	private List<Medaille> medaille;
	/**
	 * liaison plusieurs compétitions pour plusieurs équipes sur la table
	 * EQUIPE_COMPETITION
	 */
	@ManyToMany
	@JoinTable(name = "EQUIPE_COMPETITION", joinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Equipe", referencedColumnName = "id"))
	private List<Equipe> equipe;

	/**
	 * Constructeur vide de la classe Competition
	 */
	public Competition() {

	}

	/**
	 * method qui permet de remplir la table Compétition de la base de donnée
	 */
	@SuppressWarnings("unlikely-arg-type")
	public static void traiterCompet(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		/** Appel de la méthode recupFichier pour traiter le document csv */
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		ArrayList<Competition> listComp = new ArrayList<Competition>();
		HashSet<Competition> setComp = new LinkedHashSet<>();
		/** On lit chaque ligne du document */
		for (String l : lines) {
			/** On divise la ligne en autant de partie que de colonne du tableau csv */
			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
			}
			/** On récupère les informations de la compétition */
			Integer dateJO = Integer.parseInt(arrayS[9]);
			String saison = arrayS[10];
			String ville = arrayS[11];
			/** On instancie la compétition et on y ajoute ses infos */
			Competition comp = new Competition();
			comp.setSaison(saison);
			comp.setVille(ville);
			comp.setYear(dateJO);
			listComp.add(comp);
		}
		/** on élimine les doublons */
		for (Competition c : listComp) {
			if (c.equals(listComp) == false) {
				setComp.add(c);
			}
		}
		listComp.clear();
		listComp.addAll(setComp);
		/** On ajoute la compétition à la base de donnée */
		for (Competition c : listComp) {
			em.persist(c);
		}
	}

	/** méthode pour récupérer les équipes liés à chaque compétition */
	public static void recupEquipe(EntityManager em) throws ClassNotFoundException, SQLException {
		/** on récupère toutes les compétitions */
		List<Competition> allComp = em.createNamedQuery("findAllComp", Competition.class).getResultList();
		/** Pour chaque compétition */
		for (Competition comp : allComp) {

			List<Equipe> listEq = new ArrayList<>();
			/** connexion à la base de donnée */
			Statement stmt = DataBase.connectionDB();
			/** On récupère les renseignement de l'équipe sur la base de donnée brutes */
			String query = "Select team from donnee_brute where city = \"" + comp.getVille() + "\" and year = \""
					+ comp.getYear() + "\"";
			ResultSet rs = stmt.executeQuery(query);
			HashSet<String> listEquip = new HashSet<String>();
			/** On stock toutes les équipes liées à la compétition dans une liste */
			while (rs.next()) {
				String value = rs.getString(1);
				value = value.replace("\"", "");
				listEquip.add(value);
				rs.next();
			}
			/** On récupère chaque classe Equipe liée a la Compétition */
			for (String s : listEquip) {
				Equipe eq = (Equipe) em.createNamedQuery("findEquipeByNom2", Equipe.class).setParameter("name", s)
						.getSingleResult();
				listEq.add(eq);

			}
			/** On les ajoutes dans la BDD */
			comp.setEquipe(listEq);
		}

	}
	/** méthode pour récupérer les sports liés à chaque compétition */
	public static void recupSport(EntityManager em) throws ClassNotFoundException, SQLException {
		/** on récupère toutes les compétitions */
		List<Competition> allComp = em.createNamedQuery("findAllComp", Competition.class).getResultList();
		/** Pour chaque compétition */
		for (Competition comp : allComp) {
			List<Sport> listSp = new ArrayList<>();
			/** On récupère les renseignement du sport sur la base de donnée brutes */
			Statement stmt = DataBase.connectionDB();
			String query = "Select sport from donnee_brute where city = \"" + comp.getVille() + "\" and year = \""
					+ comp.getYear() + "\"";
			ResultSet rs = stmt.executeQuery(query);
			HashSet<String> listSport = new HashSet<String>();
			/** On stock tous les sport liés à la compétition dans une liste */
			while (rs.next()) {
				String value = rs.getString(1);
				listSport.add(value);
				rs.next();
			}
			/** On récupère chaque classe Sport liée a la Compétition */
			for (String s : listSport) {
				Sport sp = (Sport) em.createNamedQuery("findSportByNom2", Sport.class).setParameter("nomSport", s)
						.getSingleResult();
				listSp.add(sp);
				System.out.println(sp.getNom_FR());
			}
			/** On les ajoutes dans la BDD */
			comp.setSports(listSp);
		}
	}
	/** méthode pour récupérer les épreuves liés à chaque compétition */
	public static void recupEpreuve(EntityManager em) throws ClassNotFoundException, SQLException {
		/** on récupère toutes les compétitions */
		List<Competition> allComp = em.createNamedQuery("findAllComp", Competition.class).getResultList();
		/** Pour chaque compétition */
		for (Competition comp : allComp) {
		
			List<Epreuve> listEpreuve = new ArrayList<>();
			/** On récupère les renseignement de l'épreuve sur la base de donnée brutes */
			Statement stmt = DataBase.connectionDB();
			String query = "Select event, sport from donnee_brute where city = \"" + comp.getVille()
					+ "\" and year = \"" + comp.getYear() + "\"";
			ResultSet rs = stmt.executeQuery(query);
			HashSet<String> listEp = new HashSet<String>();
			/** On stock toutes les épreuves liées à la compétition dans une liste */
			while (rs.next()) {
				String ep = rs.getString(1);
				String sp = rs.getString(2);
				/** suppression du noù du sport dans l'énoncé de l'épreuve */
				ep = ep.replaceFirst(sp, "").trim();
				listEp.add(ep);
				rs.next();
			}
			/** On récupère chaque classe Épreuves liée a la Compétition */
			for (String s : listEp) {
				Epreuve sp = (Epreuve) em.createNamedQuery("findEpreuveByNom2", Epreuve.class).setParameter("ep", s)
						.getSingleResult();
				listEpreuve.add(sp);
				System.out.println(sp.getNom_FR());
			}
			/** On les ajoutes dans la BDD */
			comp.setEpreuves(listEpreuve);
		}
	}

	/**
	 * @return liste d'épreuves
	 */
	public List<Epreuve> getEpreuves() {
		return epreuves;
	}

	/**
	 * @param epreuves
	 */
	public void setEpreuves(List<Epreuve> epreuves) {
		this.epreuves = epreuves;
	}

	/**
	 * @return liste de sports
	 */
	public List<Sport> getSports() {
		return sports;
	}

	/**
	 * @param sports
	 */
	public void setSports(List<Sport> sports) {
		this.sports = sports;
	}

	/**
	 * @return liste d'équipes
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
	 * verifie les doublons
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Competition other = (Competition) obj;
		return Objects.equals(edition, other.edition) && Objects.equals(saison, other.saison)
				&& Objects.equals(ville, other.ville);
	}

	@Override
	public String toString() {
		return "Competition [year=" + edition + ", saison=" + saison + ", ville=" + ville + "]";
	}

	/**
	 * @return l'edition de la Compétition
	 */
	public Integer getYear() {
		return edition;
	}

	/**
	 * @param year
	 */
	public void setYear(Integer year) {
		this.edition = year;
	}

	/**
	 * @return si ce sont des JO d'hiver ou d'été
	 */
	public String getSaison() {
		return saison;
	}

	/**
	 * @param saison
	 */
	public void setSaison(String saison) {
		this.saison = saison;
	}

	/**
	 * @return la ville où s'est déroulé les JO
	 */
	public String getVille() {
		return ville;
	}

	/**
	 * @param ville
	 */
	public void setVille(String ville) {
		this.ville = ville;
	}

}
