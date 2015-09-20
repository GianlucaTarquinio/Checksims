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

package net.lldp.checksims.testutil;

import net.lldp.checksims.submission.ConcreteSubmission;
import net.lldp.checksims.submission.Submission;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Utilities to assist in testing submissions
 */
public class SubmissionUtils {
    private SubmissionUtils() {}

    /**
     * Check two collections of submissions for equality
     *
     * @param toCheck Actual
     * @param checkAgainst Expected
     */
    public static void checkSubmissionCollections(Collection<Submission> toCheck, Collection<Submission> checkAgainst) {
        assertNotNull(toCheck);
        assertNotNull(checkAgainst);
        assertEquals(checkAgainst.size(), toCheck.size());
        checkAgainst.stream().forEach((submission) -> assertTrue(toCheck.contains(submission)));
    }

    /**
     * Check two collections of files for equality
     *
     * @param toCheck Actual
     * @param checkAgainst Expected
     */
    public static void checkFileCollections(Collection<File> toCheck, Collection<File> checkAgainst) {
        assertNotNull(toCheck);
        assertNotNull(checkAgainst);
        assertEquals(toCheck.size(), checkAgainst.size());

        // We want absolute comparisons
        // So map to absolute file
        Collection<File> absoluteToCheck = toCheck.stream().map(File::getAbsoluteFile).collect(Collectors.toList());
        Collection<File> absoluteCheckAgainst = checkAgainst.stream().map(File::getAbsoluteFile).collect(Collectors.toList());

        absoluteCheckAgainst.stream().forEach((file) -> assertTrue(absoluteToCheck.contains(file)));
    }

    /**
     * Build a Submission with given string as content
     *
     * @param name Name of submission
     * @param content Content of submission
     * @param type Token type to use when building
     * @return Submission with given content
     */
    public static Submission submissionFromString(String name, String content) {
        return new ConcreteSubmission(name, content);
    }

    /**
     * Create a Set from a group of elements
     *
     * @param elements Elements to create set from
     * @param <T> Type of elements, and thus returned set
     * @return Set of input elements, following set invariants - all duplicate inputs will be removed
     */
    @SafeVarargs
    public static <T> Set<T> setFromElements(T... elements) {
        Set<T> toReturn = new HashSet<>();

        toReturn.addAll(Arrays.asList(elements));

        return toReturn;
    }
}
