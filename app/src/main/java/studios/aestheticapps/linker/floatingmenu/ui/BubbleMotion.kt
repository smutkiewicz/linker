package studios.aestheticapps.linker.floatingmenu.ui

import android.graphics.PointF
import android.os.Build
import android.os.SystemClock
import android.view.View

/**
 * Moves and scales a View as if its hovering.
 */
class BubbleMotion
{
    private var view: View? = null
    private var isRunning: Boolean = false
    private var timeOfLastUpdate: Long = 0
    private var dtInMillis: Int = 0

    private val brownianMotionGenerator = BrownianMotionGenerator()
    private val growAndShrinkGenerator = GrowAndShrinkGenerator(0.05f)

    private val stateUpdateRunnable = object : Runnable
    {
        override fun run()
        {
            if (isRunning)
            {
                // Calculate the time that's passed since the last update.
                dtInMillis = (SystemClock.elapsedRealtime() - timeOfLastUpdate).toInt()

                // Update visual state.
                updatePosition()
                updateGrowth()

                // Update time tracking.
                timeOfLastUpdate = SystemClock.elapsedRealtime()

                // Schedule next loop.
                view!!.postDelayed(this, RENDER_CYCLE_IN_MILLIS.toLong())
            }
        }
    }

    fun start(view: View)
    {
        this.view = view
        isRunning = true
        timeOfLastUpdate = SystemClock.elapsedRealtime()
        this.view!!.post(stateUpdateRunnable)
    }

    fun stop()
    {
        isRunning = false
    }

    private fun updatePosition()
    {
        // Calculate a new position and move the View.
        val mPositionOffset = brownianMotionGenerator.applyMotion(dtInMillis)
        view!!.apply {
            translationX = mPositionOffset.x
            translationY = mPositionOffset.y
        }
    }

    private fun updateGrowth()
    {
        // Calculate and apply scaling.
        val scale = growAndShrinkGenerator.applyGrowth(dtInMillis)
        view!!.apply {
            scaleX = scale
            scaleY = scale
        }

        // Set elevation based on scale (the bigger, the higher).
        val baseElevation = 50
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            view!!.elevation = baseElevation * scale
        }
    }

    class BrownianMotionGenerator
    {
        private val mMaxDisplacementInPixels = 200
        private val mPosition = PointF(0f, 0f)
        private val mVelocity = PointF(0f, 0f)

        fun applyMotion(dtInMillis: Int): PointF
        {
            val xConstraintAdditive = mPosition.x / mMaxDisplacementInPixels
            val yConstraintAdditive = mPosition.y / mMaxDisplacementInPixels

            // Randomly adjust velocity.
            val velocityXAdjustment = (Math.random() * 1.0 - 0.5).toFloat() - xConstraintAdditive
            val velocityYAdjustment = (Math.random() * 1.0 - 0.5).toFloat() - yConstraintAdditive
            mVelocity.offset(velocityXAdjustment, velocityYAdjustment)

            // Apply velocity to position.
            mPosition.offset(mVelocity.x, mVelocity.y)

            // Apply friction to velocity.
            mVelocity.set(mVelocity.x * FRICTION, mVelocity.y * FRICTION)

            return mPosition
        }

        companion object
        {
            private const val FRICTION = 0.8f
        }
    }

    class GrowAndShrinkGenerator(private val mGrowthFactor: Float)
    {
        private var mLastTimeInMillis: Int = 0

        fun applyGrowth(dtInMillis: Int): Float
        {
            mLastTimeInMillis += dtInMillis
            return 1.0f + (Math.sin(Math.PI * (mLastTimeInMillis.toFloat() / GROWTH_CYCLE_IN_MILLIS)) * mGrowthFactor).toFloat()
        }

        companion object
        {
            private const val GROWTH_CYCLE_IN_MILLIS = 5000
        }
    }

    companion object
    {
        private const val TAG = "BubbleMotion"
        private const val RENDER_CYCLE_IN_MILLIS = 16 // 60 FPS.
    }
}
