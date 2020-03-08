package com.commwin.lucene.createIndex;

import com.commwin.dao.BookDao;
import com.commwin.dao.impl.BookDaoImpl;
import com.commwin.domain.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App01_CreateIndex {

    private static final String INDEX_PATH = "D:\\lucene\\bookindex03";

    //检索索引数据
    private void search(Query query) throws Exception {
        //打印查询语法
        System.out.println("查询的Query语法为：" + query);

        //1、创建分词器Analyzer
        //Analyzer analyzer = new StandardAnalyzer();
        //使用ik分词器
        Analyzer analyzer = new IKAnalyzer();

        //2、创建查询对象Query

        //3、创建存放索引目录Directory，指定索引存放路径
        Directory directory = FSDirectory.open(new File(INDEX_PATH));
        //4、创建索引读对象IndexReader
        IndexReader indexReader = DirectoryReader.open(directory);
        //5、创建索引搜索对象IndexSearcher，执行搜索，返回结果
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        /**
         * 参数1：查询对象
         * 参数2：查询前n个文档
         * 返回结果：得分文档（包含文档数组，总的命中数）
         */
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("符合本次查询的总命中文档数为：" + topDocs.totalHits);

        //6、处理搜索结果
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("文档在Lucene中的id为：" + scoreDoc.doc + "；文档分值为：" + scoreDoc.score);
            //根据lucene中的文档id查询到文档
            Document document = indexSearcher.doc(scoreDoc.doc);

            System.out.println("文档id为：" + document.get("bookId"));
            System.out.println("名称为：" + document.get("bookName"));
            System.out.println("价格为：" + document.get("bookPrice"));
            System.out.println("图片为：" + document.get("bookPic"));
            System.out.println("描述为：" + document.get("bookDesc"));
            System.out.println("---------------------------------------");
        }
        //7、释放资源
        indexReader.close();
    }

    @Test
    public void test1() throws Exception {
        //1. 采集数据，封装文档对象的List集合（Document）
        //1.1 调用Dao，获取业务数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> books = bookDao.findAllBooks();

        //1.2 把Book对象转换为Document对象
        List<Document> documentList = new ArrayList<>();
        for(Book book:books){
            //1.2.1 创建Document对象
            Document document = new Document();

            //1.2.2 把Book对象的数据转到Document对象中
            //添加Field对象
            /**
             * TextField: 封装文本类型数据
             *    参数一：定义Field的名称
             *    参数二：Filed存放的内容
             *    参数三：是否持久化该Field的内容到Lucene  ， YES：持久化 NO：不持久化
             */

            /**
             * StringField: 不支持分词，支持索引及存储。适合订单编号，身份证号码
             * FloatField/DoubleField/LongFiled: 专门针对数值类型存储
             * StoredField：只需要存储，不需要分词与索引
             * TextField: 支持分词，支持索引，控制是否存储
             */

            document.add(new StringField("bookId",book.getId()+"", Field.Store.YES));
            document.add(new TextField("bookName",book.getBookname(), Field.Store.YES));
            document.add(new FloatField("bookPrice",book.getPrice(), Field.Store.YES));
            document.add(new StoredField("bookPic",book.getPic()));
            document.add(new TextField("bookDesc",book.getBookdesc(), Field.Store.NO));

            //1.2.3 把封装好数据的Document对象存入List集合
            documentList.add(document);
        }

        //2. 创建分析器对象（Analyzer），用于分词
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //3. 创建索引库配置对象（IndexWriterConfig）配置索引库
        IndexWriterConfig indexWriterConfig =
                new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);

        //4. 创建索引库的目录对象（Directory），指定索引库的存储位置
        File file = new File("D:\\lucene\\bookindex04");
        Directory directory = FSDirectory.open(file);

        //5. 创建索引库操作对象（IndexWriter），操作索引库(***)
        /**
         * 参数一：索引存储位置
         * 参数二：存储前分词配置（指定分词器）
         */
        IndexWriter indexWriter =
                new IndexWriter(directory,indexWriterConfig);

        //6. 使用IndexWriter对象，把文档对象写入索引库
        for(Document document:documentList){
            indexWriter.addDocument(document);// 写出索引
        }

        //7. 释放资源
        indexWriter.close();

    }

/*
    1.建立索引库的配置对象（IndexWriterConfig），配置索引库
	1-1.建立分析器对象（Analyzer），用于分词
2.建立索引库的目录对象（Directory），指定索引库的位置
3.建立索引库的操作对象（IndexWriter），操作索引库
	3-1.建立文档对象（Document）
            3-2.建立条件对象（Term）
            3-3.使用IndexWtier，执行更新
	3-4.释放资源
*/

    @Test
    public void updateIndex() throws Exception {
        //1、创建分词器analyzer
        Analyzer analyzer = new IKAnalyzer();
        //2、创建文档索引配置对象IndexWriterConfig
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        //3、创建存放索引目录Directory，指定索引存放路径
        File file = new File("D:\\lucene\\bookindex04");
        Directory directory = FSDirectory.open(file);
        //4、创建索引编写器IndexWriter
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        //5、创建文档Document
        Document document = new Document();
        document.add(new StringField("id", "123", Field.Store.YES));
        document.add(new TextField("name", "spring and struts and springmvc and mybatis", Field.Store.YES));

        //6、创建条件对象Term
        Term term = new Term("name", "mybatis");

        //7、根据词条更新；如果存在则更新，不存在则新增
        indexWriter.updateDocument(term, document);

        //8、释放资源
        indexWriter.close();
    }



    @Test
    public void termQuery() throws Exception {
        TermQuery termQuery = new TermQuery(new Term("bookName", "java"));
        search(termQuery);
    }

    @Test
    public void numericRangeQuery() throws Exception {
        /**
         * 参数1：域名
         * 参数2：数值范围的下限
         * 参数3：数值范围的下限
         * 参数4：是否包含数值范围的下限（端点）
         * 参数5：是否包含数值范围的上限（端点）
         */
        NumericRangeQuery<Double> query = NumericRangeQuery.newDoubleRange("bookPrice", 80d, 100d, false, false);
        search(query);
    }

}
