package test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import classJO.Athlete;
import classJO.Pays;

public class PouetReplace {
	

	public static void main(String[] args) 
	{
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Jeux_Olympiques");
		EntityManager em = entityManagerFactory.createEntityManager();
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		
		String requete = "SELECT a FROM Athlete a where a.id=1";
		
		
	
		TypedQuery<Athlete> result =  em.createQuery(requete, Athlete.class);
			
		List<Athlete> listath = result.getResultList();
		
		for (Athlete a : listath)
		{
			System.out.println("pouet"+a.getNom());
		}
			
				
	}
	
	
	
}
