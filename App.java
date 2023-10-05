import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import java.net.URL;
import java.awt.Desktop;
public class App{
    public static void main(String arg[]){
        Scanner in = new Scanner(System.in, "UTF-8");
        String url =URL(in);
        System.out.println(url);
       String JsonStr = getJson(url);
       AllPage allPage=Toclass(JsonStr);
       int selectedPoint = Menu(allPage, in);
        Desktop(allPage,selectedPoint);
    }
    public static String URL(Scanner in)
    {
        System.out.print("Enter the article name in En: ");
    String nameArticle = in.nextLine().toString();
        String URLname = "https://en.wikipedia.org/w/api.php?action=query&list=search&utf8=&format=json&srsearch=\"" + nameArticle + "\"";
        return URLname;
    }
    public static String getJson(String OldURL)
    {
        HttpsURLConnection con = null;
        try{
            URL u=new URL(OldURL);
            con = (HttpsURLConnection) u.openConnection();
            con.connect();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            String jsonStr = sb.toString();
            //System.out.println(jsonStr);      
            return jsonStr;       
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    public static AllPage Toclass(String jsonStr)
    {
        Gson g=new Gson();
        AllPage allPage=g.fromJson(jsonStr, AllPage.class);
        return allPage;
    }
    public static int Menu(AllPage allPage, Scanner in)
    {  
        for (int i = 0; i < allPage.query.search.size(); i++) {
            System.out.println("№ " + i);
            System.out.println("Name of article: " + allPage.query.search.get(i).title);
            //System.out.println("pageid: " + allPage.query.search.get(i).pageid);
        }
        int selectedPoint = -1; // Инициализируем значение выбранной статьи с недопустимым значением
        while (selectedPoint < 0 || selectedPoint >= allPage.query.search.size()) {
            System.out.print("Enter the number of the article you want to select: ");
            if (in.hasNextInt()) {
                selectedPoint = in.nextInt();
                if (selectedPoint < 0 || selectedPoint >= allPage.query.search.size()) {
                    System.out.println("Invalid article number. Please enter a valid number.");
                }
            } else {
                in.next(); // Очистка буфера ввода
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return selectedPoint;
    }
    public static void Desktop(AllPage allPage, int selectedPoint) 
    {
        try
        {
        for(int i=0;i < allPage.query.search.size(); i++)
        {
            if (i==selectedPoint)
            {
                int id=allPage.query.search.get(i).pageid;
                URI uri= new URI("https://en.wikipedia.org/w/index.php?curid=" +id);
                Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
                if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
                    try {
                        desktop.browse(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }catch (URISyntaxException ex) {
            System.out.print("URISyntaxException");
        }
    }
}
