package cli;

import java.io.File;

class OptionFlagAndOutFile {
    final boolean optionFlag;
    final File outFile;

    OptionFlagAndOutFile(boolean optionFlag, File outFile) {
        this.optionFlag = optionFlag;
        this.outFile = outFile;
    }
}
