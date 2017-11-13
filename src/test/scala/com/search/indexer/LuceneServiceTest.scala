package com.search.indexer

import java.nio.file.Paths

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{DirectoryReader, IndexWriter, IndexWriterConfig}
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory
import org.scalatest.{BeforeAndAfter, FunSpec}

class LuceneServiceTest extends FunSpec with BeforeAndAfter {

  var tenantRoot: TenantRootTrait = _
  var sampleFolder: DatedDataFolderTrait = _
  var delimitedDataFile: DelimitedDataFileTrait = _
  var luceneService = LuceneService

  before {
    tenantRoot = TenantRoot(esldata / tenant_code)
    println(tenantRoot.file.pathAsString)

    sampleFolder = tenantRoot.datedFolders.next()
    delimitedDataFile = sampleFolder.delimitedDataFiles.next()
  }

  it("should index a single file") {

    val IndexStoreDir = Paths.get("/Users/gcrowell/Lucene/csv")
    val analyzer = new StandardAnalyzer()
    val writerConfig = new IndexWriterConfig(analyzer)
    writerConfig.setOpenMode(OpenMode.CREATE)
    writerConfig.setRAMBufferSizeMB(500)
    val directory = FSDirectory.open(IndexStoreDir)
    val indexWriter = new IndexWriter(directory, writerConfig)
    luceneService.writeToDoc(delimitedDataFile, indexWriter)

  }

  it("should search an index") {
    /**
      *
      * AbsenceID,EventDate,EmployeeID,AbsenceReason0,AbsenceReason1,AbsenceHours,AbsenceDays,FunctionalCategory,PlanningCategory,CompensationCategory
      * Absence-1,2012-01-01,Employee-1850,Unpaid Sick Leave Scheduled,Reason1,24.0,3.0,Sick Leave,Scheduled,Unpaid
      * Absence-2,2012-01-01,Employee-1611,Unpaid Sick Leave Unscheduled,Reason1,8.0,1.0,Sick Leave,Unscheduled,Unpaid
      * Absence-3,2012-01-01,Employee-1542,Unpaid Sick Leave Scheduled,Reason1,8.0,1.0,Sick Leave,Scheduled,Unpaid
      * Absence-4,2012-01-01,Employee-1478,Unpaid Sick Leave Scheduled,Reason1,40.0,5.0,Sick Leave,Scheduled,Unpaid
      * Absence-5,2012-01-01,Employee-1421,Unpaid Sick Leave Scheduled,Reason1,40.0,5.0,Sick Leave,Scheduled,Unpaid
      */
    val keyword = "Unpaid Sick Leave Scheduled"


    val IndexStoreDir = Paths.get("/Users/gcrowell/Lucene/csv")
    var directoryReader = DirectoryReader.open(FSDirectory.open(IndexStoreDir))

    val searcher = new IndexSearcher(directoryReader)
    val fieldsToSearch = Array("AbsenceID", "EventDate", "EmployeeID", "AbsenceReason0")
    //      AbsenceID,EventDate,EmployeeID,AbsenceReason0,AbsenceReason1,AbsenceHours,AbsenceDays,FunctionalCategory,PlanningCategory,CompensationCategory
    val analyzer = new StandardAnalyzer()
    val mqp = new MultiFieldQueryParser(fieldsToSearch, analyzer)
    val query = mqp.parse(keyword)

    val hits = searcher.search(query, 500)
    val scoreDoc = hits.scoreDocs
    println(s"scoreDoc.size = ${scoreDoc.size}")
    scoreDoc.foreach(docs => {
      val doc = searcher.doc(docs.doc)
      println("*** Document Found: ")
      fieldsToSearch.foreach((field: String) =>
        println(s"***** ${field}: ${doc.get(field)}")
      )
      //      println("***** Agency_Category: ")
      //      println(doc.get("Agency_Category"))
    })
    println("*** Results Found: " + hits.totalHits)
    println("*** Max Score Found: " + hits.getMaxScore)

  }
}
