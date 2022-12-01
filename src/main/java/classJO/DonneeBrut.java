package classJO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import createTable.DataBase;

/**
 * @author Manuel Rougé
 *
 */
/**
 * @author tyran
 *
 */
@Entity
/** table servant à stocké sans traiter les données du fichier .csv */
@Table(name = "DONNEE_BRUTE")
public class DonneeBrut {

	/**
	 * colonne id, clef primaire incrédenté automatiquement
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	/**
	 * colonne name de taille 100 et non null contenant nom et prénom
	 */
	@Column(name = "name", length = 100, nullable = false)
	private String name;
	/**
	 * colonne sex de taille 1 et non null
	 */
	@Column(name = "sex", length = 1, nullable = false)
	private String sex;
	/**
	 * colonne agede taille 2 et non null
	 */
	@Column(name = "age", length = 2, nullable = false)
	private String age;
	/**
	 * colonne height de taille 3 et non null
	 */
	@Column(name = "height", length = 3, nullable = false)
	private String taille;
	/**
	 * colonne weight de taille 3 et non null
	 */
	@Column(name = "weight", length = 3, nullable = false)
	private String poids;
	/**
	 * colonne team de taille 100 et non null
	 */
	@Column(name = "team", length = 100, nullable = false)
	private String team;
	/**
	 * colonne noc de taille 3 et non null contenant le code du pays
	 */
	@Column(name = "noc", length = 3, nullable = false)
	private String noc;
	/**
	 * colonne games de taille 100 et non null
	 */
	@Column(name = "games", length = 100, nullable = false)
	private String jeux;
	/**
	 * colonne year de taille 4 et non null contenant l'année des JP
	 */
	@Column(name = "year", length = 4, nullable = false)
	private String annee;
	/**
	 * colonne season de taille 6 et non null
	 */
	@Column(name = "season", length = 6, nullable = false)
	private String saison;
	/**
	 * colonne city de taille 100 et non null
	 */
	@Column(name = "city", length = 100, nullable = false)
	private String ville;
	/**
	 * colonne sport de taille 100 et non null
	 */
	@Column(name = "sport", length = 100, nullable = false)
	private String sport;
	/**
	 * colonne event de taille 100 et non null contenant le nom de l'épreuve et du
	 * sport
	 */
	@Column(name = "event", length = 100, nullable = false)
	private String epreuve;
	/**
	 * colonne medal de taille 3 et non null contenant la médaille obtenue
	 */
	@Column(name = "medal", length = 100, nullable = false)
	private String med;

	/**
	 * method qui permet de remplir la table DONNEE_BRUTE de la base de donnée
	 */
	public static void traiterDDB(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		/** Appel de la méthode recupFichier pour traiter le document csv */
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		/** On lit chaque ligne du document */
		for (String l : lines) {

			/** On divise la ligne en autant de partie que de colonne du tableau csv */
			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
			}
			/** On instancie la ligne */
			DonneeBrut bdd = new DonneeBrut();

			bdd.setName(arrayS[1]);
			bdd.setSex(arrayS[2]);
			bdd.setAge(arrayS[3]);
			bdd.setTaille(arrayS[4]);
			bdd.setPoids(arrayS[5]);
			bdd.setTeam(arrayS[6]);
			bdd.setNoc(arrayS[7]);
			bdd.setJeux(arrayS[8]);
			bdd.setAnnee(arrayS[9]);
			bdd.setSaison(arrayS[10]);
			bdd.setVille(arrayS[11]);
			bdd.setSport(arrayS[12]);
			bdd.setEpreuve(arrayS[13]);
			bdd.setMed(arrayS[14]);
			/** on ajoute la ligne à la base de donnée */
			em.persist(bdd);
		}
	}

	/**
	 * @return prenom + nom
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param arrayS
	 */
	public void setName(String arrayS) {
		this.name = arrayS;
	}

	/**
	 * @return le genre
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return l'age
	 */
	public String getAge() {
		return age;
	}

	/**
	 * @param age
	 */
	public void setAge(String age) {
		this.age = age;
	}

	/**
	 * @return la taille
	 */
	public String getTaille() {
		return taille;
	}

	/**
	 * @param taille
	 */
	public void setTaille(String taille) {
		this.taille = taille;
	}

	/**
	 * @return le poids
	 */
	public String getPoids() {
		return poids;
	}

	/**
	 * @param poids
	 */
	public void setPoids(String poids) {
		this.poids = poids;
	}

	/**
	 * @return le nom de l'équipe
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * @param team
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/**
	 * @return le code du pays
	 */
	public String getNoc() {
		return noc;
	}

	/**
	 * @param noc
	 */
	public void setNoc(String noc) {
		this.noc = noc;
	}

	/**
	 * @return la saison et l'année
	 */
	public String getJeux() {
		return jeux;
	}

	/**
	 * @param jeux
	 */
	public void setJeux(String jeux) {
		this.jeux = jeux;
	}

	/**
	 * @return l'édition
	 */
	public String getAnnee() {
		return annee;
	}

	/**
	 * @param annee
	 */
	public void setAnnee(String annee) {
		this.annee = annee;
	}

	/**
	 * @return la saison
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
	 * @return la ville
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

	/**
	 * @return le sport
	 */
	public String getSport() {
		return sport;
	}

	/**
	 * @param sport
	 */
	public void setSport(String sport) {
		this.sport = sport;
	}

	/**
	 * @return l'épreuve et le sport
	 */
	public String getEpreuve() {
		return epreuve;
	}

	/**
	 * @param epreuve
	 */
	public void setEpreuve(String epreuve) {
		this.epreuve = epreuve;
	}

	/**
	 * @return la médaille
	 */
	public String getMed() {
		return med;
	}

	/**
	 * @param med
	 */
	public void setMed(String med) {
		this.med = med;
	}

}
