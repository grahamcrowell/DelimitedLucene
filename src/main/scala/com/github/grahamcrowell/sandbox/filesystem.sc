import java.nio.file.Path

import better.files._

import scala.collection.JavaConverters._

val root = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root")
var data_file = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root/20170625/Absence.csv")
data_file = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root/20170625/Absence.csv")

data_file.path.resolve(root.path)
data_file.path.subpath(2,4)
data_file.path.toAbsolutePath
root.path.resolve(data_file.path)
data_file.path.toAbsolutePath.toString.substring(root.path.toAbsolutePath.toString.length, data_file.pathAsString.length)

val fs = root.fileSystem
fs.getRootDirectories.asScala.foreach(println)
root.listRelativePaths.foreach(println)

/**
  * PARENT RELATIVE PATH
  */
data_file.path.iterator().asScala.foreach(println)
val data_file_tokens = data_file.path.iterator().asScala.toIndexedSeq
val root_tokens = root.path.iterator().asScala.toIndexedSeq
val rel_path_tokens = data_file_tokens.slice(root_tokens.length, data_file_tokens.length)
val filename = rel_path_tokens.takeRight(1)
val parent_relative_path = rel_path_tokens.dropRight(1)

/**
  * RELATIVE PATH HANDLING BY BETTER FILES
  */
val relative = "/gcrowell/TEST_DATA".toFile
println(relative.path)
println(relative.path.getNameCount)

/**
  * GET PARENT RELATIVE PATH
  */
data_file.pathAsString
root.pathAsString

val rel_file_path : String = data_file.pathAsString.substring(root.pathAsString.length).drop(1)
val rel_parent_path = rel_file_path.dropRight(data_file.name.length + 1)
"".drop(1)
root / "" / rel_file_path

