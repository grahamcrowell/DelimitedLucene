import better.files.File

val root = File("/Users/gcrowell/workspace/esldata")

val data_file_path = File("/Users/gcrowell/workspace/esldata/WFF_m1f/20170625/Absence.csv")

data_file_path.path.startsWith(root.path)

root.path.getNameCount()

data_file_path.path.subpath(3,7)

data_file_path.path.subpath(3,-1)


