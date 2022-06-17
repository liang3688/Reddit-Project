package com.example.demo;

import com.opencsv.CSVReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController
//@RequestMapping("/api")
//@CrossOrigin("*")
@Controller
public class ArticleController {

//	@RequestMapping("/articles")
	 @GetMapping("/articles")
	 public String searchDocuments(@RequestParam(required = false, defaultValue = "") String querystr, Model model) throws IOException, ParseException {

		 if (querystr.isEmpty()){
			 model.addAttribute("msg", "Please enter a query");
			 return "index";
		 }
		 List<Article> matches = new ArrayList<>();
		 String [] fields = {"title", "text"};
		 Map<String, Float> boosts = new HashMap<>();
		 boosts.put(fields[0], 0.65f);
		 boosts.put(fields[1], 0.35f);
		 MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer(), boosts);
		 Query q = parser.parse(querystr);

		 long start = System.currentTimeMillis();
		 IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("/Users/admin/Index")));
		 IndexSearcher searcher = new IndexSearcher(reader);
		 TopDocs docs = searcher.search(q, 20);
		 if(docs.totalHits.value == 0) {
			 model.addAttribute("msg", "No record found");
			 return "index";
		 }
		 else {
			 //System.out.println("Found " + docs.totalHits + "! \nDisplaying top " + hitsPerPage + " results:\n");
			 Integer count = 0;
			 for(ScoreDoc scoreDoc : docs.scoreDocs) {
				 Document d = searcher.doc(scoreDoc.doc);
				 File file = new File(d.get("file_path"));
				 FileReader filereader;
				 filereader = new FileReader(file);
				 CSVReader csvReader = new CSVReader(filereader, ',', '"', '\0');
				 String[] nextRecord = csvReader.readNext();
				 while ((nextRecord = csvReader.readNext()) != null) {
					 if(nextRecord.length != 8)
						 continue;
					 if(d.get("id").equals(nextRecord[3])) {
						 matches.add(new Article(d.get("type"), Integer.parseInt(d.get("score")), scoreDoc.score, d.get("id"), nextRecord[4], nextRecord[5], nextRecord[6]));
//						 System.out.println("Result#: " + (++count) + "\nScore: " + scoreDoc.score
//								 + "\t|\tReddit Score: " + d.get("score") + "\t|\tSubreddit: "
//								 + nextRecord[6] + "\t|\tID: "+ d.get("id") + "\nType: "
//								 + d.get("type") + "\nTitle: " + nextRecord[4]
//								 + "\nText: " + nextRecord[5] + "\n\n\n");
						 break;
					 }
				 }
				 csvReader.close();
				 filereader.close();
			 }
			 reader.close();
			 model.addAttribute("articles", matches);
			 model.addAttribute("msg", "\nSearch time: " + (System.currentTimeMillis()-start) + " milliseconds");
		 }
		return "index";
	}

}
