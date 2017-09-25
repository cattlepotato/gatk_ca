package org.broadinstitute.hellbender.tools.spark.sv.utils;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.annotations.VisibleForTesting;

import java.util.Map;

/**
 * A <Kmer,count> pair.
 */
@DefaultSerializer(SVKmerAndCount.Serializer.class)
@VisibleForTesting
public final class SVKmerAndCount extends SVKmerLong implements Map.Entry<SVKmer, Integer> {
    private int count;

    public SVKmerAndCount(final SVKmerLong kmer) { this(kmer,1); }

    public SVKmerAndCount(final SVKmerLong kmer, final int count) {
        super(kmer);
        this.count = count;
    }

    private SVKmerAndCount(final Kryo kryo, final Input input ) {
        super(kryo, input);
        count = input.readInt();
    }

    protected void serialize( final Kryo kryo, final Output output ) {
        super.serialize(kryo, output);
        output.writeInt(count);
    }

    @Override
    public SVKmer getKey() { return new SVKmerLong(this); }
    @Override
    public Integer getValue() { return count; }
    @Override
    public Integer setValue( final Integer count ) {
        final Integer result = this.count;
        this.count = count;
        return result;
    }
    public int grabCount() { return count; }
    public void bumpCount() { count += 1; }
    public void bumpCount( final int extra ) { count += extra; }

    public static final class Serializer extends com.esotericsoftware.kryo.Serializer<SVKmerAndCount> {
        @Override
        public void write( final Kryo kryo, final Output output, final SVKmerAndCount kmerAndInterval) {
            kmerAndInterval.serialize(kryo, output);
        }

        @Override
        public SVKmerAndCount read(final Kryo kryo, final Input input,
                                   final Class<SVKmerAndCount> klass ) {
            return new SVKmerAndCount(kryo, input);
        }
    }
}
