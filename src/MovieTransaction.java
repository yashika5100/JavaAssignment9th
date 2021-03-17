import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.util.Date;

class Movie{
	
	private int movieId; 
	private String movieName; 
	private String movieType;
	private String language;
	private String releaseDate; 
	List<String> casting;
	private double rating;
	private double totalBusinessDone;
	static int i=1;
	static Connection con=null;
	static String url="jdbc:mysql://localhost:3306/moviedetail";
	static String username="root";
	static String password="12345";
	//static List<Movie> mList=new ArrayList<Movie>();
	
	
	Movie(int movieID,String name,String type,String lang,String date,List<String> cast,double rating,double business){
		this.movieId= movieID;
		this.movieName=name;
		this.movieType=type;
		this.language=lang;
		this.releaseDate=date;
		this.casting=cast;
		this.rating=rating;
		this.totalBusinessDone=business;
	}
	
	
	
	public static List<Movie> populateMovies(File f) throws Exception{
		List<Movie> mlist = new ArrayList<Movie>();
        Path p=Paths.get(f.toString());
        if(Files.exists(p)){
        	List<String> content=Files.readAllLines(p);
            for(String list:content){
            	String elements[]=list.split(",");
                List<String>castlist = new ArrayList<String>();
                String cast[]=elements[5].split(";");
                for(int i=0;i<cast.length;i++) {
            	     castlist.add(cast[i]);
                }
            
            Movie m=new Movie(Integer.parseInt(elements[0]),elements[1],elements[2],elements[3],elements[4],castlist,Double.parseDouble(elements[6]),Double.parseDouble(elements[7]));
       
            mlist.add(m);
            }

        }
        return mlist;
	}
	
	public static void addToDb(List<Movie> m) throws Exception {
		
		Statement stm;
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		con=DriverManager.getConnection(url,username,password);
		PreparedStatement p=con.prepareStatement("INSERT INTO `moviedetail`.`movie`(`movieId`,`movieName`,`movieType`,`moviesLang`,`releaseDate`,`rating`,`movieBusiness`) VALUES (?,?,?,?,?,?,?);");
		PreparedStatement pr=con.prepareStatement("INSERT INTO `moviedetail`.`cast`(`castId`,`actorName`,`movieId`) VALUES (?,?,?);");
		try{
			for(Movie s: m) {
			
			
			p.setInt(1, s.movieId);
			p.setString(2, s.movieName);
			p.setString(3, s.movieType);
			p.setString(4, s.language);
			p.setString(5, s.releaseDate);
			p.setString(6, String.valueOf(s.rating));
			p.setString(6, String.valueOf(s.totalBusinessDone));
			p.execute();
			for(String f:s.casting) {
				pr.setInt(1, i);
				i++;
				pr.setString(2, f);
				pr.setInt(3, s.movieId);
				pr.execute();
				pr.close();
			}
		}
	}
		catch(Exception e) {
			System.out.println(e);
		}
		
	}
	
	public static void addMovie(Movie movie,List<Movie> m) {
		m.add(movie);
	}
	
	public static void serializeMovies(List<Movie> movies, String fileName) {
		try{
		       
	        File file=new File(fileName);
	        FileWriter myWriter = new FileWriter(file,true);
	        
	        String mve;
	        for(Movie m:movies) {
	        	String casting="";
	        	for(String c:m.casting) {
	        		casting+=c+";";
	        	}
	        	mve=m.movieId+","+m.movieName+","+m.movieType+","+m.language+","+m.releaseDate+","+casting+","+m.rating+","+m.totalBusinessDone+"\n";
	        	myWriter.write(mve);
		        myWriter.flush();
		       // myWriter.close();
	        }
	        
	        }
	        catch(Exception e){
	            System.out.println(e);
	        }
	}
	
	public static List<Movie> deserializeMovie(String filename) throws Exception{
		List<Movie> mlist = new ArrayList<Movie>();
        Path p=Paths.get(filename);
        if(Files.exists(p)){
        	List<String> content=Files.readAllLines(p);
            for(String list:content){
//            	System.out.println("content list:");
//                System.out.println(list);
            	String elements[]=list.split(",");
                List<String>castlist = new ArrayList<String>();
                String cast[]=elements[5].split(";");
                for(int i=0;i<cast.length;i++) {
            	     castlist.add(cast[i]);
                }
            
            Movie m=new Movie(Integer.parseInt(elements[0]),elements[1],elements[2],elements[3],elements[4],castlist,Double.parseDouble(elements[6]),Double.parseDouble(elements[7]));
            mlist.add(m);
            }

        }
        return mlist;
	}
	
