import com.sun.net.httpserver.SimpleFileServer;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.zip.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Path site = Path.of("site").toAbsolutePath();
        if (!Files.isRegularFile(site.resolve("index.html"))) {
            throw new IllegalStateException("Запустите из корня проекта: нет site/index.html");
        }
        if (args.length > 0 && args[0].equals("--pack")) {
            Path temp = Files.createTempFile(Path.of("."), "shop-", ".zip");
            try {
                try (var zip = new ZipOutputStream(Files.newOutputStream(temp)); var paths = Files.walk(site)) {
                    for (Path path : paths.toList()) {
                        if (Files.isSymbolicLink(path)) throw new IllegalArgumentException("Ссылка в site: " + path);
                        if (Files.isDirectory(path)) continue;
                        if (!Files.isRegularFile(path)) throw new IllegalArgumentException("Необычный файл: " + path);
                        zip.putNextEntry(new ZipEntry(site.relativize(path).toString().replace('\\', '/')));
                        Files.copy(path, zip);
                        zip.closeEntry();
                    }
                }
                Files.move(temp, Path.of("shop.zip"), StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temp);
            }
            System.out.println("Готово: shop.zip (index.html в корне)");
            return;
        }
        String host = System.getenv().getOrDefault("HOST", "127.0.0.1");
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
        var server = SimpleFileServer.createFileServer(new InetSocketAddress(host, port), site, SimpleFileServer.OutputLevel.INFO);
        server.start();
        System.out.println("Откройте http://localhost:" + port + " — Ctrl+C для остановки");
    }
}
