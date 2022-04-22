package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
 * Classe principale du laboratoire 4
 * 
 *  
 * @author el hachemi Alikacem 
 * @version 1
 *
 */
public class Laboratoire4 {
	
	public static Statement statmnt = null;
	
	public static Connection connexion = null;
	
	static {
	   try {
		   Class.forName("oracle.jdbc.driver.OracleDriver");
		   
	   } catch (ClassNotFoundException e) {
		
		   e.printStackTrace();
	   }
	}
	
	//Question 1 Ouverture de la connection
    public static Connection connexionBDD(String login, String password, String uri) {

    	Connection une_connexion = null ;
    	// TODO
        try {
            une_connexion = DriverManager.getConnection(uri, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return une_connexion;
    }
    
    
    // Question 2 - lister les inscriptions 
    public static void listeInscription() {
    	//TODO : VERIFIER
        ResultSet result;
        String select = "select * from inscription where DATE_ABANDON is null";
        try {
            statmnt = connexion.createStatement();
            result = statmnt.executeQuery(select);
            // Parcourir tous les rows.
            // Affiche les noms des colonnes de la table Inscription.
            System.out.printf("Code permanent\t");
            System.out.printf("Sigle\t");
            System.out.printf("No groupe\t");
            System.out.printf("Code session\t");
            System.out.printf("Date inscription\t");
            System.out.printf("Date abandon\t");
            System.out.printf("Note\t");
            System.out.printf("\n");

            // TODO : Formattage output.
            while (result.next()) {
                System.out.printf(result.getString("code_permanent")+"\t");
                System.out.printf(result.getString("sigle")+"\t");
                System.out.printf(result.getInt("no_groupe")+"\t\t\t\t");
                System.out.printf(result.getInt("code_session")+"\t\t");
                System.out.printf(result.getDate("date_inscription") +"\t \t\t\t\t");
                System.out.printf(result.getDate("date_abandon") + "\t");
                System.out.printf(result.getDouble("note") +"\t");
                System.out.println();
            }
            result.close();
        } catch (SQLException exception){
            exception.printStackTrace();
        }
    }
    
    // Question 3 - lister les cours d'une session
    // Implémenter la méthode listeCoursSession(), qui affiche tous les cours offerts dans
    //l’établissement pour une session donnée. La fonction reçoit en argument le code de la
    //session.

    public static void listeCoursSession(int codeSession) {
    	// TODO : Note : la méthode affiche un message approprié en cas de problèmes, d’erreurs ou si le code de la session n’existe pas.
        // Préparer le select.
        String select = " select c.*\n"+
                        " from COURS C\n"+
                        " inner join GROUPECOURS G on C.SIGLE = G.SIGLE\n"+
                        " where g.code_session = ?";
        try {
            PreparedStatement requete = connexion.prepareStatement(select);
            requete.setInt(1, codeSession);
            // Executer la rêquete, i.e: on envoie la rêquete au Server SQL.
            ResultSet resultat = requete.executeQuery();
            System.out.printf("Sigle\t");
            System.out.printf("Titre\t\t\t\t");
            System.out.printf("Nb credits\t");
            System.out.printf("\n");
            while(resultat.next()){
                System.out.printf(resultat.getString("sigle")+ "\t");
                System.out.printf(resultat.getString("titre")+ "\t\t\t");
                System.out.printf(resultat.getInt("nb_credits")+"\t");
                System.out.println();
            }
            resultat.close();
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
    }

    // Question 4 - calculer et afficher le cout d'une session pour un étudiant
    public static void coutSession(String nom, String prenom, int codeSession) {
        // TODO : La méthode affiche un message que l’étudiant n’existe pas si c’est le cas, ou un message d’erreur en cas d’échec.
        // Vérifier l'étudiant fait l'inscription aux cours à la session donnée.
        String select = "select SUM(C.NB_CREDITS) AS nb_credits \n" +
                "from ETUDIANT E\n" +
                "inner join INSCRIPTION I\n" +
                "on E.CODE_PERMANENT = I.CODE_PERMANENT\n" +
                "inner join GROUPECOURS G on G.SIGLE = I.SIGLE and G.NO_GROUPE = I.NO_GROUPE and G.CODE_SESSION = I.CODE_SESSION\n" +
                "inner join COURS C on C.SIGLE = G.SIGLE\n" +
                "where E.nom = ? AND E.prenom = ? AND I.CODE_SESSION = ?";
        try {
            // On va tester
            // Nom              : Tremblay
            // Prenom           : Jean
            // Code de session  : 32003
            // Code permanent   : TREJ18088001

            PreparedStatement statement = connexion.prepareStatement(select);
            statement.setString(1, nom);
            statement.setString(2, prenom);
            statement.setInt(3, codeSession);

            ResultSet resultat = statement.executeQuery();

            while (resultat.next()) {
                System.out.printf("Nb credits : %d\n", resultat.getInt("nb_credits"));
            }
            resultat.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // Question 5 - supprimer une Inscription   
    public static void supprimeInscription(String codePermanent, String sigle, 
    		                               int noGroupe , int codeSession) {
    	// TODO : A verifier
        String deleteInscription = "DELETE FROM INSCRIPTION" +
                " WHERE CODE_PERMANENT = ? AND SIGLE = ? AND NO_GROUPE = ? AND CODE_SESSION = ?";
        try {
            PreparedStatement statement = connexion.prepareStatement(deleteInscription);
            statement.setString(1, codePermanent);
            statement.setString(2, sigle);
            statement.setInt(3, noGroupe);
            statement.setInt(4, codeSession);

            int nbSupprimes = statement.executeUpdate(deleteInscription);
            if (nbSupprimes > 0)
            {
                System.out.println("succes");
            }
            else
            {
                System.out.println("echec");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Question 6 -  Ajouter un cours dans la base de données 
    public static void AjoutCours(String newSigle , String titreCours, int nbreCredit, 
    		                      ArrayList<String> listPrealable) {
    	String ajoutCours = "INSERT INTO COURS (sigle, titre, nb_credits) (?, ?, ?);";
    	String ajoutPrealable;
    	int coursAjoutes = 0;
    	int index = 2;
    		try {
    			
        		PreparedStatement cours = connexion.prepareStatement(ajoutCours);
        		cours.setString(1, newSigle);
        		cours.setString(2, titreCours);
        		cours.setInt(3, nbreCredit);
        		
        		for (String prealable : listPrealable) {
            		ajoutPrealable = "INSERT INTO PREALABLE (sigle, sigle_prealable) (?, ?);";
            		PreparedStatement prealables = connexion.prepareStatement(ajoutPrealable);
            		prealables.setString(1, newSigle);
            		prealables.setString(2, prealable);
            		coursAjoutes++;
            		
        		}
    		} catch (SQLException e) {
        		e.printStackTrace();
        	}
    }
  
    

    // Question 7 - calculer de la cote   
    public static void calculCote() {
    	String calcul = "";
    	try {
			PreparedStatement calculAFaire;
			Statement requete = connexion.createStatement();
	    	ResultSet resultats = requete.executeQuery("SELECT * FROM INSCRIPTION");
				
				
				System.out.println("+----------------+--------------+---------------+---------------+---------------------+---------------------+--------+----------+  ");
				System.out.println("| CODE_PERMANENT | SIGLE        | NO_GROUPE     | CODE_SESSION  | DATE_INSCRIPTION    | DATE_ABANDON        | NOTE   | COTE     |  ");
				System.out.println("+----------------+--------------+---------------+---------------+---------------------+---------------------+--------+----------+  ");
				
				while (resultats.next()) {
					int note =  resultats.getInt("note");
					
					if (note >= 90 && note <= 100) {
						calcul = "INSERT INTO INSCRIPTION (COTE) VALUES (?)";
						calculAFaire = connexion.prepareStatement(calcul);
						calculAFaire.setString(1, "A");
						
					} else if (note >= 80 && note <= 89) {
						calcul = "INSERT INTO INSCRIPTION VALUES (COTE) (?)";
						calculAFaire = connexion.prepareStatement(calcul);
						calculAFaire.setString(1, "B");
						
					} else if (note >= 70 && note <= 79) {
						calcul = "INSERT INTO INSCRIPTION VALUES (COTE) (?)";
						calculAFaire = connexion.prepareStatement(calcul);
						calculAFaire.setString(1, "C");
						
					} else if (note >= 60 && note <= 69) {
						calcul = "INSERT INTO INSCRIPTION VALUES (COTE) (?)";
						calculAFaire = connexion.prepareStatement(calcul);
						calculAFaire.setString(1, "D");
						
					} else if (note >= 0 && note <= 59) {
						calcul = "INSERT INTO INSCRIPTION VALUES (COTE) (?)";
						calculAFaire = connexion.prepareStatement(calcul);
						calculAFaire.setString(1, "E");
					}; 
					
					
					System.out.printf("| %14s | %-12s | %13d | %-13d | %-18s | %-19s | %6d | %8s |\n" , 
							resultats.getString("code_permanent"),
							resultats.getString("sigle"),
							resultats.getInt("no_groupe"),
							resultats.getInt("code_session"),
							resultats.getString("date_inscription"),
							resultats.getString("date_abandon"),
							resultats.getInt("note"),
							resultats.getString("cote"));
				}
				
				System.out.println("+----------------+--------------+---------------+---------------+---------------------+---------------------+--------+----------+  \n");
				System.out.println("succes");
				
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("echec");
		}
    	
    	
    }

    // Question 8 - Afficher les statistiques    
    public static void statistiqueCours() {
    	
    	try {
			Statement requeteTableau = connexion.createStatement();
			ResultSet resultats = requeteTableau.executeQuery("SELECT * FROM COURS");
			
			//Requete inscriptions
			Statement requeteInscriptions = connexion.createStatement();
			ResultSet resultatsInscription = requeteInscriptions.executeQuery("SELECT COUNT(CODE_PERMANENT) AS NB_INSCRIPTIONS FROM INSCRIPTION");
			//int nb_inscriptions;
			
			//Requete etudiants haute note
			Statement requeteNote = connexion.createStatement();
			ResultSet resultatsNote = requeteNote.executeQuery("SELECT COUNT(*) AS NB_NOTE FROM INSCRIPTION WHERE NOTE >= 80");
			//int nb_note;
			
			//Requete note nim
			Statement requeteMin = connexion.createStatement();
			ResultSet resultatMin = requeteMin.executeQuery("SELECT MIN(NOTE) AS NB_MIN FROM INSCRIPTION");
			int nb_min;
			
			
			//Requete note max
			Statement requeteMax = connexion.createStatement();
			ResultSet resultatMax = requeteMax.executeQuery("SELECT MAX(NOTE) AS NB_MAX FROM INSCRIPTION");
			int nb_max;
			
			//Moyenne des notes
			Statement requeteAvg = connexion.createStatement();
			ResultSet resultatAvg = requeteMax.executeQuery("SELECT AVG(NOTE) AS NB_AVG FROM INSCRIPTION");
			float nb_avg;
			
			
			System.out.println("+---------+----------------------------------------------------+-----------------+-----------------+---------------+---------------+---------------+  ");
			System.out.println("| SIGLE   | TITRE                                              | NB INSCRIPTIONS |  NB_NOTE_HAUTE  | NOTE MINIMALE | NOTE MAXIMALE | NOTE MOYENNE  |");
			System.out.println("+---------+----------------------------------------------------+-----------------+-----------------+---------------+---------------+---------------+  ");
			
			while (resultats.next()) {
				String titre = resultats.getString("titre");
				int nb_inscriptions = resultatsInscription.getInt("nb_inscriptions");
				int nb_note = resultatsNote.getInt("nb_note");
				nb_min = resultatMin.getInt("nb_min");
				nb_max = resultatMax.getInt("nb_max");
				nb_avg = resultatAvg.getFloat("nb_avg");
				
				
				System.out.printf("| %6s | %50s | %15d | %15d | %15d | %15d | %15d | \n", resultats.getString("sigle"), resultats.getString("titre"), nb_inscriptions, nb_note, nb_min, nb_max, nb_avg);
			}
			System.out.println("+---------+----------------------------------------------------+-----------------+---------------+---------------+  ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    // Question 9 - Écrire une fonction nommé fermetureConnexion() permettant de vous déconnecter de la
    //connexion actuelle. Cette fonction ne prendra aucun argument en entrée, mais retourne un
    //booléen true/false indiquant le succès ou l’échec de l’opération.
    public static boolean fermetureConnexion() {
        boolean resultat = false ;
        try {
            connexion.close();
            resultat = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat ;
    }

    
    /* ===================================================================== */ 
    /**
     * Extrait les éléments séparés par une virgule dans d'une liste
     * et retourne les éléments dans un objet ArrayList
     *   
     * @param listPrealable
     * @return
     */
    private static ArrayList<String> toArrayList(String listPrealable) {
    	ArrayList<String> res = new ArrayList<String>() ; 
    	StringTokenizer st1 = new StringTokenizer(listPrealable , ",");    	
    	while (st1.hasMoreTokens()) {
    		String un_token = st1.nextToken().trim();          
         	res.add(un_token) ; 
    	}
 
    	return res ; 
    } //  toArrayList()
    
    /* ------------------------------------------------------------------------- */      
    /**
     * Affiche un menu pour le choix des opérations 
     * 
     */
    public static void afficheMenu(){
        System.out.println("0. Quitter le programme");
        System.out.println("1. Afficher la liste des inscriptions");
        System.out.println("2. Afficher la liste des cours d'une session");
        System.out.println("3. Afficher le coût d'une session pour un étudiant");
        System.out.println("4. Supprimer une inscription");
        System.out.println("5. Ajouter un nouveau cours dans la base de données");
        System.out.println("6. Caluler les cotes");
        System.out.println("7. Afficher les statistiques des cours");
        System.out.println("Votre choix...");
    }
    
	/**
	 * La méthode main pour le lancement du programme 
	 * Il faut mettre les informations d'accès à la BDD  
	 * 
	 * @param args
	 */
	public static void main(String args[]){
		
		// Mettre les informations de votre compte sur SGBD Oracle 
		String username = "equipe316" ;
		String motDePasse = "vQHvDzYX" ;
		String uri = "jdbc:oracle:thin:@tch054ora12c.logti.etsmtl.ca:1521:TCH054" ;

		// Appel de le méthode pour établir la connexion avec le SGBD 
		connexion = connexionBDD(username , motDePasse , uri ) ;

		if (connexion != null) {
			
			System.out.println("Connection reussie...");
			
			// Affichage du menu pour le choix des opérations 
			afficheMenu(); 
             
			Scanner sc = new Scanner(System.in);
            String choix = sc.nextLine();
            
            while(!choix.equals("0")){
           	
                if(choix.equals("1")){
 
                    listeInscription() ; 
                    
                 }else if(choix.equals("2")){
 
                    System.out.print("Veuillez saisir le code de la session : ");
                    sc = new Scanner(System.in);                    
                    Integer codeSession = sc.nextInt();    
                    
                    listeCoursSession(codeSession.intValue()) ;
                                     
                 }else if(choix.equals("3")){
                    System.out.print("Veuillez saisir le nom de l'etudiant(e): ");
                    //System.out.println("Veuillez saisir le code permanent de l'etudiant(e): ");
                    sc = new Scanner(System.in);

                    String nom = sc.next();
                    System.out.print("Veuillez saisir le prenom de l'etudiant(e): ");
                    String prenom = sc.next();
                    //String codePermanent = sc.nextLine().trim() ;
                    System.out.print("Veuillez saisir le code de la session : ");
                    Integer codeSession = sc.nextInt();
                    coutSession(nom, prenom, codeSession);
                    
                 }else if(choix.equals("4")){
                	
                	sc = new Scanner(System.in);
                	
                    System.out.print("Veuillez saisir le code permanent de l'etudiant(e) à supprimer : ");                    
                    String codePermanent = sc.nextLine().trim();
                    
                    System.out.print("Veuillez saisir le sigle du cours : ");
                    String sigle = sc.nextLine().trim();
                    
                    System.out.print("Veuillez saisir le numero de groupe : ");
                    Integer noGroupe = sc.nextInt();
                    
                    System.out.print("Veuillez saisir le code de la session : ");
                    Integer codeSession = sc.nextInt();
                    
                    supprimeInscription(codePermanent, sigle, noGroupe.intValue() , codeSession.intValue())  ;
                                                       
                 }else if(choix.equals("5")){
                	 
                    sc = new Scanner(System.in);

                    System.out.print("Veuillez introduire le sigle du nouveau cours : ");
                    String newSigle= sc.nextLine().trim();
                    
                    System.out.print("Veuillez introduire le titre du nouveau cours : ");                   
                    String newTitre= sc.nextLine().trim();
                    
                    
                    System.out.print("Veuillez introduire le nombre de crédit du nouveau cours : ");
                    Integer nbreCredit = sc.nextInt();
                                                         
                    System.out.print("Veuillez introduire la liste des préalables séparés par des virgules  : ");
                    String listePrealables = sc.nextLine().trim();
                    
                    // Transfert des préalables de la liste vers une ArrayList 
                    ArrayList<String> prealableArrayList = toArrayList(listePrealables) ;                     
                    AjoutCours(newSigle , newTitre, nbreCredit.intValue() , prealableArrayList) ; 
                    
                 }else if(choix.equals("6")){                    
                	 calculCote() ;
                	 
                 }else if(choix.equals("7")){
                    statistiqueCours() ;
                    
                 }

                afficheMenu();
                sc = new Scanner(System.in);
                choix = sc.nextLine();
            	
            } // while 

            // FIn de la boucle While - Fermeture de la connexion 
            if(fermetureConnexion()){
                System.out.println("Deconnection reussie...");
                
            }else{
                System.out.println("Échec ou Erreur lors de le déconnexion...");
            }
            
		 } else {  // if (connexion != null) {
			 
			 System.out.println("Echec de la Connection. Au revoir ! ");
			 
		 } // if (connexion != null) {
	        
	} // main() 
	
}