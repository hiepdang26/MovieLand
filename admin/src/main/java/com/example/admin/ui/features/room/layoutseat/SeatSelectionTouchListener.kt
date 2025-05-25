import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class SeatSelectionTouchListener(
    private val recyclerView: RecyclerView,
    private val onSelectionChanged: (Set<Int>) -> Unit
) : RecyclerView.OnItemTouchListener {

    private var gestureDetector: GestureDetector
    private var startPos = -1
    private var currentPos = -1
    private var hasMoved = false

    init {
        gestureDetector = GestureDetector(recyclerView.context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean {
                hasMoved = false
                return false
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                hasMoved = true
                return false
            }

        })
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(e)

        when (e.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                startPos = rv.getChildAdapterPosition(rv.findChildViewUnder(e.x, e.y) ?: return false)
                currentPos = startPos
            }
            MotionEvent.ACTION_MOVE -> {
                val newPos = rv.getChildAdapterPosition(rv.findChildViewUnder(e.x, e.y) ?: return false)
                if (newPos != RecyclerView.NO_POSITION && newPos != currentPos) {
                    currentPos = newPos
                    updateSelection()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!hasMoved) {
                }
                startPos = -1
                currentPos = -1
                hasMoved = false
            }
        }
        return false
    }

    private fun updateSelection() {
        if (startPos == -1 || currentPos == -1) return

        val selectedPositions = if (startPos <= currentPos) {
            (startPos..currentPos).toSet()
        } else {
            (currentPos..startPos).toSet()
        }
        onSelectionChanged(selectedPositions)
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}

