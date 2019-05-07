package jp.dip.oyasirazu.lsp4snippet;

import java.util.List;

import org.eclipse.lsp4j.launch.LSPLauncher;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class App {

    public static void main(String[] args) {
        var options = parseArgs(args);

        var server = new SnippetLanguageServer(options.getSnippetFilePaths());
        var launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);

        var client = launcher.getRemoteProxy();

        server.connect(client);
        launcher.startListening();
    }

    public static Options parseArgs(String[] args) {
        // オプションオブジェクト準備
        Options options = new Options();

        // パーサー準備
        CmdLineParser optionParser = new CmdLineParser(options);

        try {
            // オプションパース実行
            optionParser.parseArgument(args);
        } catch (CmdLineException e) {
            // Useage を表示
            System.out.println("Useage:\n"
                    + "  lsp4snippet [options] [ARGUMENTS...]\n"
                    + "\n"
                    + "Options:");
            optionParser.printUsage(System.out);
        }

        return options;
    }

    public static class Options {
        @Option(name="--snippet")
        private List<String> snippetFilePaths;

        public List<String> getSnippetFilePaths() {
            return snippetFilePaths;
        }

        public String toString() {
            return String.format("{ snippetFilePath: %s }", snippetFilePaths);
        }
    }
}

