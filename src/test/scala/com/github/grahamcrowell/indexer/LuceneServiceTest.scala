package com.github.grahamcrowell.indexer

import java.nio.file.Paths

import better.files.File
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.{DirectoryReader, IndexableField}
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.store.FSDirectory
import org.scalatest.{BeforeAndAfter, FunSpec}

import scala.collection.JavaConverters._

class LuceneServiceTest extends FunSpec with BeforeAndAfter {

  it("should index a single file: WFF_m1f") {
    val tenantRoot = TenantRoot(esldata / "WFF_m1f")
    val indexDataDirectory = File("/Users/gcrowell/Lucene/csv")
    val luceneService: LuceneServiceTrait = LuceneService(indexDataDirectory)
    val sampleFolder: DatedDataFolderTrait = tenantRoot.datedFolders.next()
    val delimitedDataFile: DelimitedDataFileTrait = sampleFolder.delimitedDataFiles.next()
    println(tenantRoot.file.pathAsString)
    luceneService.writeToDoc(delimitedDataFile)
  }

  it("should index duplicate files: WFF_duplicate_files") {
    // initialize a Lucene index
    val indexDataDirectory = File("/Users/gcrowell/Lucene/csv4")
    //    val luceneService: LuceneServiceTrait  = LuceneService(indexDataDirectory)
    // set source data directory
    val tenantRoot = TenantRoot(esldata / "WFF_duplicate_files")
    println(tenantRoot.file.pathAsString)
    val list = tenantRoot.dataFiles.toList
    val luceneService: LuceneServiceTrait = LuceneService(indexDataDirectory)
    println(list)
    list.foreach((delimitedDataFile: DelimitedDataFileTrait)=>luceneService.writeToDoc(delimitedDataFile))
  }

  it("can reuse index WFF_cross_folder_subject") {
    // initialize a Lucene index
    val indexDataDirectory = File("/Users/gcrowell/Lucene/WFF_cross_folder_subject")
    //    val luceneService: LuceneServiceTrait  = LuceneService(indexDataDirectory)
    // set source data directory
    val tenantRoot = TenantRoot(esldata / "WFF_cross_folder_subject")
    println(tenantRoot.file.pathAsString)
    val list = tenantRoot.dataFiles.toList
    val luceneService: LuceneServiceTrait = LuceneService(indexDataDirectory)
    println(list)
    list.foreach((delimitedDataFile: DelimitedDataFileTrait)=>luceneService.writeToDoc(delimitedDataFile))
  }

  it("should search an index") {
    /**
      * Sample file: /Users/gcrowell/workspace/esldata/WFF_m1f/20170625/Absence.csv
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
    val analyzer = new StandardAnalyzer()
    val mqp = new MultiFieldQueryParser(fieldsToSearch, analyzer)
    val query = mqp.parse(keyword)

    val hits = searcher.search(query, 500)
    val scoreDoc = hits.scoreDocs
    println(s"scoreDoc.size = ${scoreDoc.size}")
    scoreDoc.foreach(docs => {
      val doc = searcher.doc(docs.doc)
      val fields = doc.getFields.asScala
      println("*** Document Found: ")
      fields.foreach((field: IndexableField) => println(s"${field.name()}: ${field.stringValue()}"))

      //      fieldsToSearch.foreach((field: String) =>
      //        println(s"***** ${field}: ${doc.get(field)}")
      //      )
    })
    println("*** Results Found: " + hits.totalHits)
    println("*** Max Score Found: " + hits.getMaxScore)
  }

  it("should search across many indices") {
    val keyword = "Unpaid Sick Leave Scheduled"


    val IndexStoreDir = Paths.get("/Users/gcrowell/Lucene/csv3")
    var directoryReader = DirectoryReader.open(FSDirectory.open(IndexStoreDir))

    val searcher = new IndexSearcher(directoryReader)
    val fieldsToSearch = Array("AbsenceID", "EventDate", "EmployeeID", "AbsenceReason0")
    val analyzer = new StandardAnalyzer()
    val mqp = new MultiFieldQueryParser(fieldsToSearch, analyzer)
    val query = mqp.parse(keyword)

    val hits = searcher.search(query, 500)
    val scoreDoc = hits.scoreDocs
    println(s"scoreDoc.size = ${scoreDoc.size}")
    scoreDoc.foreach(docs => {
      val doc = searcher.doc(docs.doc)
      val fields = doc.getFields.asScala
      println("*** Document Found: ")
      fields.foreach((field: IndexableField) => println(s"${field.name()}: ${field.stringValue()}"))

      //      fieldsToSearch.foreach((field: String) =>
      //        println(s"***** ${field}: ${doc.get(field)}")
      //      )
    })
    println("*** Results Found: " + hits.totalHits)
    println("*** Max Score Found: " + hits.getMaxScore)
  }
}
