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

@Entity
@NamedQueries({
	@NamedQuery(name="findAllAthlete", query = "Select a FROM Athlete as a"),
	@NamedQuery(name="findPaysByName", query = "SELECT p FROM Pays as p where p.cio_Code =:pays"),
	@NamedQuery(name="findEquipeByName", query = "SELECT e FROM Equipe as e where e.nom=:eq"),
})
@Table(name="ATHLETE")
public final class Athlete {

	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name="nom", nullable = false)	
	private String nom;
	
	@Column (name="prenom")	
	private String prenom;
	
	@Column (name="genre")	
	private String genre;
	
	@Column (name="annee_Naissance")	
	private Integer annee_Naissance;
	
	@Column (name="taille")	
	private Integer taille;
	
	@Column (name="poids")	
	private Integer poids;
	
	@OneToMany(mappedBy = "athlete")
	private List<Medaille> medaille;
	
	@ManyToMany
	@JoinTable (name="EQUIPE_ATHLETE",
	joinColumns= @JoinColumn(name="id_Athlete", referencedColumnName ="id"),
	inverseJoinColumns= @JoinColumn(name="id_Equipe", referencedColumnName = "id")
	)
	private List<Equipe> equipe;
	
	@ManyToMany
	@JoinTable (name="PAYS_ATHLETE",
	joinColumns= @JoinColumn(name="id_Athlete", referencedColumnName ="id"),
	inverseJoinColumns= @JoinColumn(name="id_Pays", referencedColumnName = "id")
	)
	private List<Pays> pays;
	
	
	public List<Pays> getPays() {
		return pays;
	}

	public void setPays(List<Pays> pays) {
		this.pays = pays;
	}

	public Athlete()
	{
		
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void recupAth (EntityManager em) throws IOException
	{
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		ArrayList<Athlete> listAth = new ArrayList<Athlete>();
		Set<Athlete> setAth = new LinkedHashSet<>();
			
		for (String l : lines) {
			

			Integer intAge = 0;
			Integer naissance  = 0;
			Integer taille = 0;
			Integer poids = 0;
			
			
			
			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
				
			}
			
			String genre = arrayS[2];
			
			Integer DateJO = Integer.parseInt(arrayS[9]);
			if (arrayS[3].matches("-?\\d+"))
			{
			intAge = Integer.parseInt(arrayS[3]);
			naissance  = DateJO - intAge;		
			}
			
			if (arrayS[4].matches("-?\\d+"))
			{
				taille = Integer.parseInt(arrayS[4]);
			}
			
			if (arrayS[5].matches("-?\\d+"))
			{
			poids = Integer.parseInt(arrayS[5]);
		
			}
			
			String nom = arrayS[1];
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
			
			String paysCode = arrayS[7];
			String team = arrayS[6];
			
			
			Athlete athlete = new Athlete ();
			
			athlete.setAnnee_Naissance(naissance);
			athlete.setGenre(genre);
			athlete.setNom(nom);
			athlete.setPrenom(prenom);
			athlete.setPoids(poids);
			athlete.setTaille(taille);
			
	/*		List<Pays> pays = (List<Pays>) em.createNamedQuery("findPaysByName", Pays.class).setParameter("pays", paysCode).getResultList();
			List<Equipe> equipe = (List<Equipe>) em.createNamedQuery("findEquipeByName", Equipe.class).setParameter("eq", team).getResultList();
			
			athlete.setEquipe(equipe);
			athlete.setPays(pays);*/
			
			listAth.add(athlete);
			
			
		}
		
		

		for (Athlete a : listAth)
		{
			if (a.equals(listAth)==false)
			{
				setAth.add(a);
			}
		}
		
		listAth.clear();
		listAth.addAll(setAth);
		for (Athlete a : listAth)
	{
			if (a.getPrenom() == "Ould Lamine")
			{
				System.out.println("POUET POUET POUET");
			}
		em.persist(a);
	}
	}
	
	
	 
public static void recupEquipe(EntityManager em) throws ClassNotFoundException, SQLException
{
List<Equipe> listEq = new ArrayList<>();

List<Athlete> allAth = em.createNamedQuery("findAllAthlete", Athlete.class).getResultList();




for (int i = 1; i <allAth.size(); i++)
{
	
	
	Statement stmt = DataBase.connectionDB();
	
	String query ="Select team from donnee_brute where id = "+allAth.get(i).id;
	System.out.println(query);
	ResultSet rs = stmt.executeQuery(query);
	HashSet<String> listEquip = new HashSet<String>();
	
	while (rs.next())
	{
		 String value = rs.getString(1);
		 listEquip.add(value);
		 rs.next();
	}
	
	for (String s : listEquip)
	{
		Equipe eq =  (Equipe) em.createNamedQuery("findEquipeByName", Equipe.class).setParameter("eq", s).getSingleResult();
		listEq.add(eq);
	}
	
	allAth.get(i).setEquipe(listEq);
	
}


}






	public List<Equipe> getEquipe() {
		return equipe;
	}

	public void setEquipe(List<Equipe> equipe) {
		this.equipe = equipe;
	}

	@Override
	public int hashCode() {
		return Objects.hash(annee_Naissance, genre, nom, poids, prenom, taille);
	}



	@Override
	public boolean equals(Object obj) {
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

	@Override
	public String toString() {
		return "Athlete [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", genre=" + genre + ", annee_Naissance="
				+ annee_Naissance + ", taille=" + taille + ", poids=" + poids + ", medaille=" + medaille + ", equipe="
				+ equipe + "]";
	}







	public String getPrenom() {
		return prenom;
	}







	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}







	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public Integer getAnnee_Naissance() {
		return annee_Naissance;
	}

	public void setAnnee_Naissance(Integer annee_Naissance) {
		this.annee_Naissance = annee_Naissance;
	}

	public Integer getTaille() {
		return taille;
	}

	public void setTaille(Integer taille) {
		this.taille = taille;
	}

	public Integer getPoids() {
		return poids;
	}

	public void setPoids(Integer poids) {
		this.poids = poids;
	}
	
	

}
