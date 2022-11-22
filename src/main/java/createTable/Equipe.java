package createTable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;



public class Equipe {

	public static void main(String[] args) throws IOException {
		
		
		
	
		Path path = Paths.get(".\\src\\main\\resources\\liste_des_epreuves.csv");
		List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
		lines.remove(0);
		
		
		
		for (String l : lines) {
			
			System.out.println(l);
		/*	String[] arrayS = new String[2];
			for (int i = 0; i < l.split(";").length; i++) {
				arrayS[i] = l.split(";")[i];
				System.out.println(arrayS[0]);
			}*/
		}
		

	}

}
