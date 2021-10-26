import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Airline {
  private String [] cityNames = null;
  private Digraph G = null;
  private static Scanner scan = null;
  private static final int INFINITY = Integer.MAX_VALUE;
  private static String fileName = null;
  private static int numEdges = 0;


  /**
  * Test client.
  */
  public static void main(String[] args) throws IOException {
    Airline airline = new Airline();
    scan = new Scanner(System.in);
    while(true){
      switch(airline.menu()){
        case 1:
          airline.readGraph();
          break;
        case 2:
          airline.printGraph();
          break;
        case 3:
          airline.shortestDistance();
          break;
        case 4:
          airline.shortestPrice();
          break;
        case 5:
          airline.shortestHops();
          break;
        case 6:
          airline.lowerPrice();
          break;
        case 7:
          airline.addRoute();
          break;
        case 8:
          airline.removeRoute();
          break;
        case 9:
          airline.save(fileName);
          scan.close();
          System.exit(0);
          break;
        default:
          System.out.println("Incorrect option.");
      }
    }
  }

  private int menu(){
    System.out.println("*********************************");
    System.out.println("Welcome to FifteenO'One Airlines!");
    System.out.println("1. Read data from a file.");
    System.out.println("2. Display all routes.");
    System.out.println("3. Compute shortest path based on distance.");
    System.out.println("4. Compute shortest path based on price.");
    System.out.println("5. Compute shortest path based on number of hops.");
    System.out.println("6. Compute all trips with a price lower than given amount.");
    System.out.println("7. Add a route.");
    System.out.println("8. Remove a route.");
    System.out.println("9. Exit.");
    System.out.println("*********************************");
    System.out.print("Please choose a menu option (1-9): ");

    int choice = Integer.parseInt(scan.nextLine());
    return choice;
  }

  private void readGraph() throws IOException {
    System.out.println("Please enter graph filename:");
    fileName = scan.nextLine();
    Scanner fileScan = new Scanner(new FileInputStream(fileName));
    //gets size of file so that the adjacency list is long enough
    try {
      Path file = Paths.get(fileName);
      numEdges = (int)(Files.lines(file).count()) - 10;
    } catch (Exception e) {
      System.out.println("Error retrieving size.");
    }
    int v = Integer.parseInt(fileScan.nextLine());
    G = new Digraph(v);

    cityNames = new String[v];
    for(int i=0; i<v; i++){
      cityNames[i] = fileScan.nextLine();
    }

    while(fileScan.hasNext()){
      int from = fileScan.nextInt();
      int to = fileScan.nextInt();
      int weight = fileScan.nextInt();
      double price = fileScan.nextDouble();
      G.addEdge(new WeightedDirectedEdge(from-1, to-1, weight, price));
      G.addEdge(new WeightedDirectedEdge(to-1, from-1, weight, price));
    }
    fileScan.close();
    System.out.println("Data imported successfully.");
    System.out.print("Please press ENTER to continue ...");
    scan.nextLine();
  }

  private void printGraph() {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      for (int i = 0; i < G.v; i++) {
        System.out.print(cityNames[i] + ": ");
        for (WeightedDirectedEdge e : G.adj(i)) {
          System.out.print(cityNames[e.to()] + "(" + e.weight() + "mi)->$" + e.price() + "   ");
        }
        System.out.println();
      }
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();

    }
  }

  private void shortestDistance()
  {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      for(int i=0; i<cityNames.length; i++){
        System.out.println(i+1 + ": " + cityNames[i]);
      }
      System.out.print("Please enter source city (1-" + cityNames.length + "): ");
      int source = Integer.parseInt(scan.nextLine());
      System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
      int destination = Integer.parseInt(scan.nextLine());
      source--;
      destination--;
      G.dijkstras(source, destination, true);
      if(!G.marked[destination]){
        System.out.println("There is no route from " + cityNames[source]
                            + " to " + cityNames[destination]);
      } else {
        Stack<Integer> path = new Stack<>();
        for (int x = destination; x != source; x = G.edgeTo[x]){
            path.push(x);
        }
        System.out.print("The shortest route from " + cityNames[source] +
                           " to " + cityNames[destination] + " has " +
                           G.distTo[destination] + " miles: ");

        int prevVertex = source;
        System.out.print("\nPath with edges: \n" + cityNames[source] + " ");
        while(!path.empty()){
          int v = path.pop();
          System.out.print(G.distTo[v] - G.distTo[prevVertex] + " "
                           + cityNames[v] + " ");
          prevVertex = v;
        }
        System.out.println();

      }
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    }
  }

  private void shortestPrice()
  {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      for(int i=0; i<cityNames.length; i++){
        System.out.println(i+1 + ": " + cityNames[i]);
      }
      System.out.print("Please enter source city (1-" + cityNames.length + "): ");
      int source = Integer.parseInt(scan.nextLine());
      System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
      int destination = Integer.parseInt(scan.nextLine());
      source--;
      destination--;
      G.dijkstras(source, destination, false);
      if(!G.marked[destination]){
        System.out.println("There is no route from " + cityNames[source]
                            + " to " + cityNames[destination]);
      } else {
        Stack<Integer> path = new Stack<>();
        for (int x = destination; x != source; x = G.edgeTo[x]){
            path.push(x);
        }
        System.out.print("The cheapest route from " + cityNames[source] +
                           " to " + cityNames[destination] + " costs $" +
                           (double)G.distTo[destination] + ": ");

        int prevVertex = source;
        System.out.print("\nPath with edges: \n" + cityNames[source] + " ");
        while(!path.empty()){
          int v = path.pop();
          System.out.print(G.distTo[v] - G.distTo[prevVertex] + " "
                           + cityNames[v] + " ");
          prevVertex = v;
        }
        System.out.println();

      }
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    }
  }

  private void shortestHops()
  {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      for(int i=0; i<cityNames.length; i++){
        System.out.println(i+1 + ": " + cityNames[i]);
      }
      System.out.print("Please enter source city (1-" + cityNames.length + "): ");
      int source = Integer.parseInt(scan.nextLine());
      System.out.print("Please enter destination city (1-" + cityNames.length + "): ");
      int destination = Integer.parseInt(scan.nextLine());
      source--;
      destination--;
      G.bfs(source);
      if(!G.marked[destination]){
        System.out.println("There is no route from " + cityNames[source]
                            + " to " + cityNames[destination]);
      } else {

        Stack<Integer> path = new Stack<>();
        for(int x = destination; x != source; x = G.edgeTo[x])
        {
          path.push(x);
        }
        path.push(source);
        System.out.print("The shortest route from " + cityNames[source] + " " + "to " + cityNames[destination] + " " + "has " + G.distTo[destination] + " " + "hop(s): \nPath: \n");
        while (!path.empty())
        {
          System.out.print(cityNames[path.pop()] + " ");
        }
        System.out.println();

      }
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    }
  }

  private void lowerPrice()
  {
    double p;

    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      System.out.println("Enter the maximum price: ");
      p = scan.nextDouble();
      scan.nextLine();
      for(int source = 0; source < G.v; source++)
      {
        for(int destination = 0; destination < G.v; destination++)
        {
          G.dijkstras(source, destination, false);

          if(!G.marked[destination])
          {

          }
          else
          {
            Stack<Integer> path = new Stack<>();

            if(G.distTo[destination] <= p && G.distTo[destination] != 0)
            {
              for (int x = destination; x != source; x = G.edgeTo[x]){
                  path.push(x);
              }
              System.out.print("Cost: $" + G.distTo[destination] + " Path: " + cityNames[source] + " ");
            }
            int prevVertex = source;
            while(!path.empty()){
              int v = path.pop();
              //System.out.print("Cost: $" + G.distTo[destination] + " Path: " + cityNames[source] + " ");
              System.out.print(G.distTo[v] - G.distTo[prevVertex] + " "
                               + cityNames[v] + " ");
              prevVertex = v;
            }
            if(G.distTo[destination] <= p && G.distTo[destination] != 0)
            {
              System.out.println();
            }
          }
        }
      }
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    }
  }

  private void addRoute()
  {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      int from = -1;
      int to = -1;
      int weight;
      double price;
      String cityOne;
      String cityTwo;

      System.out.println("Enter first city name: ");
      cityOne = scan.nextLine();

      System.out.println("Enter second city name: ");
      cityTwo = scan.nextLine();

      System.out.println("Enter the distance: ");
      weight = scan.nextInt();

      System.out.println("Enter the price: ");
      price = scan.nextDouble();
      scan.nextLine();

      for(int i = 0; i < cityNames.length; i++)
      {
        if(cityNames[i].equals(cityOne))
        {
          from = i;
        }

        if(cityNames[i].equals(cityTwo))
        {
          to = i;
        }
      }

      G.addEdge(new WeightedDirectedEdge(from, to, weight, price));
      G.addEdge(new WeightedDirectedEdge(to, from, weight, price));
      System.out.println("Route successfully added.");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    }
  }

  private void removeRoute()
  {
    if(G == null){
      System.out.println("Please import a graph first (option 1).");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    } else {
      int from = -1;
      int to = -1;
      String cityOne;
      String cityTwo;
      WeightedDirectedEdge edgeToRemove = null;
      WeightedDirectedEdge edgeToRemoveR = null;

      System.out.println("Enter first city name: ");
      cityOne = scan.nextLine();

      System.out.println("Enter second city name: ");
      cityTwo = scan.nextLine();

      for(int i = 0; i < cityNames.length; i++)
      {
        if(cityNames[i].equals(cityOne))
        {
          from = i;
        }

        if(cityNames[i].equals(cityTwo))
        {
          to = i;
        }
      }
      for (int i = 0; i < G.v; i++)
      {
        for (WeightedDirectedEdge e : G.adj(i)) {
          if(e.from() == from && e.to() == to)
          {
            edgeToRemove = e;
          }
          if(e.from() == to && e.to() == from)
          {
            edgeToRemoveR = e;
          }
        }
      }
      G.removeEdge(edgeToRemove);
      G.removeEdge(edgeToRemoveR);
      System.out.println("Route successfully removed.");
      System.out.print("Please press ENTER to continue ...");
      scan.nextLine();
    }
  }

  public void save(String fileName)
  {
    try
    {
      PrintWriter pw = new PrintWriter(fileName);
      pw.print(G.v);
      for(int i = 0; i < G.v; i++)
      {
        pw.print("\n" + cityNames[i]);
      }
      for(int i = 0; i < G.v; i++)
      {
        for(WeightedDirectedEdge e : G.adj(i))
        {
          if(e.from() < e.to())
          {
            pw.print("\n" + (e.from()+1) + " " + (e.to()+1) + " " + e.weight() + " " + e.price());
          }
        }
      }

      pw.close();
    }
    catch(IOException e)
    {
      System.out.println("Error saving to file.");
    }
  }


  private class Digraph {
    private final int v;
    private int e;
    private LinkedList<WeightedDirectedEdge>[] adj;
    private boolean[] marked;  // marked[v] = is there an s-v path
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path

    /**
    * Create an empty digraph with v vertices.
    */
    public Digraph(int v) {
      if (v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
      this.v = v;
      this.e = 0;
      @SuppressWarnings("unchecked")
      LinkedList<WeightedDirectedEdge>[] temp =
      (LinkedList<WeightedDirectedEdge>[]) new LinkedList[numEdges];
      adj = temp;
      for (int i = 0; i < numEdges; i++)
        adj[i] = new LinkedList<WeightedDirectedEdge>();
    }

    /**
    * Add the edge e to this digraph.
    */
    public void addEdge(WeightedDirectedEdge edge) {
      int from = edge.from();
      adj[from].add(edge);
      e++;
    }

    public void removeEdge(WeightedDirectedEdge edge){
      int from = edge.from();
      adj[from].remove(edge);
      e--;
    }

    /**
    * Return the edges leaving vertex v as an Iterable.
    * To iterate over the edges leaving vertex v, use foreach notation:
    * <tt>for (WeightedDirectedEdge e : graph.adj(v))</tt>.
    */
    public Iterable<WeightedDirectedEdge> adj(int v) {
      return adj[v];
    }

    //whichWeight determines whether to use distance or price, true = distance, false = price
    public void dijkstras(int source, int destination, boolean whichWeight) {
      marked = new boolean[this.v];
      distTo = new int[this.v];
      edgeTo = new int[this.v];


      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      int nMarked = 1;

      int current = source;
      while (nMarked < this.v) {
        for (WeightedDirectedEdge w : adj(current)) {
          if(whichWeight == true)
          {
            if (distTo[current]+w.weight() < distTo[w.to()]) {
              edgeTo[w.to()] = current;
              distTo[w.to()] = distTo[current] + w.weight;
            }
          }
          else
          {
            if (distTo[current]+w.price() < distTo[w.to()]) {
              edgeTo[w.to()] = current;
              distTo[w.to()] = distTo[current] + (int)w.price;
            }
          }
        }
        //Find the vertex with minimim path distance
        int min = INFINITY;
        current = -1;

        for(int i=0; i<distTo.length; i++){
          if(marked[i])
            continue;
          if(distTo[i] < min){
            min = distTo[i];
            current = i;
          }
        }

        if(current >= 0)
        {
          marked[current] = true;
          nMarked++;
        }
        else
        {
          break;
        }
      }
    }


    public void bfs(int source) {
      marked = new boolean[this.v];
      distTo = new int[this.e];
      edgeTo = new int[this.v];

      Queue<Integer> q = new LinkedList<Integer>();
      for (int i = 0; i < v; i++){
        distTo[i] = INFINITY;
        marked[i] = false;
      }
      distTo[source] = 0;
      marked[source] = true;
      q.add(source);

      while (!q.isEmpty()) {
        int v = q.remove();
        for (WeightedDirectedEdge w : adj(v)) {
          if (!marked[w.to()]) {
            edgeTo[w.to()] = v;
            distTo[w.to()] = distTo[v] + 1;
            marked[w.to()] = true;
            q.add(w.to());
          }
        }
      }
    }
  }

  private class WeightedDirectedEdge {
    private final int v;
    private final int w;
    private int weight;
    private double price;
    private WeightedDirectedEdge curr;
    /**
    * Create a directed edge from v to w with given weight.
    */
    public WeightedDirectedEdge(int v, int w, int weight, double price) {
      this.v = v;
      this.w = w;
      this.weight = weight;
      this.price = price;
    }

    public int from(){
      return v;
    }

    public int to(){
      return w;
    }

    public int weight(){
      return weight;
    }

    public double price(){
      return price;
    }
  }
}
