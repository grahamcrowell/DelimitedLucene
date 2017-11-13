import java.nio.file.Paths

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.store.FSDirectory

val IndexStoreDir = Paths.get("/Users/gcrowell/Lucene/csv")
val analyzer = new StandardAnalyzer()
val writerConfig = new IndexWriterConfig(analyzer)
writerConfig.setOpenMode(OpenMode.CREATE)
writerConfig.setRAMBufferSizeMB(500)
val directory = FSDirectory.open(IndexStoreDir)

var doc = new Document()
var count = 0
