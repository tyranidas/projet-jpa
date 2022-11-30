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

	@NamedQuery(name="findPaysByName", query = "SELECT p FROM Pays as p where p.cio_Code =:pays"),
	
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
	private Double taille;
	
	@Column (name="poids")	
	private Double poids;
	
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
			double taille = 0;
			double poids = 0;
			
			
			
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
			
			if (!arrayS[4].equals("NA"))
			{
				arrayS[4] = arrayS[4].replace(".", ".5");
				taille = Double.parseDouble(arrayS[4]);
			}
			
			if (!arrayS[5].equals("NA"))
			{
				arrayS[5] = arrayS[5].replace(".", ".1");
			poids = Double.parseDouble(arrayS[5]);
		
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
	
	
	@SuppressWarnings("unlikely-arg-type")
	public static List<Athlete> findAth(ResultSet rs, EntityManager em) throws SQLException
	{
		List<Athlete> listAth = new ArrayList<>();
		HashSet<Athlete> setAth = new LinkedHashSet<>();
		HashSet<String[]> listInfos = new HashSet<String[]>();
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

		for (String[] s : listInfos) {
			System.out.println();
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
			Athlete ath = new Athlete();
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
		return listAth;
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

	public Double getTaille() {
		return taille;
	}

	public void setTaille(Double taille) {
		this.taille = taille;
	}

	public Double getPoids() {
		return poids;
	}

	public void setPoids(Double poids) {
		this.poids = poids;
	}
	
	

}
