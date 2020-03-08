package com.commwin.lucene.searchIndex;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;

public class App01_SearchIndex {

    @Test
    public void test1() throws Exception {

        //1. 创建分析器对象（Analyzer），用于分词
        Analyzer analyzer = new StandardAnalyzer();

        //2. 创建查询对象（Query）
        //MultiFieldQueryParser: 用于在多个Field里面同时搜索数据（只能用在字符串类型的Field）
        /**
         * 参数一：多个字段名称
         * 参数二：指定分词器
         */
        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"bookName","bookDesc"},analyzer);
        //参数：需要搜索的内容
        Query query = queryParser.parse("java书籍");
        //3. 创建索引库目录对象（Directory），指定索引库的位置
        File file = new File("D:\\lucene\\bookindex01");
        Directory directory = FSDirectory.open(file);
        //4. 创建索引数据读取对象（IndexReader），把索引数据读取到内存中
        IndexReader indexReader = DirectoryReader.open(directory);
        //5. 创建索引搜索对象（IndexSearcher）
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        //6. 执行搜索，返回搜索的结果集TopDocs（查询到的文档数，文档信息列表）
        // search: 在索引库中进行检索
        /**
         * 参数一：检索的Query对象
         * 参数二：检索几条(pageSize)
         */
        //TopDocs: 封装词条的数据（词条与文档关系，词条在什么文档，什么位置，什么频率）
        TopDocs topDocs = indexSearcher.search(query, 10);
        //7. 获取文档信息列表，处理结果
        //scoreDocs: 获取词条与文档的关系信息
        ScoreDoc[] scoreDocs =  topDocs.scoreDocs;
        //击中总数
        System.out.println("查询结果总数："+topDocs.totalHits);
        for(ScoreDoc scoreDoc:scoreDocs){
            //7.1 获取文档
            //scoreDoc.doc: 获取该词条所在文档的ID号
            Document document =  indexSearcher.doc(scoreDoc.doc);
            System.out.println("====================");
            System.out.println(document.get("bookId"));
            System.out.println(document.get("bookName"));
            System.out.println(document.get("bookPrice"));
            System.out.println(document.get("bookPic"));
            System.out.println(document.get("bookDesc"));

        }
        //8. 释放资源
        indexReader.close();
    }
}
