
public class Main {


     public static void main(String[] args) {
         int port = 8080;
         if(args.length == 1){
             port = Integer.parseInt(args[0]);
         }
         new Main().start(port);
     }

     private void start(int port) {
        // new JettyLuncher().start(port);
     }

}
