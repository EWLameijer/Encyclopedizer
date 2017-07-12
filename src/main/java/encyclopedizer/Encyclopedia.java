package encyclopedizer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * Created by Eric-Wubbo on 09-07-17.
 */
public class Encyclopedia {
    public Encyclopedia() {
        try {
            System.out.println("Started");
            Path pwd = Paths.get("").toAbsolutePath();
            System.out.println("path=" + pwd);
            final Path path = Paths.get("testency.txt");
            final Charset charset = StandardCharsets.UTF_8;
            List<String> lines = Files.readAllLines(path, charset);
            System.out.println("Lines: " + lines);
        } catch (IOException e) {
            System.out.println("Excepted");
        }

    }

}