	public static List<Movie> getMoviesRealeasedInYear(int year,List<Movie>m) throws Exception{
		List<Movie> mlist = new ArrayList<Movie>();
		for (Movie mon: m)
		{
//			System.out.println("releae date:");
//			System.out.println(Integer.parseInt(mon.releaseDate.substring(0,4)));
			int y=Integer.parseInt(mon.releaseDate.substring(0,4));
			if(y==year)
			{
				mlist.add(mon);
			}
		}
		return mlist;
	}
	
	public static void updateRatings(Movie movie, double rating ,List<Movie> movies) throws Exception{
		for(Movie m:movies) {
			if(m.movieId == movie.movieId) {
				m.rating=rating;
			}
		}
	}
	
	public static void updateBusiness(Movie movie, double amount,List<Movie> movies)
	{
		for (Movie m:movies)
		{
			if(m.movieId==movie.movieId)
			{
				m.totalBusinessDone=amount;
			}
		}
	}
	
//	public static Map<String,Set<Movie>> businessDone(double amount,List<Movie>mlist)
//	{
//		Set<Movie> bus=new TreeSet<Movie>();
//		Map<String,Set<Movie>> map=new TreeMap<String,Set<Movie>>();
//		for (Movie m:mlist)
//		{
//			if(m.totalBusinessDone>amount)
//			{
//				
//				bus.add(m);
//				if(map.containsKey(m.language))
//				{
//					map.get(m.language).add(m);
//				}
//				else {
//				Set<Movie> bus2=new TreeSet<Movie>();
//				bus2.add(m);
//				map.put(m.language,bus2);	
//				}
//			}
//			
//		}
//		
//		 for(Map.Entry m : map.entrySet()){    
//			    System.out.println(m.getKey()+" "+m.getValue());    
//			   }  
//		//Collections.sort(bus,new Sortamout());
//		return map;
//		
//		
//	}
	
	public String toString() {
		return this.movieId+" "+this.movieName+" "+this.language+" "+this.releaseDate+" "+this.rating+" "+this.totalBusinessDone;
	}
	
	

}
	public class MovieTransaction{
		
		
		public static void main(String[] args) throws Exception {
			String fileName="C:\\Users\\ADMIN\\eclipse-workspace\\Ass09_Yashika_Goyal\\movies.txt";
			File f=new File(fileName);
			List<Movie> m= Movie.populateMovies(f);
			System.out.println("After populate");
			for(Movie s:m) {
				System.out.println(s.toString());
			}
			Movie.addToDb(m);
			List<String> cast=new ArrayList<String>();
			cast.add("rani");
			cast.add("shahrukh");
			Movie ex=new Movie(7,"kash","bolly","hindi","2020-03-10",cast,2.5,5.0);
			Movie.addMovie(ex, m);
			System.out.println("After addmovie");
			for(Movie s:m) {
				System.out.println(s.toString());
			}
			String file="C:\\Users\\ADMIN\\eclipse-workspace\\Ass09_Yashika_Goyal\\lockedin.txt";
			Movie.serializeMovies(m, file);
			List<Movie> mov=Movie.deserializeMovie(file);
			System.out.println("After deserialize");
			for(Movie s:mov) {
				System.out.println(s.toString());
			}
			List<Movie> yr=Movie.getMoviesRealeasedInYear(2021, mov);
			System.out.println("After getReleasedyr");
			for(Movie s:yr) {
				System.out.println(s.toString());
			}
			Movie.updateRatings(ex, 3.7, mov);
			System.out.println("After update rating");
			for(Movie s:mov) {
				System.out.println(s.toString());
			}
			Movie.updateBusiness(ex, 6.5, mov);
			System.out.println("After updatebusiness");
			for(Movie s:mov) {
				System.out.println(s.toString());
			}
//			Map<String,Set<Movie>> map1=Movie.businessDone(5.0, mov);
//			System.out.println("after businessDone");
//			for(Map.Entry m1 : map1.entrySet()){    
//			    System.out.println(m1.getKey()+" "+m1.getValue());    
//			   }  
		
			
		}
		
	}
