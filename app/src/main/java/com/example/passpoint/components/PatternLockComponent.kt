// PatternLockComposable.kt
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.passpoint.R
import com.itsxtt.patternlock.PatternLockView

@Composable
fun PatternLockComponent() {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth(),
        factory = { context ->
            // Načítanie XML layoutu
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.pattern_lock_view, null)

            // Získať referenciu na PatternLockView z layoutu
            val patternLockView = view.findViewById<PatternLockView>(R.id.pattern_lock_view)

            // Nastavenie listenera pre PatternLockView
            patternLockView.setOnPatternListener(object : PatternLockView.OnPatternListener {
                override fun onStarted() {
                    // Keď používateľ začne kresliť pattern
                }

                override fun onProgress(ids: ArrayList<Int>) {
                    // Keď používateľ posúva prst po obrazovke
                }

                override fun onComplete(ids: ArrayList<Int>): Boolean {
                    // Keď je pattern dokončený
                    println(ids);
                    return isPatternCorrect(ids) // Implementuj svoju logiku
                }
            })

            // Vrátenie root view (LinearLayout) z layoutu
            view as LinearLayout
        }
    )
}

fun isPatternCorrect(ids: ArrayList<Int>): Boolean {
    // Implementuj svoju logiku na kontrolu správnosti patternu
    // Napríklad porovnanie s uloženým patternom
    return ids == arrayListOf(1, 2, 3, 4) // Príklad správneho patternu
}