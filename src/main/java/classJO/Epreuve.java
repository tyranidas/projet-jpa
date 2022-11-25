package classJO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import createTable.DataBase;

@Entity
@Table(name = "Epreuve")
public class Epreuve {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "nom_FR", length = 100)
	private String nom_FR;

	@Column(name = "nom_ENG", length = 100, nullable = false)
	private String nom_ENG;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Id_Sport")
	private Sport sport;

	@ManyToMany
	@JoinTable(name = "EPREUVE_COMPETITION", joinColumns = @JoinColumn(name = "id_Epreuve", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"))
	private List<Competition> competition;

	@OneToMany(mappedBy = "epreuve")
	private List<Medaille> medaille;

	public Epreuve() {

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

	public static void traiterEpreuve(EntityManager em) throws ClassNotFoundException, SQLException, IOException {

		int compter = 0;

		List<Epreuve> listEp = new ArrayList<Epreuve>();
		HashMap<String, String> mapEpreuve = new HashMap<>();
		HashMap<String, String> mapNom = new HashMap<>();
		List<String> lines = DataBase.recupFichier("liste_des_epreuves");
		List<String> linesAth = DataBase.recupFichier("athlete_epreuves");
		String[] nom = new String[2];
		String requete = "SELECT * FROM Sport as s where s.nom_ENG=?";

		for (String ath : linesAth) {
			
			String[] arrayA = new String[15];
			for (int i = 0; i < ath.split(";").length; i++) {
				arrayA[i] = ath.split(";")[i];
			}
			String sport = arrayA[12];
			String ep = arrayA[13];
			ep = ep.replaceFirst(sport, "").trim();
			
			mapEpreuve.put(ep,sport);
		
			

		}
		if (mapEpreuve.containsKey("Mixed 2 x 6 kilometres and 2 x 7.5 kilometres Relay"))
		{
			
		}
		
		Iterator<Entry<String, String>> it = mapEpreuve.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = (Entry<String, String>) it.next();
		@SuppressWarnings("unchecked")
		List<Sport> classSport = em.createNativeQuery(requete, Sport.class).setParameter(1, entry.getValue())
					.getResultList();
			Epreuve epreuve = new Epreuve();
			
			System.out.println(entry.getKey());
				
			
			
			if (classSport.size() > 0) {

				epreuve.setSport(classSport.get(0));

			}
			epreuve.setNom_ENG(entry.getKey());

			System.out.println(epreuve.getNom_ENG());
			if (epreuve.getNom_ENG() != null && !(epreuve.getNom_ENG().isEmpty())) {
				
				em.persist(epreuve);
				listEp.add(epreuve);
			}

		}
		for (Epreuve e : listEp) {
			if (e.getNom_ENG() == "Mixed 2 x 6 kilometres and 2 x 7.5 kilometres Relay")
			{
				System.out.println(e.toString());
			}
			for (String l : lines) {
				String[] arrayS = new String[2];
				for (int i = 0; i < l.split(";").length; i++) {
					arrayS[i] = l.split(";")[i];

				}
				String nom_Fr = arrayS[1];
				String nom_En = arrayS[0];

				if (nom_Fr == null || nom_Fr.length() == 0) {
					nom_Fr = nom_En;
				}
				
				if (nom_En != null && nom_En.length() > 0)
					if (nom_En.equals(e.getNom_ENG())) {
						e.setNom_FR(nom_Fr);
					}
			
			} 
		}
		System.out.println(mapEpreuve.size());
		System.out.println("pouet");
	}

	public Sport getSport() {
		return sport;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}
}
