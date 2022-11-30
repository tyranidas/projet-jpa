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

@Entity
@NamedQueries({ @NamedQuery(name = "findAllPays", query = "Select p FROM Pays as p"),
	@NamedQuery(name = "findAthByNomForPays", 
	query = "SELECT a FROM Athlete as a where a.nom =:nom "
				+ " and a.prenom=:prenom " 
			+ "and a.taille = :taille " 
			+ "and a.poids = :poids "
			+ "and a.genre = :genre "
			+ "and a.annee_Naissance = :age"), 
			
@NamedQuery(name = "findAthByNomForPaysIfNull", 
query = "SELECT a FROM Athlete as a where a.nom =:nom "
		+ " and  a.prenom IS NULL " 
	+ "and a.taille = :taille " 
	+ "and a.poids = :poids "
	+ "and a.genre = :genre "
	+ "and a.annee_Naissance = :age") 
	})

@Table(name="PAYS")
public class Pays {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@Column (name="CIO_Code", length = 3, nullable = false)
	private String cio_Code;
	
	@Column (name="nom_FR", length = 50, nullable = false)
	private String nom_FR;
	
	@Column (name="nom_ENG", length = 50, nullable = false)
	private String nom_ENG;
	
	@Column (name="code_ISO_Alpha3", length = 3)
	private String iso;
	
	@Column(name= "obsolète", length = 1, nullable = false)
	private String obs;
	
	@ManyToMany
	@JoinTable (name="PAYS_ATHLETE",
	joinColumns= @JoinColumn(name="id_Pays", referencedColumnName ="id"),
	inverseJoinColumns= @JoinColumn(name="id_Athlete", referencedColumnName = "id")
	)
	private List<Athlete> athlete;
	
	@OneToMany(mappedBy = "equiPays")
	private List<Equipe> equipe;

		public Pays()
	{
		
	}
		
	
	@Override
	public int hashCode() {
		return Objects.hash(cio_Code, iso, nom_ENG, nom_FR);
	}


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


	@SuppressWarnings("unlikely-arg-type")
	public static void traiterPays (EntityManager em) throws ClassNotFoundException, SQLException, IOException
	{
		

		Path path = Paths.get(".\\src\\main\\resources\\wikipedia_iso_country_codes.csv");
		List<String> lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
		lines.remove(0);
		
		//List<String> lines = DataBase.recupFichier("wikipedia_iso_country_codes");
		ArrayList<Pays> listPays = new ArrayList<Pays>();
		HashSet<Pays> setPays = new LinkedHashSet<>();
		
		for (String l : lines) {
			l = l.replace("'", "\\'");
			String[] arrayS = new String[5];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
				
			}
			String cio_Code = arrayS[0];
			String nom_FR =  arrayS[1];
			String nom_ENG =  arrayS[2];
			String iso=  arrayS[3];
			String obs =  arrayS[4];
			
			Pays pays = new Pays ();
			
			pays.setCio_Code(cio_Code);
			pays.setNom_FR(nom_FR);
			pays.setNom_ENG(nom_ENG);
			pays.setIso(iso);
			pays.setObs(obs);
			
			
			listPays.add(pays);
			
		}
		
		for (Pays p : listPays)
		{
			if (p.equals(listPays)==false)
			{
			
				setPays.add(p);
			}
		}
		listPays.clear();
		listPays.addAll(setPays);
		for (Pays p : listPays)
		{
			em.persist(p);
		}
	} 
	

	public static void recupAth(EntityManager em) throws ClassNotFoundException, SQLException {
	

		List<Pays> allPays = em.createNamedQuery("findAllPays", Pays.class).getResultList();

		for (int i = 0; i < 100 ; i++) 
			//allPays.size()
		{
			
			Statement stmt = DataBase.connectionDB();
		
			String query = "Select name, height, weight, sex, age, year from donnee_brute where noc = \"" + allPays.get(i).getCio_Code()+"\"";
			ResultSet rs = stmt.executeQuery(query);
			
			List<Athlete> listAth = Athlete.findAth(rs, em);
			
			allPays.get(i).setAthlete(listAth);

			
		}
	}	


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCio_Code() {
		return cio_Code;
	}

	public void setCio_Code(String cio_Code) {
		this.cio_Code = cio_Code;
	}

	public String getNom_FR() {
		return nom_FR;
	}

	public void setNom_FR(String nom_FR) {
		this.nom_FR = nom_FR;
	}

	public String getNom_ENG() {
		return nom_ENG;
	}

	public void setNom_ENG(String nom_ENG) {
		this.nom_ENG = nom_ENG;
	}

	public String getIso() {
		return iso;
	}

	public void setIso(String iso) {
		this.iso = iso;
	}

	public String getObs() {
		return obs;
	}

	public void setObs(String obs) {
		this.obs = obs;
	}

	public List<Athlete> getAthlete() {
		return athlete;
	}

	public void setAthlete(List<Athlete> athlete) {
		this.athlete = athlete;
	}

	public List<Equipe> getEquipe() {
		return equipe;
	}

	public void setEquipe(List<Equipe> equipe) {
		this.equipe = equipe;
	}
	
	
	
}
