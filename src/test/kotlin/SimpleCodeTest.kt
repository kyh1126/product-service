import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class SimpleCodeTest {
    private fun calculateBestBefore(expirationDate: LocalDateTime, manufacturedBefore: Int): Float {
        val today = LocalDateTime.now()
        val duration = Duration.between(today, expirationDate).toDays()

        return duration.div(manufacturedBefore.toFloat())
    }

    @Test
    fun dateTest() {
        val expirationDate = LocalDateTime.now().plusDays(30)
        val manufacturedBefore = 40
        val 상미기한 = calculateBestBefore(expirationDate, manufacturedBefore)

        println(상미기한)
    }
}