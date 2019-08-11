package jp.dip.oyasirazu.lsp4snippet;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class App {

    public static void main(String[] args) throws IOException {
        Options options = parseArgs(args);

        List<String> snippets = findSnippets(options.getSnippetFilePaths());
        SnippetLanguageServer server = new SnippetLanguageServer(snippets);
        Launcher<LanguageClient> launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);

        LanguageClient client = launcher.getRemoteProxy();

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

    public static List<String> findSnippets(List<String> globs) throws IOException {
        List<String> snippets = new ArrayList<>();
        for (var candidateGlob : globs) {

            // ワイルドカードなどを含まない最長のパスを抽出
            var splittedCandidateGlob = candidateGlob.toString().split("[\\*?\\[]");

            // glob の特殊文字が無い場合はそれがそのままファイルパスになるので、
            // リストへ追加して次へ
            if (splittedCandidateGlob.length == 1) {
                snippets.add(candidateGlob);
                continue;
            }

            // 特殊文字が無い部分を取り出して Path 化
            var searchBaseString = splittedCandidateGlob[0];
            var searchBasePath = Paths.get(searchBaseString);

            // glob の特殊文字が入った場所から後ろの部分を抽出
            var glob = candidateGlob.substring(searchBaseString.length(), candidateGlob.length());

            // searchBasePath から glob で指定されたファイル群を抽出
            var pathMatcher = FileSystems.getDefault().getPathMatcher("glob:./" + glob);
            Files.walkFileTree(searchBasePath,
                    new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file,
                                BasicFileAttributes attr) throws IOException {
                            if (pathMatcher.matches(file)) {
                                snippets.add(file.toString().replace("\\", "/"));
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
        }

        return snippets;
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

    public static class GlobInfo {
        private Path basePath;
        private PathMatcher pathMatcher;
        
        public Path getBasePath() {
            return basePath;
        }
        
        public void setBasePath(Path basePath) {
            this.basePath = basePath;
        }
        
        public PathMatcher getPathMatcher() {
            return pathMatcher;
        }
        
        public void setPathMatcher(PathMatcher pathMatcher) {
            this.pathMatcher = pathMatcher;
        }
    }
}

