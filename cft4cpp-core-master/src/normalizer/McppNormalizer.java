package normalizer;

import java.io.File;

import config.Paths;
import parser.projectparser.ProjectParser;
import utils.Utils;
import utils.UtilsVu;

/**
 * Replace macro with its content <br/>
 * (http://mcpp.sourceforge.net/) <br/>
 * Mcpp options: <br/>
 * <p>
 * -I <directory> Add <directory> to the #include search list. <br/>
 * -Q Output diagnostics to "mcpp.err" (default:stderr). <br/>
 * <p>
 * Ex: <b>mcpp.exe D:\ava\data-test\tsdv\Sample_for_R1_2\Preprocessor.cpp -I
 * D:\ava\data-test\tsdv\Sample_for_R1_2 -Q </b>
 *
 * @author
 */
public class McppNormalizer extends AbstractSourcecodeFileNormalizer implements ISourcecodeFileNormalizer {
    /**
     * Absolute path of mcpp.exe
     */
    private String mcppPath;
    /**
     * Absolute path of mcpp.exe
     */
    private String environment;
    /**
     * the file needed to be remove macros
     */
    private String currentFile;
    /**
     * external folder that contains headers
     */
    private String externalIncludedFolder;

    public McppNormalizer() {
    }

    public McppNormalizer(String mcppPath, String environment, String currentFile, String externalIncludedFolder) {
        this.mcppPath = mcppPath;
        this.environment = environment;
        this.currentFile = currentFile;
        this.externalIncludedFolder = externalIncludedFolder;
    }

    public static void main(String[] args) throws Exception {
        new ProjectParser(new File(Paths.TSDV_R1_2), null);

        String mcppPath = "C:\\Users\\ducanhnguyen\\Desktop\\mcpp\\bin\\mcpp.exe";
        String mcppBinFolder = "C:\\Users\\ducanhnguyen\\Desktop\\mcpp\\bin\\";
        String currentFile = "D:\\ava\\data-test\\tsdv\\Sample_for_R1_2\\Preprocessor.cpp";
        String externalIncludedFolder = "D:\\ava\\data-test\\tsdv\\Sample_for_R1_2";

        McppNormalizer norm = new McppNormalizer();

        norm.setCurrentFile(currentFile);
        norm.setMcppBinFolder(mcppBinFolder);
        norm.setExternalIncludedFolder(externalIncludedFolder);
        norm.setMcppPath(mcppPath);

        norm.normalize();

        System.out.println(norm.getNormalizedSourcecode());
    }

    @Override
    public void normalize() {
        try {
            normalizeSourcecode = Utils.readFileContent(new File(currentFile));
            if (mcppPath != null && environment != null && currentFile != null && externalIncludedFolder != null)
                normalizeSourcecode = UtilsVu.runCommand(mcppPath, null, new File(environment), currentFile, "-I",
                        externalIncludedFolder, "-Q", "-P", "-z");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
    }

    public void setMcppBinFolder(String mcppBinFolder) {
        environment = mcppBinFolder;
    }

    public void setExternalIncludedFolder(String externalIncludedFolder) {
        this.externalIncludedFolder = externalIncludedFolder;
    }

    public void setMcppPath(String mcppPath) {
        this.mcppPath = mcppPath;
    }
}