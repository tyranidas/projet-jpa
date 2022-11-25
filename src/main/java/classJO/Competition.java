package classJO;

import java.io.IOException;
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
@NamedQueries({ @NamedQuery(name = "findSportByNom2", query = "SELECT s FROM Sport as s where s.nom_ENG =:nomSport"),
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
		HashMap<Integer, String[]> mapEqui = new HashMap<>();

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

		/*	System.out.println("equipe = " + eq);
			 Equipe equipe = em.createNamedQuery("findEquipeByNom2",
			 Equipe.class).setParameter("eq", eq).getSingleResult();

			compteur++;

			mapEqui.put(compteur, infosEqui);

			 List<Sport> sport = (List<Sport>) em.createNamedQuery("findSportByNom2",
			 Sport.class).setParameter("nomSport", sp).getResultList();
			 List<Epreuve> epreuve = (List<Epreuve>)
			 em.createNamedQuery("findEpreuveByNom2", Epreuve.class).setParameter("ep",
			 ep).getResultList();*/

			listComp.add(comp);

		}

	/*	Iterator<Entry<Integer, String[]>> it = mapEqui.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, String[]> entry = (Entry<Integer, String[]>) it.next();
			@SuppressWarnings("unchecked")
			 Equipe equipe = em.createNamedQuery("findEquipeByNom2",
						 Equipe.class).setParameter("eq", entry.getValue()[1]).getSingleResult();
			for (Equipe e : classEqui) {
				System.out.println(e.getNom());
			}
*/
		
		
		  for (Competition c : listComp) 
		  {
		  
		  if (c.equals(listComp) == false) 
		  { setComp.add(c); } 
		  } 
		  
		  listComp.clear();
		  listComp.addAll(setComp);
	
		  
		  for (Competition c : listComp) { 
		/*	  for (String e : listEqui) { 
			  compteur++;
		  System.out.println(compteur); System.out.println("equipe"+e);
		  
		 Equipe equipe = em.createNamedQuery("findEquipeByNom2",
		  Equipe.class).setParameter("eq", e).getSingleResult();*/
		  
			  em.persist(c); }
		  
			System.out.println("pouet");
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
