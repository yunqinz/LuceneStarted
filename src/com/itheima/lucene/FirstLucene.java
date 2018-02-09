package com.itheima.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/*
 * Lucene入门
 * 创建索引，查询索引
 * */
public class FirstLucene {
	
	@Test
	public void testIndex() throws Exception{
//		第一步：创建Java project，导包
//		第二步：创建一个indexwriter对象
		Directory directory = FSDirectory.open(new File("D:\\temp\\index"));
		Analyzer analyzer = new IKAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST,analyzer);
		IndexWriter indexWriter = new IndexWriter(directory,config);
//		              1）指定索引库存放位置Directory对象
//		              2）指定一个分析器，对文档内容进行分析
//		第四步：创建field,将field添加到Document
		File f = new File("E:\\Lucene&solr\\searchsource");
		File[] listFiles = f.listFiles();
		for(File file:listFiles){
//			第三步：创建Document对象
			Document document = new Document();
			
			//文件名称
			String file_name = file.getName();//Y,Y,X
			Field fileNameField = new TextField("fileName",file_name,Store.YES);
	
			//文件大小
			long file_size = FileUtils.sizeOf(file);
			Field fileSizeField = new LongField("fileSize",file_size,Store.YES);
			
			//文件路径
			String file_path = file.getPath(); //N,N,Y
			Field filePathField = new StoredField("filePath",file_path);
			
			//文件内容
			String file_content = FileUtils.readFileToString(file);//Y,Y,X
			Field fileContentField = new TextField("fileContent",file_content,Store.YES);
			
			document.add(fileNameField);  
			document.add(fileSizeField);
			document.add(filePathField);
			document.add(fileContentField);
//			第四步：使用indexwriter对象将Document对象写入索引库，此过程进行索引创建。并将索引和Document对象写入索引库
			indexWriter.addDocument(document);
		}
//		第六步：关闭indexwriter对象
		indexWriter.close();
	}
	@Test
	public void testSearch() throws Exception{
//		第一步：创建一个Directory对象，也就是索 引库的位置
		Directory directory = FSDirectory.open(new File("D:\\temp\\index")); //磁盘上的库
//		第二步：创建一个indexReader对象，需要制定Directory对象（该对象用来和索引库沟通）
		IndexReader indexReader = DirectoryReader.open(directory);
//		第三步：创建一个indexsearcher对象，需要制定indexReader对象 （基于流的搜索）
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//		第四步：创建一个TermQuery对象，指定查询的域和查询的关键词
		TermQuery query = new TermQuery(new Term("fileContent","spring"));
//		第五步：执行查询
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
//		第七步：关闭IndexReader对象
		indexReader.close();
	}
	// 查看标准分析器的分词效果
	@Test
	public void testTokenStream() throws Exception {
		// 创建一个标准分析器对象
//		Analyzer analyzer = new StandardAnalyzer();
//		Analyzer analyzer = new CJKAnalyzer();
//		Analyzer analyzer = new SmartChineseAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		// 获得tokenStream对象
		// 第一个参数：域名，可以随便给一个
		// 第二个参数：要分析的文本内容
//		TokenStream tokenStream = analyzer.tokenStream("test",
//				"The Spring Framework provides a comprehensive programming and configuration model.");
		TokenStream tokenStream = analyzer.tokenStream("test",
				"高富帅可以用二维表结构来逻辑表达实现的数据");
		// 添加一个引用，可以获得每个关键词
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		// 添加一个偏移量的引用，记录了关键词的开始位置以及结束位置
		OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
		// 将指针调整到列表的头部
		tokenStream.reset();
		// 遍历关键词列表，通过incrementToken方法判断列表是否结束
		while (tokenStream.incrementToken()) {
			// 关键词的起始位置
			System.out.println("start->" + offsetAttribute.startOffset());
			// 取关键词
			System.out.println(charTermAttribute);
			// 结束位置
			System.out.println("end->" + offsetAttribute.endOffset());
		}
		tokenStream.close();
	}

	

}
