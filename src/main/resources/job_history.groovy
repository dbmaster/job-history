import com.branegy.service.connection.api.ConnectionService
import com.branegy.dbmaster.connection.ConnectionProvider

import java.util.*
import java.io.*
import java.sql.*
import groovy.sql.Sql


connectionSrv = dbm.getService(ConnectionService.class);

connectionInfo = connectionSrv.findByName(p_server);
connector = ConnectionProvider.getConnector(connectionInfo);

def connection = connector.getJdbcConnection(null);
qReader = new SQLReader("plugins/data-services-job-history/queries.sql");

def output = ""

Sql sql = new Sql(connection);

for (String dbName : p_database.split(",")) {

    logger.info("Handling database ${dbName}")

    connection.setCatalog(dbName);


       sql.eachRow(qReader.getQuery("job_history_query")) { row ->
              if (output.length()>0) output = output + ","

              output += """

{
"id":"${p_server}_${dbName}_${row.object_key}",
"title": "${row.service}",
"startdate": "${row.start_time}",
"enddate": "${row.end_time}",
"date_display":"ye",
"importance":"4",
"high_threshold":50,
"description":"${p_server} - ${dbName} - ${row.start_time} - ${row.end_time} " }
"""
           }

    logger.info("Handling database ${dbName} - done")

}


// TODO add try / finally
connection.close();

println """

[
{
"id":"js_history",
"title":"A little history of JavaScript",
"description":"Data Services ",
"focus_date":"2012-05-07 13:00:00",
"__timezone":"-05:00",

"initial_zoom":"5",

"events":[

${output}

],
"_legend": [
   {"title":"libs &amp; frameworks", "icon":"triangle_orange.png"},
   {"title":"engines", "icon":"square_gray.png"},
   {"title":"browsers", "icon":"triangle_yellow.png"},
   {"title":"JS evolution", "icon":"triangle_green.png"},
   {"title":"languages", "icon":"circle_green.png"},
   {"title":"standards", "icon":"square_blue.png"},
   {"title":"conferences", "icon":"circle_blue.png"},
   {"title":"milestones", "icon":"circle_purple.png"}
]

}
]


""".toString()


logger.info("Done")

public class SQLReader {
    private Map<String,String> queries = new HashMap<String,String>(10);

    public SQLReader(String filename) throws Exception {
        LineNumberReader lnr = new LineNumberReader(new FileReader(filename));
        String line = "";
        String queryName = "";
        String query = "";
        while ((line = lnr.readLine())!=null) {
            line = line.trim();
            if (line.startsWith("----")) {
                queryName = lnr.readLine();
                if (queryName!=null) {
                    queryName = queryName.trim();
                    query = "";
                }
            } else if (line.startsWith("--")) { // comment
                continue;
            } else {
                query = query + " " + line;
            }
            queries.put(queryName, query);
        }
    }

    public String getQuery(String queryName) throws Exception {
        // TODO if query doesn't exists - throw a message
    return queries.get(queryName);
    }
}
