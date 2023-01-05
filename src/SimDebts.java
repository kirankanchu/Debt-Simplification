import java.util.*;
import static java.lang.Math.min;


public class SimDebts {
    private static final long OFFSET = 1000000000L;
    private static Set<Long> visitedEdges;

    public static void main(String[] args) {
        createGraphForDebts();
    }


    private static void createGraphForDebts() {
        //  List of all people in the group
        //String[] person = { "Alice", "Bob", "Charlie", "David", "Ema", "Fred", "Gabe"};
        ReadCSV readCSV = new ReadCSV();
        String[] person = readCSV.readParticipantNamesFromCSV();

        int n = person.length;
        //  Creating a graph with n vertices
        Dinics participant = new Dinics(n, person);
        //  Adding edges to the graph
        participant = addAllTransactions(participant);
        System.out.println("Simplifying Debts...");


        //  Map to keep track of visited edges
        visitedEdges = new HashSet<>();
        Integer edgePosition;

        while((edgePosition = getNonVisitedEdge(participant.getEdges())) != null) {

            boolean solved=false;
            //  Set source and sink in the flow graph
            Dinics.Edge firstEdge = participant.getEdges().get(edgePosition);
            participant.setSource(firstEdge.from);
            //this.(firstEdge.from)=firstEdge.from;
            participant.setSink(firstEdge.to);
            //  Initialize the residual graph to be same as the given graph
            List<Dinics.Edge>[] residualGraph = participant.getGraph();
            List<Dinics.Edge> newEdges = new ArrayList<>();


            for(int j=0; j< residualGraph.length;j++) {
                List<Dinics.Edge> allEdges= residualGraph[j];

                for(int i=0;i< allEdges.size();i++){
                    NetworkFlowSolverBase.Edge edge = allEdges.get(i);

                    long remainingFlow;

                    if (edge.flow < 0)
                    {
                        remainingFlow=edge.capacity;
                    }
                    else
                    {
                        remainingFlow=edge.capacity-edge.flow;
                    }
                    if(remainingFlow > 0) {
                        newEdges.add(new Dinics.Edge(edge.from, edge.to, remainingFlow));
                    }
                }
            }

            //  Get the maximum flow between the source and sink
            long maxFlow = participant.getMaxFlow();

            //  Mark the edge from source to sink as visited
            int source = participant.getSource();
            int sink = participant.getSink();

            visitedEdges.add(source*OFFSET + sink);

            //  Create a new graph
            participant = new Dinics(n, person);
            //  Add edges having remaining capacity
            participant.addEdges(newEdges);
            //  Add an edge from source to sink in the new graph with obtained maximum flow as it's weight
            participant.addEdge(source, sink, maxFlow);
        }
        //  Print the edges in the graph
        participant.printEdges();
        System.out.println();
    }

    private static Dinics addAllTransactions(Dinics participant) {
        //  Transactions made by Bob
        ArrayList<ArrayList<Double>> transac = new ArrayList<>();
        ReadCSV re = new ReadCSV();
        re.readTransactionsFromCSV(transac);

        for(int i=0;i<transac.size();i++){
            for(int j=0;j<transac.get(i).size();j++){
                for(int k=j+1;k<transac.get(i).size();k++){
                    Double first = transac.get(i).get(j);
                    Double second = transac.get(i).get(k);

                    if(first == 0.0 || second == 0.0){
                        continue;
                    }
                    if(first < 0.0 && second > 0.0){
                        participant.addEdge(j, k,  (long) (first*(-1)));
                        transac.get(i).set(k, second - first*(-1));
                        //System.out.println("Edge from "+j+" "+k+" of "+first*(-1) + " long type "+(long) (first*(-1)));
                    }else if(first > 0.0 && second < 0.0){
                        participant.addEdge(k, j,  (long)(second*(-1)));
                        transac.get(i).set(j, first - second*(-1));
                        //System.out.println("Edge from "+k+" "+j+" of "+second*(-1) + " long type "+(long) (second*(-1)));
                    }
                }

            }
        }

        return participant;
    }


    private static Integer getNonVisitedEdge(List<Dinics.Edge> edges) {
        Integer edgePosition = null;
        int currentEdge = 0;

        for(int i=0;i<edges.size();i++){
            NetworkFlowSolverBase.Edge edge =edges.get(i);
            if(!visitedEdges.contains( edge.from * OFFSET + edge.to)){
                edgePosition = currentEdge;
            }
            currentEdge=currentEdge+1;
        }
        return edgePosition;
    }



}