package uk.ac.manchester.ac.uk.server.repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Simon Jupp
 * @date 14/12/2011
 * Functional Genomics Group EMBL-EBI
 */
public class FileBasedKUPKBConfig implements KUPKBConfig{



    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String getRemoteRepositoryConnectionURL() {
        try {
            BufferedReader inp = new BufferedReader(new InputStreamReader(FileBasedKUPKBConfig.class.getClassLoader().getResourceAsStream("resources/config.txt")));
            String nextLine = null;
            for (;;) {
                String line = nextLine;
                nextLine = null;
                if (line == null) {
                    line = inp.readLine();
                }
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("repository-path:")) {

                    StringBuilder buff = new StringBuilder(line.substring(16, line.length()));
                    return buff.toString();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    @Override
    public String getLocalRepositoryConnectionPath() {
        return null;
    }

    @Override
    public String getConfigFilePath() {
        return null;
    }

    @Override
    public String getRepositoryID() {
        try {
            BufferedReader inp = new BufferedReader(new InputStreamReader(FileBasedKUPKBConfig.class.getClassLoader().getResourceAsStream("resources/config.txt")));
            String nextLine = null;
            for (;;) {
                String line = nextLine;
                nextLine = null;
                if (line == null) {
                    line = inp.readLine();
                }
                if (line == null) {
                    break;
                }
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.startsWith("repository-id:")) {

                    StringBuilder buff = new StringBuilder(line.substring(14, line.length()));
                    return buff.toString();

                }
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
