package edu.wpi.checksims.algorithm.output;

import edu.wpi.checksims.submission.Submission;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Prints all similarity matrix entries over a certain threshold
 */
public class SimilarityMatrixThresholdPrinter implements SimilarityMatrixPrinter {
    private float threshold;
    private static final float DEFAULT_THRESHOLD = 0.6f;

    public SimilarityMatrixThresholdPrinter(float threshold) {
        this.threshold = threshold;
    }

    public static SimilarityMatrixThresholdPrinter getInstance() {
        return new SimilarityMatrixThresholdPrinter(DEFAULT_THRESHOLD);
    }

    class SimilarityEntry implements Comparable<SimilarityEntry> {
        public final Submission submissionA;
        public final Submission submissionB;
        public final float matchPercentA;
        public final float matchPercentB;

        SimilarityEntry(Submission submissionA, Submission submissionB, float matchPercentA, float matchPercentB) {
            this.submissionA = submissionA;
            this.submissionB = submissionB;
            this.matchPercentA = matchPercentA;
            this.matchPercentB = matchPercentB;
        }

        @Override
        public boolean equals(Object other) {
            if(!(other instanceof SimilarityEntry)) {
                return false;
            }

            SimilarityEntry otherEntry = (SimilarityEntry)other;

            return otherEntry.matchPercentA == matchPercentA && otherEntry.matchPercentB == matchPercentB &&
                    otherEntry.submissionA.equals(submissionA) && otherEntry.submissionB.equals(submissionB);
        }

        @Override
        public int hashCode() {
            return (int)(1000 * matchPercentA + 100 * matchPercentB);
        }

        @Override
        public int compareTo(SimilarityEntry other) {
            return (int)(100 * (matchPercentA - other.matchPercentA));
        }
    }

    @Override
    public String getName() {
        return "threshold";
    }

    @Override
    public String printMatrix(SimilarityMatrix matrix) {
        StringBuilder b = new StringBuilder();
        float[][] similarityMatrix = matrix.getResults();
        int matrixSize = matrix.getSubmissions().size();
        List<Submission> submissionList = matrix.getSubmissions();
        DecimalFormat formatter = new DecimalFormat("###.00");
        List<SimilarityEntry> highestEntries = new LinkedList<>();

        for(int i = 0; i < matrixSize; i++) {
            for(int j = 0; j < matrixSize; j++) {
                if(i != j && similarityMatrix[i][j] >= threshold) {
                    Submission subI = submissionList.get(i);
                    Submission subJ = submissionList.get(j);
                    float iToJ = similarityMatrix[i][j];
                    float jToI = similarityMatrix[j][i];
                    SimilarityEntry toAddIJ = new SimilarityEntry(subI, subJ, iToJ, jToI);
                    SimilarityEntry toAddJI = new SimilarityEntry(subJ, subI, jToI, iToJ);
                    SimilarityEntry toAdd;

                    // Ensure no duplicates added
                    // Can't use a SetUniqueList as we want to check for the swapped entry
                    if(highestEntries.contains(toAddIJ) || highestEntries.contains(toAddJI)) {
                        continue;
                    }

                    if(iToJ > jToI) {
                        toAdd = toAddIJ;
                    } else {
                        toAdd = toAddJI;
                    }

                    highestEntries.add(toAdd);
                }
            }
        }

        // Sort low to high
        // Then reverse to get expected high to low ordering
        Collections.sort(highestEntries);
        Collections.reverse(highestEntries);

        for(SimilarityEntry e : highestEntries) {
            b.append("Found match of ");
            b.append(formatter.format(100 * e.matchPercentA));
            b.append("% (inverse match ");
            b.append(formatter.format(100 * e.matchPercentB));
            b.append("%) between submissions ");
            b.append(e.submissionA.getName());
            b.append(" and ");
            b.append(e.submissionB.getName());
            b.append("\n");
        }

        return b.toString();
    }

    @Override
    public String toString() {
        return "Similarity Matrix Threshold Printer, with threshold of " + threshold;
    }
}