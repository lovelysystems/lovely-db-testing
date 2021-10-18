import com.lovelysystems.db.testing.DBTest
import com.lovelysystems.db.testing.PGTestSettings

class SQLTest : DBTest(PGTestSettings(devDir = "src/test/sql"))