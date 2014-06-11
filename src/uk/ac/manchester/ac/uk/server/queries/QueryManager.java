package uk.ac.manchester.ac.uk.server.queries;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;/*
 * Copyright (C) 2010, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Simon Jupp<br>
 * Date: Jun 21, 2011<br>
 * The University of Manchester<br>
 * Bio-Health Informatics Group<br>
 */
public class QueryManager {


    public static String getQuery(String queryId) {

        String[] queries = new String[0];
        try {
            queries = collectQueries("uk/ac/manchester/ac/uk/server/queries/queries.sparql");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        for (int i = 0; i < queries.length; i++) {
			final String name = queries[i].substring(0, queries[i].indexOf(":"));

            if (name.equals(queryId)) {
                return queries[i].substring(name.length() + 2).trim();
            }
        }

        return null;
    }


    private static String[] collectQueries(String queryFile) throws Exception {
		List<String> queries = new ArrayList<String>();
		BufferedReader inp = new BufferedReader(new InputStreamReader(QueryManager.class.getClassLoader().getResourceAsStream(queryFile)));
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
			if (line.startsWith("#")) {
				continue;
			}
			if (line.startsWith("^[") && line.endsWith("]")) {
				StringBuffer buff = new StringBuffer(line.substring(2, line.length() - 1));
				buff.append(": ");

				for(;;) {
					line = inp.readLine();
					if (line == null) {
						break;
					}
					line = line.trim();
					if (line.length() == 0) {
						continue;
					}
					if (line.startsWith("#")) {
						continue;
					}
					if (line.startsWith("^[")) {
						nextLine = line;
						break;
					}
					buff.append(line);
					buff.append(System.getProperty("line.separator"));
				}

				queries.add(buff.toString());
			}
		}

		String[] result = new String[queries.size()];
		for (int i = 0; i < queries.size(); i++) {
			result[i] = queries.get(i);
		}
		return result;
	}


}
