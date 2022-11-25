package classJO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import createTable.DataBase;

@Entity
@Table(name = "Equipe")
public class Equipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "nom", length = 100, nullable = false)
	private String nom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Id_Pays")
	private Pays equiPays;

	@ManyToMany
	@JoinTable(name = "EQUIPE_ATHLETE", 
	joinColumns = @JoinColumn(name = "id_Equipe", referencedColumnName = "id"), 
	inverseJoinColumns = @JoinColumn(name = "id_Athlete", referencedColumnName = "id"))
	private List<Athlete> athlete;

	@ManyToMany
	@JoinTable(name = "EQUIPE_COMPETITION", 
	joinColumns = @JoinColumn(name = "id_Equipe", referencedColumnName = "id"), 
	inverseJoinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"))
	private List<Competition> competition;

	public Equipe() {
	}

	
	public static void traiterEquipe(EntityManager em) throws ClassNotFoundException, SQLException, IOException {
		int compteur = 0;
		List<String> lines = DataBase.recupFichier("athlete_epreuves");
		HashMap<String, String> mapEqui = new HashMap<>();
		String requete = "SELECT * FROM Pays as p where p.CIO_Code=?";
		
		for (String l : lines) {
		
			String[] arrayS = new String[15];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];

			}
			
			String nom = arrayS[6];
			String pays = arrayS[7];
			
			
					mapEqui.put(nom, pays);
		}
		
			Iterator<Entry<String, String>> it = mapEqui.entrySet().iterator();
			while(it.hasNext())
			{
				Map.Entry<String, String> entry = (Entry<String, String>)it.next();
				
				System.out.println(entry.getKey());
				
				Equipe equipe = new Equipe();
				equipe.setNom(entry.getKey());
				@SuppressWarnings("unchecked")
				List<Pays> classPays = em.createNativeQuery(requete, Pays.class).setParameter(1, entry.getValue()).getResultList();
				;
				if (classPays.size()>0)
				{
					System.out.println(classPays.get(0).getNom_FR());
				
				equipe.setPays(classPays.get(0));
				compteur++;
				System.out.println(compteur);
				
				
				}
				System.out.println(equipe.getNom());
				em.persist(equipe);
			}
			
			
		
	

	
			
		
	}
	
	/*@SuppressWarnings("unchecked")
	List<Pays> classPays = em.createNativeQuery(requete, Pays.class).setParameter(1, pays).getResultList();
	;
	compter++;
	System.out.println(compter);
	if (classPays.size() > 0) {
		equi.setPays(classPays.get(0));
	}
*/
	public Pays getPays() {
		return equiPays;
	}

	public void setPays(Pays pays) {
		this.equiPays = pays;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nom, equiPays);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Equipe other = (Equipe) obj;
		return Objects.equals(nom, other.nom) && Objects.equals(equiPays, other.equiPays);
	}

}
