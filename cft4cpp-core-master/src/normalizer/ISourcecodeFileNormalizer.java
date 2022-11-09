package normalizer;

/**
 * Normalize source code file
 *
 * @author DucAnh
 */
public interface ISourcecodeFileNormalizer extends ISourceCodeNormalizer {
    @Override
    default boolean shouldWriteToFile() {
        return true;
    }
}
