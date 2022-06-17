import java.io.*;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Lucene {

     public static void indexDocument(final String filedir, final String indexdir) throws IOException {

         long start = System.currentTimeMillis();
         File[] files = new File(filedir).listFiles(obj -> obj.isFile() && obj.getName().endsWith(".csv"));
         Directory indexDir = FSDirectory.open(Paths.get(indexdir));
         IndexWriterConfig luceneConfig = new IndexWriterConfig(new StandardAnalyzer());
         IndexWriter writer = new IndexWriter(indexDir, luceneConfig);
         ArrayList<String[]> invalidRecord = new ArrayList<String[]>();
         String[] nextRecord;
         FileReader filereader;
         Integer subreddit_num = 0;
         Integer count_in_this_subreddit = 0;
         Integer total = 0;
         Integer invalid_count = 0;
         Document doc;

         for (File file : files) {
             subreddit_num++;
             System.out.println(file.getAbsolutePath());
             count_in_this_subreddit = 0;
             filereader = new FileReader(file);
             CSVReader csvReader = new CSVReader(filereader, ',', '"', '\0');

             nextRecord = csvReader.readNext();
             while ((nextRecord = csvReader.readNext()) != null) {
                 if(nextRecord.length == 8) {
                     doc = new Document();
                     doc.add(new StringField("type", nextRecord[1], Field.Store.YES));
                     doc.add(new StringField("score", nextRecord[2], Field.Store.YES));
                     doc.add(new StringField("id", nextRecord[3], Field.Store.YES));
                     doc.add(new TextField("title", nextRecord[4], Field.Store.NO));
                     doc.add(new TextField("text", nextRecord[5], Field.Store.NO));
                     doc.add(new StringField("subreddit", nextRecord[6], Field.Store.NO));
                     doc.add(new StringField("file_path", file.getAbsolutePath(), Field.Store.YES));
                     //doc.add(new StringField("timestamp", nextRecord[7], Field.Store.YES));
                     writer.addDocument(doc);
                     System.out.println("Running:\tsubreddit#: " + subreddit_num + "\t|\tcount_in_this_subreddit: " + (++count_in_this_subreddit) +
                             "\t|\tid:" + nextRecord[3] + "\t|\t" + file.getAbsolutePath() + "\t|\ttotal: " + (++total) + "\t|\tinvalid_record_count: " + invalid_count);
                 }
                 else {
                     invalidRecord.add(nextRecord);
                     invalid_count++;
                 }
             }
             csvReader.close();
             filereader.close();
         }
         System.out.println("\nIndex time: " + (System.currentTimeMillis()-start)/1000 + " seconds");
         System.out.println("Total records indexed: " + total);
         System.out.println("Invalid records count: " + invalid_count + "\n");
         for(String[] s : invalidRecord) {
             System.out.println("Invalid record #"+ (invalidRecord.indexOf(s) + 1) + ": "
                     + Arrays.toString(s) + "\tLength: " + s.length + "\n\n");
         }
         writer.close();
     }

     public static void querySearch(final String querystr, final String indexdir, final Integer hitsPerPage) throws IOException, ParseException {

         String [] fields = {"title", "text"};
         Map<String, Float> boosts = new HashMap<>();
         boosts.put(fields[0], 0.65f);
         boosts.put(fields[1], 0.35f);
         MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer(), boosts);
         Query q = parser.parse(querystr);

         long start = System.currentTimeMillis();
         IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexdir)));
         IndexSearcher searcher = new IndexSearcher(reader);
         TopDocs docs = searcher.search(q, hitsPerPage);
         if(docs.totalHits.value == 0) {
             System.out.println("No record found");
         }
         else {
             System.out.println("Found " + docs.totalHits + "! \nDisplaying top " + hitsPerPage + " results:\n");
             Integer count = 0;
             for(ScoreDoc scoreDoc : docs.scoreDocs) {
                 Document d = searcher.doc(scoreDoc.doc);
                 File file = new File(d.get("file_path"));
                 FileReader filereader;
                 filereader = new FileReader(file);
                 CSVReader csvReader = new CSVReader(filereader, ',', '"', '\0');
                 String[] nextRecord = csvReader.readNext();
                 while ((nextRecord = csvReader.readNext()) != null) {
                     //System.out.println(nextRecord[3] +"\t" + d.get("id") + d.get("file_path"));
                     if(nextRecord.length != 8)
                        continue;
                     if(d.get("id").equals(nextRecord[3])) {
                         System.out.println("Result#: " + (++count) + "\nScore: " + scoreDoc.score
                                 + "\t|\tReddit Score: " + d.get("score") + "\t|\tSubreddit: "
                                 + nextRecord[6] + "\t|\tID: "+ d.get("id") + "\nType: "
                                 + d.get("type") + "\nTitle: " + nextRecord[4]
                                 + "\nText: " + nextRecord[5] + "\n\n\n");
                         break;
                     }
                 }
                 csvReader.close();
                 filereader.close();
             }
             reader.close();
             System.out.println("\nSearch time: " + (System.currentTimeMillis()-start) + " milliseconds");
         }
     }

    public static void main(String[] args) throws IOException, ParseException {
         String filedir = args.length == 0 ? "/Users/admin/cs172_data" : args[0] ;
         String indexdir = args.length < 2 ? "/Users/admin/Index" : args[1];
         String query = args.length < 3 ? "computer science" : args[2];
         Integer hitsPerPage = args.length < 4 ? 20 : Integer.parseInt(args[3]);;
         indexDocument(filedir, indexdir);
         querySearch(query, indexdir, hitsPerPage);
    }

}