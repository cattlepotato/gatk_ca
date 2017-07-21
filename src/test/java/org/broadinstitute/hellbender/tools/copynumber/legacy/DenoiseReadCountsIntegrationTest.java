package org.broadinstitute.hellbender.tools.copynumber.legacy;

import org.broadinstitute.hellbender.CommandLineProgramTest;
import org.broadinstitute.hellbender.cmdline.ExomeStandardArgumentDefinitions;
import org.broadinstitute.hellbender.cmdline.StandardArgumentDefinitions;
import org.broadinstitute.hellbender.tools.exome.TargetArgumentCollection;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

/**
 * Integration test for {@link DenoiseReadCounts}.
 *
 * @author Samuel Lee &lt;slee@broadinstitute.org&gt;
 */
public class DenoiseReadCountsIntegrationTest extends CommandLineProgramTest {
    private static final String TEST_SUB_DIR = publicTestDir + "org/broadinstitute/hellbender/tools/copynumber/allelic";
    private static final File NORMAL_READ_COUNT_FILE = new File("/home/slee/working/ipython/wes.tsv");

    @Test
    public void testWESWithNoGCPonWithoutAnnotations() {
        final String[] arguments = {
                "-" + StandardArgumentDefinitions.INPUT_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.tsv",
                "-" + ExomeStandardArgumentDefinitions.PON_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes.no-gc.pon",
                "-" + ExomeStandardArgumentDefinitions.PRE_TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.with-no-gc-pon_without-annot.ptn.tsv",
                "-" + ExomeStandardArgumentDefinitions.TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.with-no-gc-pon_without-annot.tn.tsv",
//                "-" + DenoiseReadCounts.NUMBER_OF_EIGENSAMPLES_SHORT_NAME, "10",
                "--" + StandardArgumentDefinitions.VERBOSITY_NAME, "INFO"
        };
        runCommandLine(arguments);
    }

    @Test
    public void testWESWithGCPonWithoutAnnotations() {
        final String[] arguments = {
                "-" + StandardArgumentDefinitions.INPUT_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.tsv",
                "-" + ExomeStandardArgumentDefinitions.PON_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes.gc.pon",
                "-" + ExomeStandardArgumentDefinitions.PRE_TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.with-gc-pon_without-annot.ptn.tsv",
                "-" + ExomeStandardArgumentDefinitions.TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.with-gc-pon_without-annot.tn.tsv",
//                "-" + DenoiseReadCounts.NUMBER_OF_EIGENSAMPLES_SHORT_NAME, "10",
                "--" + StandardArgumentDefinitions.VERBOSITY_NAME, "INFO"
        };
        runCommandLine(arguments);
    }

    @Test
    public void testWESWithoutPoNWithAnnotations() {
        final String[] arguments = {
                "-" + StandardArgumentDefinitions.INPUT_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.tsv",
                "-" + ExomeStandardArgumentDefinitions.PRE_TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.without-pon_with-annot.ptn.tsv",
                "-" + ExomeStandardArgumentDefinitions.TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.without-pon_with-annot.tn.tsv",
                "-" + TargetArgumentCollection.TARGET_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes.intervals.annot.tsv",
                "--" + StandardArgumentDefinitions.VERBOSITY_NAME, "INFO"
        };
        runCommandLine(arguments);
    }

    @Test
    public void testWESWithoutPoNWithoutAnnotations() {
        final String[] arguments = {
                "-" + StandardArgumentDefinitions.INPUT_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.tsv",
                "-" + ExomeStandardArgumentDefinitions.PRE_TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.without-pon_without-annot.ptn.tsv",
                "-" + ExomeStandardArgumentDefinitions.TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wes-pon-test/wes_case.without-pon_without-annot.tn.tsv",
                "--" + StandardArgumentDefinitions.VERBOSITY_NAME, "INFO"
        };
        runCommandLine(arguments);
    }

    @Test
    public void testWGS5M() {
        final String[] arguments = {
                "-" + StandardArgumentDefinitions.INPUT_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test-5M/wgs_4.tsv",
                "-" + ExomeStandardArgumentDefinitions.PON_FILE_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test-5M/wgs-5M.no-gc.pon",
                "-" + ExomeStandardArgumentDefinitions.PRE_TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test-5M/wgs_4.no-gc.ptn.tsv",
                "-" + ExomeStandardArgumentDefinitions.TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test-5M/wgs_4.no-gc.tn.tsv",
//                "-" + DenoiseReadCounts.NUMBER_OF_EIGENSAMPLES_SHORT_NAME, "10",
                "--" + StandardArgumentDefinitions.VERBOSITY_NAME, "INFO"
        };
        runCommandLine(arguments);
    }

    @Test
    public void testWGS() {
        final String[] arguments = {
                "-" + StandardArgumentDefinitions.INPUT_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test/wgs_4.tsv",
                "-" + ExomeStandardArgumentDefinitions.PON_FILE_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test/wgs.no-gc.pon",
                "-" + ExomeStandardArgumentDefinitions.PRE_TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test/wgs_4.no-gc.ptn.tsv",
                "-" + ExomeStandardArgumentDefinitions.TANGENT_NORMALIZED_COUNTS_FILE_SHORT_NAME, "/home/slee/working/ipython/wgs-pon-test/wgs_4.no-gc.tn.tsv",
//                "-" + DenoiseReadCounts.NUMBER_OF_EIGENSAMPLES_SHORT_NAME, "10",
                "--" + StandardArgumentDefinitions.VERBOSITY_NAME, "INFO"
        };
        runCommandLine(arguments);
    }
}