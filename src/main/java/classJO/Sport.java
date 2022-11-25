package classJO;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import createTable.DataBase;

@Entity
@Table(name = "SPORT")
public class Sport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "nom_FR", length = 50, nullable = false)
	private String nom_FR;

	@Column(name = "nom_ENG", length = 50, nullable = false)
	private String nom_ENG;

	@OneToMany(mappedBy = "sport")
	private List<Epreuve> epreuve;

	@ManyToMany
	@JoinTable(name = "SPORT_COMPETITION", joinColumns = @JoinColumn(name = "id_Sport", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "id_Competition", referencedColumnName = "id"))
	private List<Competition> competition;

	public Sport() {

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

	public List<Epreuve> getEpreuve() {
		return epreuve;
	}

	public void setEpreuve(List<Epreuve> epreuve) {
		this.epreuve = epreuve;
	}

	public List<Competition> getCompetition() {
		return competition;
	}

	public void setCompetition(List<Competition> competition) {
		this.competition = competition;
	}

	public static void traiterSport(EntityManager em) throws ClassNotFoundException, SQLException, IOException {

		List<String> lines = DataBase.recupFichier("liste des sports");
	

		for (String l : lines) {
			l = l.replace("'", "\\'");
			String[] arrayS = new String[2];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];

			}
			String nom_Fr = arrayS[1];
			String nom_En = arrayS[0];
			Sport sport = new Sport();

			if (nom_Fr == null || nom_Fr.length() == 0) {
				nom_Fr = nom_En;
			}
			sport.setNom_FR(nom_Fr);
			sport.setNom_ENG(nom_En);
			em.persist(sport);
		

		}
	}
}
