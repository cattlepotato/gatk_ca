package org.broadinstitute.hellbender.tools.copynumber;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import htsjdk.samtools.SAMReadGroupRecord;
import org.apache.commons.lang3.StringUtils;
import org.broadinstitute.barclay.argparser.Argument;
import org.broadinstitute.barclay.argparser.CommandLineProgramProperties;
import org.broadinstitute.barclay.help.DocumentedFeature;
import org.broadinstitute.hellbender.cmdline.StandardArgumentDefinitions;
import org.broadinstitute.hellbender.cmdline.programgroups.CopyNumberProgramGroup;
import org.broadinstitute.hellbender.engine.AlignmentContext;
import org.broadinstitute.hellbender.engine.FeatureContext;
import org.broadinstitute.hellbender.engine.LocusWalker;
import org.broadinstitute.hellbender.engine.ReferenceContext;
import org.broadinstitute.hellbender.engine.filters.MappingQualityReadFilter;
import org.broadinstitute.hellbender.engine.filters.ReadFilter;
import org.broadinstitute.hellbender.exceptions.UserException;
import org.broadinstitute.hellbender.tools.copynumber.allelic.alleliccount.AllelicCountCollector;
import org.broadinstitute.hellbender.utils.Nucleotide;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collects reference/alternate allele counts at sites.  The alt count is defined as the total count minus the ref count,
 * and the alt nucleotide is defined as the non-ref base with the highest count, with ties broken by the order of the
 * bases in {@link AllelicCountCollector#BASES}.
 *
 * <h3>Example</h3>
 *
 * <pre>
 * gatk-launch --javaOptions "-Xmx4g" CollectAllelicCounts \
 *   --input sample.bam \
 *   --reference ref_fasta.fa \
 *   --siteIntervals sites.interval_list \
 *   --output allelic_counts.tsv
 * </pre>
 *
 * <p>
 *     The --siteIntervals is a Picard-style interval list, e.g.:
 * </p>
 *
 * <pre>
 *     {@literal @}HD	VN:1.4	SO:unsorted
 *     {@literal @}SQ	SN:1	LN:16000	M5:8c0c38e352d8f3309eabe4845456f274
 *     {@literal @}SQ	SN:2	LN:16000	M5:5f8388fe3fb34aa38375ae6cf5e45b89
 *      1	10736	10736	+	normal
 *      1	11522	11522	+	normal
 *      1	12098	12098	+	normal
 *      1	12444	12444	+	normal
 *      1	13059	13059	+	normal
 *      1	14630	14630	+	normal
 *      1	15204	15204	+	normal
 *      2	14689	14689	+	normal
 *      2	14982	14982	+	normal
 *      2	15110	15110	+	normal
 *      2	15629	15629	+	normal
 * </pre>
 *
 * <p>
 *     The resulting table counts the reference versus alternate allele and indicates the alleles. For example:
 * </p>
 *
 * <pre>
 *     CONTIG	POSITION	REF_COUNT	ALT_COUNT	REF_NUCLEOTIDE	ALT_NUCLEOTIDE
 *     1	10736	0	0	G	A
 *     1	11522	7	4	G	C
 *     1	12098	8	6	G	A
 *     1	12444	0	18	T	A
 *     1	13059	0	8	C	G
 *     1	14630	9	8	T	A
 *     1	15204	4	4	C	G
 *     2	14689	6	9	T	A
 *     2	14982	6	5	G	A
 *     2	15110	6	0	G	A
 *     2	15629	5	3	T	C
 * </pre>
 *
 * @author Samuel Lee &lt;slee@broadinstitute.org&gt;
 */
@CommandLineProgramProperties(
        summary = "Collects ref/alt counts at sites.",
        oneLineSummary = "Collects ref/alt counts at sites.",
        programGroup = CopyNumberProgramGroup.class
)
@DocumentedFeature
public final class CollectAllelicCounts extends LocusWalker {

    private static final Logger logger = LogManager.getLogger(CollectAllelicCounts.class);

    @Argument(
            doc = "Output allelic-counts file.",
            fullName = StandardArgumentDefinitions.OUTPUT_LONG_NAME,
            shortName = StandardArgumentDefinitions.OUTPUT_SHORT_NAME
    )
    private File outputAllelicCountsFile;

    @Argument(
            doc = "Minimum base quality; base calls with lower quality will be filtered out of pileup.",
            fullName = "minimumBaseQuality",
            shortName = "minBQ",
            minValue = 0,
            optional = true
    )
    private int minimumBaseQuality = 20;

    private static final int DEFAULT_MINIMUM_MAPPING_QUALITY = 30;

    private AllelicCountCollector allelicCountCollector;

    @Override
    public boolean emitEmptyLoci() {return true;}

    @Override
    public boolean requiresReference() {return true;}

    @Override
    public boolean requiresIntervals() {return true;}

    @Override
    public void onTraversalStart() {
        final String sampleName = getSampleName();
        allelicCountCollector = new AllelicCountCollector(sampleName);
        logger.info("Collecting allelic counts...");
    }

    @Override
    public List<ReadFilter> getDefaultReadFilters() {
        final List<ReadFilter> initialReadFilters = super.getDefaultReadFilters();
        initialReadFilters.add(new MappingQualityReadFilter(DEFAULT_MINIMUM_MAPPING_QUALITY));

        return initialReadFilters;
    }

    @Override
    public Object onTraversalSuccess() {
        allelicCountCollector.getAllelicCounts().write(outputAllelicCountsFile);
        logger.info("Allelic counts written to " + outputAllelicCountsFile.toString());
        return("SUCCESS");
    }

    @Override
    public void apply(AlignmentContext alignmentContext, ReferenceContext referenceContext, FeatureContext featureContext) {
        final byte refAsByte = referenceContext.getBase();
        allelicCountCollector.collectAtLocus(Nucleotide.valueOf(refAsByte), alignmentContext.getBasePileup(), alignmentContext.getLocation(), minimumBaseQuality);
    }

    //TODO extract this code from GetSampleName
    private String getSampleName() {
        if ((getHeaderForReads() == null) || (getHeaderForReads().getReadGroups() == null)) {
            throw new UserException.BadInput("The input BAM has no header or no read groups.  Cannot determine a sample name.");
        }
        final List<String> sampleNames = getHeaderForReads().getReadGroups().stream().map(SAMReadGroupRecord::getSample).distinct().collect(Collectors.toList());
        if (sampleNames.size() > 1) {
            throw new UserException.BadInput(String.format("The input BAM has more than one unique sample name: %s", StringUtils.join(sampleNames, ", ")));
        }
        if (sampleNames.isEmpty()) {
            throw new UserException.BadInput("The input BAM has no sample names.");
        }
        return sampleNames.get(0);
    }
}
