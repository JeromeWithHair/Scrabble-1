import edu.princeton.cs.algs4.Components;
import edu.princeton.cs.algs4.Graph;

/**
 * Baseline Gerrymanderer that divides the electorate into vertical stripes (when gerrymandering for the purple party)
 * or horizontal stripes (when gerrymandering for the yellow party).
 */
public class OurGerrymanderer implements Gerrymanderer {

    //Connected Components Variables
    private boolean[] marked;   // marked[v] = has vertex v been marked?
    private int[] id;           // id[v] = id of connected component containing v
    private int[] size;         // size[id] = number of vertices in given component
    private int countCC;
    //



    int random;
    private static boolean[] pointArray;
    private int[] first;
    private int[] next;
    private int count;
    private boolean[] articulation;

    /**
     * Computes the connected components of the undirected graph {@code G}.
     *
     * @param G the undirected graph
     */

    public Components(Graph G) {
        marked = new boolean[G.V()];
        id = new int[G.V()];
        size = new int[G.V()];
        for (int v = 0; v < G.V(); v++) {
            if (!marked[v]) {
                dfs(G, v);
                countCC++;
            }
        }
    }

    /**
     * Returns true if vertices {@code v} and {@code w} are in the same
     * connected component.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         connected component; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     */
    public boolean connected(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return id(v) == id(w);
    }

    /**
     * Returns true if vertices {@code v} and {@code w} are in the same
     * connected component.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @return {@code true} if vertices {@code v} and {@code w} are in the same
     *         connected component; {@code false} otherwise
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     * @throws IllegalArgumentException unless {@code 0 <= w < V}
     * @deprecated Replaced by {@link #connected(int, int)}.
     */
    @Deprecated
    public boolean areConnected(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        return id(v) == id(w);
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }
    /**
     * Returns the component id of the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the component id of the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int id(int v) {
        validateVertex(v);
        return id[v];
    }

    /**
     * Returns the number of connected components in the graph {@code G}.
     *
     * @return the number of connected components in the graph {@code G}
     */
    public int count() {
        return countCC;
    }


    /**
     * Returns the number of vertices in the connected component containing vertex {@code v}.
     *
     * @param  v the vertex
     * @return the number of vertices in the connected component containing vertex {@code v}
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public int size(int v) {
        validateVertex(v);
        return size[id[v]];
    }


    // depth-first search for a Graph
    private void dfs(Graph G, int v) {
        marked[v] = true;
        id[v] = countCC;
        size[countCC]++;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }


    private void depthFirstSearch(Graph G, int u, int v) {
        articulation = new boolean[G.V()];
        int children = 0;
        next[v] = count++;
        first[v] = next[v];
        for (int w : G.adj(v)) {
            if (next[w] == -1) {
                children++;
                depthFirstSearch(G, v, w);
                first[v] = Math.min(first[v], first[w]);
                if (first[w] >= next[v] && u != v)
                    articulation[v] = true;
            }
            else if (w != u)
                first[v] = Math.min(first[v], next[w]);
        }
        if (u == v && children > 1)
            articulation[v] = true;
    }


    public boolean isArticulation(int v) {
        return articulation[v];
    }


    public int chooseAdjacent(int i, int d, int x) { //send position i, # of districts d, and which district we're on (x)
        int[] choose = new int[4];
        choose[0] = i + 1;
        choose[1] = i - 1;
        choose[2] = i + d;
        choose[3] = i - d;

        random = choose[(int) (Math.random() * 5)];

        if (isArticulation(random) && id[random] == x) {
            chooseAdjacent(i, d, x);
        }
            return random;
    }

    @Override
    public int[][] gerrymander(Electorate e, boolean party) {

        int d = e.getNumberOfDistricts();
        int[][] result = new int[d][d];
        int [][] old = new int[d][d];
        int i = 0;
        int n = d*d;
        int efficacy;
        int efficacyKeeper[] = new int [d];

        Graph G = new Graph(d*d);
        //Components cc = new Components(G);


        while(i < (n)) {
            if(i + 1 < (n)) {
                G.addEdge(i, i + 1);
            }
            if (i + d < (n)) {
                G.addEdge(i, i + d);
            }
            i++;
        }

        //Biconnected(G);

        i=0; //i-th position in the electorate

        for (int tic = 0; tic<10; tic++) {

            int y = 0;
            for (int x = 0; x < d; x++) { //for each district of d people

                Components cc = new Components(G);
                int partyCount=0;

                while (y < d) { // to fill each of d slots in district

                    i = chooseAdjacent(i, d, x;//this should check if adj point is an articulation point

                    if (party) {
                        result[x][y] = i;
                        partyCount++;
                    }
                    y++;

                }
                efficacy = partyCount / d;
                efficacyKeeper[x] = efficacy;
            }
        }

        return result;
    }

}