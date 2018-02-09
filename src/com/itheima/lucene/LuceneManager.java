package com.itheima.lucene;
/*
 * 索引维护
 * 添加  入门程序
 * 删除
 * 修改
 * 查询   入门查询 精准查询
 * 
 * */

import java.io.File;
import java.io.IOException;

import javax.management.Query;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneManager {
	

	public IndexWriter getIndexWriter() throws Exception{
//		第一步：创建Java project，导包
//		第二步：创建一个indexwriter对象
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,analyzer);
		IndexWriter indexWriter = new IndexWriter(directory,config);
		return indexWriter;
	}
	//全删除
	@Test
	public void testAllDelete() throws Exception{
		IndexWriter indexWriter = getIndexWriter();
		indexWriter.deleteAll();
		indexWriter.close();
	}
	//有条件删除
	@Test
	public void testDelete() throws Exception{
		IndexWriter indexWriter = getIndexWriter();
		TermQuery query = new TermQuery(new Term("fileContent","spring"));
		indexWriter.deleteDocuments(query);;
		indexWriter.close();
	}
	
	//修改
	@Test
	public void testUpdate() throws Exception{
		IndexWriter indexWriter = getIndexWriter();
		TermQuery query = new TermQuery(new Term("fileContent","spring"));
		Document doc= new Document();
		doc.add(new TextField("fileN","测试文件名",Store.YES));
		doc.add(new TextField("fileC","测试文件内容",Store.YES));
		
		indexWriter.updateDocument(new Term("fileContent","spring"), doc,new IKAnalyzer());;
		indexWriter.close(); 
	}
	
	
	public IndexSearcher getIndexSearch() throws Exception{
//		第一步：创建一个Directory对象，也就是索 引库的位置
		Directory directory = FSDirectory.open(new File("D:\\temp\\index")); //磁盘上的库
//		第二步：创建一个indexReader对象，需要制定Directory对象（该对象用来和索引库沟通）
		IndexReader indexReader = DirectoryReader.open(directory);
//		第三步：创建一个indexsearcher对象，需要制定indexReader对象 （基于流的搜索）
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		return indexSearcher;
	}
	public void printResult(IndexSearcher indexSearcher,Query query) throws Exception{
		//第五步：执行查询
		TopDocs topDocs = indexSearcher.search(query, 2); 
//		第六步：返回查询结果，遍历结果并输出
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(ScoreDoc scoreDoc : scoreDocs){
			int doc = scoreDoc.doc;
			Document document = indexSearcher.doc(doc);
			String fileName = document.get("fileName");
			System.out.println(fileName);
			String fileContent = document.get("fileContent");
			System.out.println(fileContent);
			String fileSize = document.get("fileSize");
			System.out.println(fileSize);
			String filePath = document.get("filePath");
			System.out.println(filePath);
			System.out.println("-------------");
		}
	}
	// 查询所有
	@Test
	public void testMatchAllDocsQuery() throws Exception{
		IndexSearcher indexSearcher = getIndexSearch();
		Query query = new MatchAllDocsQuery();
		printResult(indexSearcher,query);
		indexSearcher.getIndexReader().close();
		
	}
		
//	根据数值范围查询  
	@Test
	public void testNumericRangeQuery() throws Exception{
		IndexSearcher indexSearcher = getIndexSearch();
		Query query = NumericRangeQuery.newLongRange("fileSize", 1L, 200L, true, true);
		printResult(indexSearcher,query);
		indexSearcher.getIndexReader().close();
		
	}
	
}









