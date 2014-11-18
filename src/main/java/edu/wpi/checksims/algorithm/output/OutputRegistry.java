package edu.wpi.checksims.algorithm.output;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.wpi.checksims.ChecksimException;
import edu.wpi.checksims.util.reflection.ReflectiveInstantiator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registry for valid output strategies
 */
public class OutputRegistry {
    private final List<SimilarityMatrixPrinter> outputStrategies;

    private static OutputRegistry instance;

    private OutputRegistry() {
        List<SimilarityMatrixPrinter> instances = ReflectiveInstantiator.reflectiveInstantiator("edu.wpi.checksims.algorithm.output", SimilarityMatrixPrinter.class);

        // Get a list without duplicates
        // If it's a different size, then duplicates existed, which is bad
        // Throw a RuntimeException for that!
        ImmutableList<String> noDups = ImmutableSet.copyOf(instances.stream().map(SimilarityMatrixPrinter::getName).collect(Collectors.toList())).asList();
        if(noDups.size() < instances.size()) {
            throw new RuntimeException("Some algorithm names were not globally unique!");
        }

        if(instances.isEmpty()) {
            throw new RuntimeException("No providers for SimilarityMatrixPrinter present!");
        }

        // The algorithms list should not be changed after initialization
        this.outputStrategies = ImmutableList.copyOf(instances);
    }

    public static OutputRegistry getInstance() {
        if(instance == null) {
            instance = new OutputRegistry();
        }

        return instance;
    }

    public SimilarityMatrixPrinter getOutputStrategy(String name) throws ChecksimException {
        String lowerName = name.toLowerCase();
        List<SimilarityMatrixPrinter> matchingStrategies = outputStrategies.stream().filter((strategy) -> strategy.getName().equals(lowerName)).collect(Collectors.toList());

        if(matchingStrategies.isEmpty()) {
            throw new ChecksimException("No output strategy found with name " + name);
        } else if(matchingStrategies.size() > 1) {
            throw new ChecksimException("Collision in output strategy names found!");
        }

        return matchingStrategies.get(0);
    }

    public List<String> getAllOutputStrategyNames() {
        return outputStrategies.stream().map(SimilarityMatrixPrinter::getName).collect(Collectors.toList());
    }

    public SimilarityMatrixPrinter getDefaultStrategy() {
        return outputStrategies.get(0);
    }

    public String getDefaultStrategyName() {
        return getDefaultStrategy().getName();
    }
}