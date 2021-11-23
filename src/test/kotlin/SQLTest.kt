import com.lovelysystems.db.testing.DBTest
import com.lovelysystems.db.testing.PGTestSettings

// by default testFilePattern prepends "glob:"
class SQLTestPattern : DBTest(PGTestSettings(devDir = "src/test/sql", testFilePattern = "test_exampl?.sql"))

// demonstrate how to use a regex as testFilePattern
class SQLTestRegex : DBTest(PGTestSettings(devDir = "src/test/sql", testFilePattern = "regex:^test_e(?:x|z)ample\\.sql\$"))