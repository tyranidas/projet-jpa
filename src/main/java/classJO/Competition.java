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

@Entity
@NamedQueries({ 
		@NamedQuery(name = "findCompetById", query = "Select c FROM Competition as c where c.id = :id"),
		@NamedQuery(name = "findSportByNom2", query = "SELECT s FROM Sport as s where s.nom_ENG =:nomSport"),
		@NamedQuery(name = "findEquipeByNom2", query = "SELECT e FROM Equipe as e where e.nom= :name"),
		@NamedQuery(name = "findEpreuveByNom2", query = "SELECT ep FROM Epreuve as ep where ep.nom_ENG=:ep"), })
@Table(name = "COMPETITION")
public class Competition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "edition", length = 4, nullable = false)
	private Integer edition;

	@Column(name = "saison", length = 6, nullable = false)
	private String saison;

	@Column(name = "ville", length = 100, nullable = false)
	private String ville;

	@ManyToMany
	@JoinTable(name = "EPREUVE_COMPETITION", joinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Epreuve", referencedColumnName = "id"))
	private List<Epreuve> epreuves;

	@ManyToMany
	@JoinTable(name = "SPORT_COMPETITION", joinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Sport", referencedColumnName = "id"))
	private List<Sport> sports;

	@OneToMany(mappedBy = "competition")
	private List<Medaille> medaille;

	@ManyToMany
	@JoinTable(name = "EQUIPE_COMPETITION", joinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Equipe", referencedColumnName = "id"))
	private List<Equipe> equipe;

	public Competition() {

	}

	@SuppressWarnings("unlikely-arg-type")
	public static void traiterCompet(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		ArrayList<Competition> listComp = new ArrayList<Competition>();
		HashSet<Competition> setComp = new LinkedHashSet<>();
		HashSet<String[]> listEqui = new LinkedHashSet<>();
	

		int compteur = 0;
		for (String l : lines) {

			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];

			}
			Integer dateJO = Integer.parseInt(arrayS[9]);

			String saison = arrayS[10];
			String ville = arrayS[11];

			Competition comp = new Competition();

			comp.setSaison(saison);
			comp.setVille(ville);
			comp.setYear(dateJO);

			String eq = arrayS[6];
			String sp = arrayS[12];
			String ep = arrayS[13];
			ep = ep.replaceFirst(sp, "").trim();

			String[] infosEqui = new String[2];
			infosEqui[0] = ville;
			infosEqui[1] = eq;

			listEqui.add(infosEqui);

		

			listComp.add(comp);

		}

		
		
		  for (Competition c : listComp) 
		  {
		  
		  if (c.equals(listComp) == false) 
		  { setComp.add(c); } 
		  } 
		  
		  listComp.clear();
		  listComp.addAll(setComp);
	
		  
		  for (Competition c : listComp) { 
		
			
		  
		 
		 
			  em.persist(c); }
		  
			System.out.println("pouet");
		  }
		  
		 
public static void recupEquipe(EntityManager em) throws ClassNotFoundException, SQLException
{
	
	for (int i = 1; i <53; i++)
	{
		List<Equipe> listEq = new ArrayList<>();
		Competition comp = em.createNamedQuery("findCompetById",
				  Competition.class).setParameter("id", i).getSingleResult();
		
		Statement stmt = DataBase.connectionDB();
		
		String query ="Select team from donnee_brute where city = \""+comp.getVille()+"\" and year = \""+comp.getYear()+"\"";
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		HashSet<String> listEquip = new HashSet<String>();
		
		while (rs.next())
		{
			 String value = rs.getString(1);
			 System.out.println(value);
			 value = value.replace("\"", "");
			 listEquip.add(value);
			 rs.next();
		}
		
		for (String s : listEquip)
		{
			Equipe eq =  (Equipe) em.createNamedQuery("findEquipeByNom2", Equipe.class).setParameter("name", s).getSingleResult();
			listEq.add(eq);
			
		}
		
		comp.setEquipe(listEq);
		
	}
	
	
}
public static void recupSport(EntityManager em) throws ClassNotFoundException, SQLException
{
	
	for (int i = 1; i <53; i++)
	{
		List<Sport> listSp = new ArrayList<>();
		Competition comp = em.createNamedQuery("findCompetById",
				  Competition.class).setParameter("id", i).getSingleResult();
		
		Statement stmt = DataBase.connectionDB();
		
		String query ="Select sport from donnee_brute where city = \""+comp.getVille()+"\" and year = \""+comp.getYear()+"\"";
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		
		
		
		HashSet<String> listSport = new HashSet<String>();
		
		while (rs.next())
		{
			 String value = rs.getString(1);
			 listSport.add(value);
			 rs.next();
		}
		
		for (String s : listSport)
		{
			Sport sp =  (Sport) em.createNamedQuery("findSportByNom2", Sport.class).setParameter("nomSport", s).getSingleResult();
			listSp.add(sp);
			System.out.println(sp.getNom_FR());
		}
	
		comp.setSports(listSp);
		
	}
}
	

public static void recupEpreuve(EntityManager em) throws ClassNotFoundException, SQLException
{
	
	for (int i = 1; i <53; i++)
	{
		List<Epreuve> listEpreuve = new ArrayList<>();
		Competition comp = em.createNamedQuery("findCompetById",
				  Competition.class).setParameter("id", i).getSingleResult();
		
		Statement stmt = DataBase.connectionDB();
		
		String query ="Select event, sport from donnee_brute where city = \""+comp.getVille()+"\" and year = \""+comp.getYear()+"\"";
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		
		
		
		HashSet<String> listEp = new HashSet<String>();
		
		while (rs.next())
		{
			 String ep = rs.getString(1);
			 String sp = rs.getString(2);
			 
			 ep = ep.replaceFirst(sp, "").trim();
				
			 
			 listEp.add(ep);
			 rs.next();
		}
		
		for (String s : listEp)
		{
			Epreuve sp =  (Epreuve) em.createNamedQuery("findEpreuveByNom2", Epreuve.class).setParameter("ep", s).getSingleResult();
			listEpreuve.add(sp);
			System.out.println(sp.getNom_FR());
		}
	
		comp.setEpreuves(listEpreuve);
		
	}
}

	public List<Epreuve> getEpreuves() {
		return epreuves;
	}

	public void setEpreuves(List<Epreuve> epreuves) {
		this.epreuves = epreuves;
	}

	public List<Sport> getSports() {
		return sports;
	}

	public void setSports(List<Sport> sports) {
		this.sports = sports;
	}

	public List<Equipe> getEquipe() {
		return equipe;
	}

	public void setEquipe(List<Equipe> equipe) {
		this.equipe = equipe;
	}

	@Override
	public int hashCode() {
		return Objects.hash(edition, saison, ville);
	}

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

	public Integer getYear() {
		return edition;
	}

	public void setYear(Integer year) {
		this.edition = year;
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

}
