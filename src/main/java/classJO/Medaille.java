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

@Entity
@NamedQueries({
	@NamedQuery(name="findAthByNom", query = "SELECT a FROM Athlete as a where a.nom =:nom "
			+ " and (a.prenom=:prenom or a.prenom IS NULL )"
			+ "and a.annee_Naissance = :naissance and a.taille = :taille "
			+ "and a.genre = :genre"),
	@NamedQuery(name="findEpreuveByNom", query = "SELECT e FROM Epreuve as e where e.nom_ENG=:ep"),
	@NamedQuery(name="findCompetbyEditionAndVille", query = "SELECT c FROM Competition as c where c.ville=:ville and c.edition=:edition")
})
@Table(name="MEDAILLE")
public class Medaille {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name="type", length = 6)
	private String type;
	
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "Id_Athlete")
	private Athlete athlete;
	
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "Id_Epreuve")
	private Epreuve epreuve ;
	
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "Id_Competition")
	private Competition competition ;

	public Medaille()
	{
	}

	public static void traiterMedaille(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		int compteur = 0;
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		HashMap<String, String[]> medalAth = new HashMap();
	//	HashMap<String, String> medalEpr = new HashMap();
		//HashMap<String, String[]> medalComp = new HashMap();
		String med = " ";
		String nom ="";
	
		
		String [] globMed = new String [4];
		
		
		for (int j = 0; j < 100000 ; j++)
	//	for (String s : lines)
		{
			String s = lines.get(j);
			String[] arrayA = new String[15];
			for (int i = 0; i < s.split(";").length; i++) {
				arrayA[i] = s.split(";")[i];
			}
			
			med = arrayA[14];
		
			
			if (med.equals("NA") == false)
			{
				nom = arrayA[1];
				nom = nom.trim();
			
				int lastParentheseO = nom.lastIndexOf("(");
				
				if (lastParentheseO >0)
				{
				nom = nom.substring(0, lastParentheseO);
				}
				nom = nom.trim();
				String prenom = null;
				int lastSpace = nom.lastIndexOf(" ");
				if (lastSpace > 0)
				{
					prenom = nom.substring(0, lastSpace);
			//		prenom = prenom.replace("'", "\\'");
					nom =  nom.substring(lastSpace);
				
				}
				nom = nom.trim();
				Integer intAge = 0;
				Integer naissance  = 0;
				Integer DateJO = Integer.parseInt(arrayA[9]);
				if (arrayA[3].matches("-?\\d+"))
				{
				intAge = Integer.parseInt(arrayA[3]);
				naissance  = DateJO - intAge;		
				}
				
				String genre = arrayA[2];
				Double taille = (double) 0;
				if (arrayA[4].matches("-?\\d+"))
				{
				 taille = Double.parseDouble(arrayA[4]);
				}
				
				String sport = arrayA[12];
				String ep = arrayA[13];
				ep = ep.replaceFirst(sport, "").trim();
				
				String ville = arrayA[11];
//				ville = ville.replace("'", "\\'");
				Integer annee = Integer.parseInt(arrayA[9]);
				
				compteur++;
				System.out.println(compteur);
				System.out.println("nom = "+nom);
				System.out.println("prenom = "+prenom);
				System.out.println("naissance = "+naissance);
				System.out.println("taille = "+taille);
				System.out.println(ep);
				System.out.println(ville);
			
			Athlete ath = em.createNamedQuery("findAthByNom", Athlete.class)
					.setParameter("nom", nom)
					.setParameter("prenom", prenom)
					.setParameter("naissance", naissance)
					.setParameter("taille", taille)
					.setParameter("genre", genre)
					.getSingleResult();
			Epreuve epreuve = em.createNamedQuery("findEpreuveByNom", Epreuve.class).setParameter("ep", ep).getSingleResult();
			Competition compet = em.createNamedQuery("findCompetbyEditionAndVille", Competition.class)
					.setParameter("ville", ville)
					.setParameter("edition", annee).getSingleResult(); 
			
			Medaille medaille = new Medaille(); 
			
		
				medaille.setAthlete(ath);
				medaille.setCompetition(compet);
				medaille.setEpreuve(epreuve); 
				medaille.setType(med);
				em.persist(medaille);
			}
			
		}
		
	
	}
	

	
	public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Athlete getAthlete() {
		return athlete;
	}

	public void setAthlete(Athlete athlete) {
		this.athlete = athlete;
	}

	public Epreuve getEpreuve() {
		return epreuve;
	}

	public void setEpreuve(Epreuve epreuve) {
		this.epreuve = epreuve;
	}

	public Competition getCompetition() {
		return competition;
	}

	public void setCompetition(Competition competition) {
		this.competition = competition;
	}
	
	
	
	
}
