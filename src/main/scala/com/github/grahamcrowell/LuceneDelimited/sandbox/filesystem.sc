import better.files._

val root = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root")
var data_file = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root/20170625/Absence.csv")
data_file = File("/Users/gcrowell/Documents/git/DelimitedLucene/test_data/indexing_tests/mock_tenant_root/20170625/Absence.csv")

data_file.path.resolve(root.path)
data_file.path.subpath(2,4)
data_file.path.toAbsolutePath
root.path.resolve(data_file.path)
data_file.path.toAbsolutePath.toString.substring(root.path.toAbsolutePath.toString.length, data_file.pathAsString.length)

