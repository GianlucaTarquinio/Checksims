/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright (c) 2014-2015 Nicholas DeMarinis, Matthew Heon, and Dolan Murvihill
 */

package net.lldp.checksims.util.threading;

import net.lldp.checksims.algorithm.AlgorithmResults;
import net.lldp.checksims.algorithm.InternalAlgorithmError;
import net.lldp.checksims.algorithm.SimilarityDetector;
import net.lldp.checksims.parse.Percentable;
import net.lldp.checksims.parse.token.TokenTypeMismatchException;
import net.lldp.checksims.submission.Submission;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic unit of thread execution for similarity detection.
 *
 * Takes two Submissions, applies an algorithm to them, returns results.
 */
public class SimilarityDetectionWorker<T extends Percentable> implements Callable<AlgorithmResults> {
    private final SimilarityDetector<T> algorithm;
    private final Pair<Submission, Submission> submissions;

    private static Logger logs = LoggerFactory.getLogger(SimilarityDetectionWorker.class);

    /**
     * Construct a Callable to perform pairwise similarity detection for one pair of assignments.
     *
     * @param algorithm Algorithm to use
     * @param submissions Assignments to compare
     */
    public SimilarityDetectionWorker(SimilarityDetector<T> algorithm, Pair<Submission, Submission> submissions) {
        checkNotNull(algorithm);
        checkNotNull(submissions);
        checkNotNull(submissions.getLeft());
        checkNotNull(submissions.getRight());

        this.algorithm = algorithm;
        this.submissions = submissions;
    }

    /**
     * Perform pairwise similarity detection on assignments given when constructed.
     *
     * We don't throw exceptions here, checked or unchecked. The reason for this is our desire to "fail-fast" on
     * algorithm errors --- instead of waiting for all comparisons to complete, we should immediately exit and inform
     * the user of the failure.
     *
     * After future changes to the Similarity Matrix, it might be desirable to make this configurable behavior, as we'll
     * be able to tolerate missing entires in the matrix at that point.
     * TODO investigate this later
     *
     * @return Results of pairwise similarity detection
     * @throws InternalAlgorithmError 
     * @throws TokenTypeMismatchException 
     */
    @Override
    public AlgorithmResults call() throws TokenTypeMismatchException, InternalAlgorithmError {
        logs.debug("Running " + algorithm.getName() + " on submissions " + submissions.getLeft().getName() +
                "(" + submissions.getLeft().getContentAsString().length() + " bytes) and " + submissions.getRight().getName() + " (" +
                submissions.getRight().getContentAsString().length() + " bytes)");
        
        T left = algorithm.getPercentableCalculator().fromSubmission(submissions.getLeft());
        T right = algorithm.getPercentableCalculator().fromSubmission(submissions.getRight());
        
        return algorithm.detectSimilarity(submissions, left, right);
    }

    @Override
    public String toString() {
        return "Similarity detection worker for submissions \"" + submissions.getLeft().getName() + "\" and \""
                + submissions.getRight().getName() + "\"";
    }
}
