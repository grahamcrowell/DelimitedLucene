import java.io.{File => JFile}

import better.files.File
import com.github.grahamcrowell.indexer.DatedDataFolder

val root = File("/Users/gcrowell/workspace/esldata")

val data_file_path = File("/Users/gcrowell/workspace/esldata/WFF_m1f/20170625/Absence.csv")

data_file_path.path.startsWith(root.path)

root.path.getNameCount()

data_file_path.path.subpath(3, 7)

//data_file_path.path.subpath(3,)

val hash = data_file_path.digest("md5")
val hash_str = hash.toString

val dated_dir = DatedDataFolder(File("/Users/gcrowell/workspace/esldata/WFF_m0k/20170702"))

val data_files = dated_dir.delimitedDataFiles

val hashes = data_files.map(_.file.digest("sha512"))

val str_hashes = hashes.map(_.mkString)

var temp_hash = str_hashes.next()
println(temp_hash)
temp_hash = str_hashes.next()
temp_hash = str_hashes.next()
temp_hash = str_hashes.next()

val hashFunc = com.google.common.hash.Hashing.sha512()
val hashString = com.google.common.io.Files.asByteSource(new JFile(data_files.next().file.path.toString)).hash(hashFunc)
println(hashString)
