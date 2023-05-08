package com.westik.file.me.helpers

import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation

class AnimationHelper {

    companion object {
        private var startDegrees = 0.0f
        private var endDegrees = 180.0f

        fun createRotateAnimation(startDegrees: Float = this.startDegrees, endDegrees: Float = this.endDegrees): RotateAnimation {
            val rotateAnimation = RotateAnimation(startDegrees, endDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
            rotateAnimation.interpolator = DecelerateInterpolator()
            rotateAnimation.repeatCount = 0
            rotateAnimation.duration = 800
            rotateAnimation.fillAfter = true

            this.endDegrees = this.startDegrees.also{ this.startDegrees = this.endDegrees }

            return rotateAnimation
        }
    }
}