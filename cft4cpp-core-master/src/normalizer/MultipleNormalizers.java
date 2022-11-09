package normalizer;

import java.util.ArrayList;
import java.util.List;

import testdatagen.structuregen.ChangedTokens;

/**
 * Perform multiple normalizations continuously
 *
 * @author DucAnh
 */
public class MultipleNormalizers extends AbstractNormalizer {

    private List<AbstractNormalizer> normalizers = new ArrayList<>();

    @Override
    public void normalize() {
        normalizeSourcecode = originalSourcecode;

        for (AbstractNormalizer normalize : normalizers) {
            normalize.setOriginalSourcecode(normalizeSourcecode);
            normalize.normalize();
            normalizeSourcecode = normalize.getNormalizedSourcecode();
        }
    }

    @Override
    public ChangedTokens getTokens() {
        ChangedTokens mappingVars = new ChangedTokens();
        for (AbstractNormalizer normalizer : normalizers)
            mappingVars.addAll(normalizer.getTokens());
        return mappingVars;
    }

    public List<AbstractNormalizer> getNormalizers() {
        return normalizers;
    }

    public void setNormalizers(List<AbstractNormalizer> normalizers) {
        this.normalizers = normalizers;
    }

    public void addNormalizer(AbstractNormalizer normalizer) {
        normalizers.add(normalizer);
    }

}
