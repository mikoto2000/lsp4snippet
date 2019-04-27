package jp.dip.oyasirazu.lsp4snippet;

import org.eclipse.lsp4j.launch.LSPLauncher;

public class App {

    public static void main(String[] args) {

        var server = new SnippetLanguageServer();
        var launcher = LSPLauncher.createServerLauncher(server, System.in, System.out);

        var client = launcher.getRemoteProxy();

        server.connect(client);
        launcher.startListening();
    }
}

