package normalizer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PrivateToPublicNormalizerTest {

    private PrivateToPublicNormalizer normalizer;

    @Before
    public void setUp() throws Exception {
        normalizer = new PrivateToPublicNormalizer();
    }

    @Test
    public void testConflicName() {
        String origin = "class SV (private: int privateeee, int y; public: )",
                output = "class SV (public : int privateeee, int y; public: )";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testExtraSpace() {
        String origin = "class SV (private       : int x, int y; public: )",
                output = "class SV (public        : int x, int y; public: )";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testNewLine() {
        String origin = "class SV (private\r\n: int x, int y; public: )",
                output = "class SV (public \r\n: int x, int y; public: )";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }

    @Test
    public void testSimple() {
        String origin = "class SV (private: int x, int y; public: )",
                output = "class SV (public : int x, int y; public: )";
        normalizer.setOriginalSourcecode(origin);
        normalizer.normalize();
        Assert.assertEquals(output, normalizer.getNormalizedSourcecode());
    }
}
