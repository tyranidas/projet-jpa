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

@Entity
@Table(name = "DONNEE_BRUTE")
public class DonneeBrut {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name", length = 100, nullable = false)
	private String name;

	@Column(name = "sex", length = 1, nullable = false)
	private String sex;

	@Column(name = "age", length = 2, nullable = false)
	private String age;
	
	@Column(name = "height", length = 3, nullable = false)
	private String taille;
	
	@Column(name = "weight", length = 3, nullable = false)
	private String poids;
	
	@Column(name = "team", length = 100, nullable = false)
	private String team;
	
	@Column(name = "noc", length = 3, nullable = false)
	private String noc;
	
	@Column(name = "games", length = 100, nullable = false)
	private String jeux;
	
	@Column(name = "year", length = 4, nullable = false)
	private String annee;
	
	@Column(name = "season", length = 6, nullable = false)
	private String saison;
	
	@Column(name = "city", length = 100, nullable = false)
	private String ville;
	
	@Column(name = "sport", length = 100, nullable = false)
	private String sport;
	
	@Column(name = "event", length = 100, nullable = false)
	private String epreuve;
	
	@Column(name = "medal", length = 100, nullable = false)
	private String med;
	


public static void traiterDDB(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
	List<String> lines = DataBase.recupFichier("athlete_epreuves");
	for (String l : lines) {

		String[] arrayS = new String[15];
		for (int i = 0; i < l.split(";").length; i++) {
			arrayS[i] = l.split(";")[i];
		}
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
		 em.persist(bdd); 
	}
}



public String getName() {
	return name;
}



public void setName(String arrayS) {
	this.name = arrayS;
}



public String getSex() {
	return sex;
}



public void setSex(String sex) {
	this.sex = sex;
}



public String getAge() {
	return age;
}



public void setAge(String age) {
	this.age = age;
}



public String getTaille() {
	return taille;
}



public void setTaille(String taille) {
	this.taille = taille;
}



public String getPoids() {
	return poids;
}



public void setPoids(String poids) {
	this.poids = poids;
}



public String getTeam() {
	return team;
}



public void setTeam(String team) {
	this.team = team;
}



public String getNoc() {
	return noc;
}



public void setNoc(String noc) {
	this.noc = noc;
}



public String getJeux() {
	return jeux;
}



public void setJeux(String jeux) {
	this.jeux = jeux;
}



public String getAnnee() {
	return annee;
}



public void setAnnee(String annee) {
	this.annee = annee;
}



public String getSaison() {
	return saison;
}



public void setSaison(String saison) {
	this.saison = saison;
}



public String getVille() {
	return ville;
}



public void setVille(String ville) {
	this.ville = ville;
}



public String getSport() {
	return sport;
}



public void setSport(String sport) {
	this.sport = sport;
}



public String getEpreuve() {
	return epreuve;
}



public void setEpreuve(String epreuve) {
	this.epreuve = epreuve;
}



public String getMed() {
	return med;
}



public void setMed(String med) {
	this.med = med;
}

}



