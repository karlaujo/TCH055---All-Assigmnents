package database;
import java.sql.*;

public class RequetePanier {
	private DatabaseManager db_manager;
	private Connection connexion_jdbc;
	
	public void start(String user, String pass) {
		db_manager = new DatabaseManager(user, pass);
		boolean reponse = db_manager.createConnection();
		if (reponse) {
			System.out.println("La connexion est faite!\n");
		} else {
			System.out.println("La connexion n'a pas plus etre effectuee");
			db_manager = null;
		}
		
		connexion_jdbc = db_manager.getConnection();
	}
	
	public void terminate() {
		db_manager.terminateConnection();
	}
	
	public void listAllProducts() {
		System.out.println("Question 4: Liste des produits");
		if (db_manager != null) {
			try {
				Statement requete = connexion_jdbc.createStatement();
				ResultSet resultats = requete.executeQuery("SELECT * FROM Produit");
				int nb = 0;
				System.out.println("+--------+--------------+---------------+---------------+----------+----------+----------+----------+  ");
				System.out.println("| No     | Nom          | Marque        | Modele        | Prix     | Quantite | Statut   | Code     |  ");
				System.out.println("|--------+--------------+---------------+---------------+----------|----------+----------|----------+  ");
				
				while (resultats.next()) {
					System.out.printf("| %6d | %-12s | %-13s | %-13s | %8.2f | %8d | %8s | %8d |\n" , 
							nb+1,
							resultats.getString("nom_produit"),
							resultats.getString("marque"),
							resultats.getString("modele"),
							resultats.getFloat("prix"),
							resultats.getInt("quantite"),
							resultats.getString("statut"),
							resultats.getInt("code_produit"));
					nb++;
				}
				System.out.println("+--------+--------------+---------------+---------------+----------+----------+----------+----------+  \n");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.print("Erreur: Connexion impossible");
		}
		
	}
	
	public void listProduct(int code_p) {
		System.out.println("Question 5: Liste le produit "+code_p);
		if (db_manager != null){
			try {
				Statement requete = connexion_jdbc.createStatement();
				ResultSet resultats = requete.executeQuery(""
						+ "SELECT * FROM Produit p WHERE "+code_p+" = p.code_produit ");
				
				System.out.println("+--------+--------------+---------------+---------------+----------+----------+----------+----------+  ");
				System.out.println("| No     | Nom          | Marque        | Modele        | Prix     | Quantite | Statut   | Code     |  ");
				System.out.println("|--------+--------------+---------------+---------------+----------|----------+----------|----------+  ");
				
				while (resultats.next()) {
					System.out.printf("| %6d | %-12s | %-13s | %-13s | %8.2f | %8d | %8s | %8d |\n" , 
							code_p,
							resultats.getString("nom_produit"),
							resultats.getString("marque"),
							resultats.getString("modele"),
							resultats.getFloat("prix"),
							resultats.getInt("quantite"),
							resultats.getString("statut"),
							resultats.getInt("code_produit"));
				}
				System.out.println("+--------+--------------+---------------+---------------+----------+----------+----------+----------+  \n");
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.print("Erreur: Connexion impossible");
		}
	}
	
	public void prixquantiteProduit(String marque, String nom) {
		System.out.println("Question 6: Prix et Quantite du produit "+nom+" de "+marque);
		if (db_manager != null) {
				try {
				PreparedStatement prepRequete = db_manager.getConnection().prepareStatement("SELECT prix, quantite FROM Produit WHERE marque = ? AND nom_produit = ?");
				
				prepRequete.setString(1, marque);
				prepRequete.setString(2, nom);
				
				ResultSet resultats = prepRequete.executeQuery();
				
				int nb=1;
				
				System.out.println("+--------+----------+----------+  ");
				System.out.println("| No     | Prix     | Quantite |  ");
				System.out.println("|--------+----------+----------+  ");
				
					while (resultats.next()) {
						System.out.printf("| %6d | %8.2f | %8d | \n", nb, resultats.getFloat("prix"), resultats.getInt("quantite"));
						
					}	
					System.out.println("+--------+----------+----------+ \n");
					
				} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else {
			System.out.print("Erreur: Connexion impossible");
		}
	}
	
	public void addItemToPanier(int id_p, int code_p, int quantite_p) {
		System.out.println("Question 7: insertion du produitn de l'id "+id_p+" et quantite de "+quantite_p);
		if (db_manager != null) {
			try {
				Statement requete = db_manager.getConnection().createStatement();
				String sQuery = "INSERT INTO ItemProduit (id_panier, code_produit, quantite) VALUES ("+id_p+" , "+code_p+" , "+quantite_p+") ";
				int nbAffecte=requete.executeUpdate(sQuery);
				
				System.out.println("Nombre de lignes affecte: "+nbAffecte);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else {
			System.out.println("Erreur: Connexion impossible");
		}
	}
	
	public static void main(String []args) {
		RequetePanier requete = new RequetePanier();
		
		//question 3
		requete.start("equipe316", "vQHvDzYX");
		
		//question 4
		requete.listAllProducts();
		
		//question 5
		requete.listProduct(122);
		
		//question 6
		requete.prixquantiteProduit("sony", "phone");
		
		//question 7
		requete.addItemToPanier(10, 122, 3);
		
		requete.terminate();
	}
}
