import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.function.Consumer;

public class GeneratorHelpers {
    /**
     * Gets or sets the name of the attribute to use when generating a {@link Graph} with a weight on each {@link Edge}.
     *
     * @apiNote This value defaults to weight.
     */
    public static String WeightAttributeName = "weight";

    /**
     * Gets or sets the name of the attribute to use when calculating distance between {@link Node}s in a {@link Graph}.
     *
     * @apiNote This value defaults to distance.
     */
    public static String DistanceAttributeName = "distance";

    /**
     * Generates a graph with the {@link RandomGenerator} algorithm.
     *
     * @param graph       Represents the {@link Graph} on which to apply random generation.
     * @param eventCount  Sets the amount of even to generate.
     * @param avgDegree   Sets the average degree of the {@link Graph}.
     * @param allowRemove Sets whether to allow removing some existing edges or not at each event generation.
     * @param directed    Sets whether the generated edges are directed or not.
     * @param addWeight   Sets whether to add a random weight or not to the different edges.
     * @return Returns the {@link Generator} that was used to generate the {@link Graph}.
     */
    public static Generator generateGraph(
            Graph graph, int eventCount, double avgDegree, boolean allowRemove, boolean directed, boolean addWeight) {
        Generator generator = addWeight
                ? new RandomGenerator(avgDegree, allowRemove, directed, null, WeightAttributeName)
                : new RandomGenerator(avgDegree, allowRemove, directed);
        generator.addSink(graph);
        generator.begin();
        for (int i = 0; i < eventCount; i++) {
            generator.nextEvents();
        }
        generator.end();

        if (addWeight) {
            applyToEdges(graph, e -> { // needed because random generation uses numbers between 0 and 1.
                double weight = (double) e.getAttribute(WeightAttributeName);
                e.setAttribute(WeightAttributeName, (int)(weight * 10));
            });
        }

        return generator;
    }

    /**
     * Sets the distance attribute for each {@link Node} of the {@link Graph} to -1.
     *
     * @param graph Represents the {@link Graph} that contains the {@link Node} we want to reset the distance for.
     * @apiNote -1 is used because you can't use dijkstra algorithm with negative weight.
     */
    public static void resetDijkstraDistance(Graph graph) {
        applyToNodes(graph, n -> n.setAttribute(DistanceAttributeName, -1));
    }

    /**
     * Apply a function to every {@link Edge} of the given {@link Graph}.
     *
     * @param graph    Represents the {@link Graph} which will have its nodes modified.
     * @param function Function to be used for each {@link Edge} in the {@link Graph}.
     */
    public static void applyToEdges(Graph graph, Consumer<? super Edge> function) {
        graph.edges().forEach(function);
    }

    /**
     * Apply a function to every {@link Node} of the given {@link Graph}.
     *
     * @param graph    Represents the {@link Graph} which will have its nodes modified.
     * @param function Function to be used for each {@link Node} in the {@link Graph}.
     */
    public static void applyToNodes(Graph graph, Consumer<? super Node> function) {
        graph.nodes().forEach(function);
    }
}
