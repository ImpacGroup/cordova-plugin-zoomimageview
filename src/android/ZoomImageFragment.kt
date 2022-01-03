package de.impacgroup.zoomimageview.module

import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager

class ZoomImageFragment: Fragment() {

    var bitmap: Bitmap? = null
        set(value) {
            field = value
            if (this::zoomImageView.isInitialized && this::animImageView.isInitialized && value != null) {
                zoomImageView.setImageBitmap(value)
                animImageView.setImageBitmap(value)
            }
        }

    lateinit var zoomImageView: ImageView
    lateinit var animImageView: ImageView
    lateinit var zoomImageFragment: ConstraintLayout

    var position: ImagePosition? = null

    companion object {
        fun present(bitmap: Bitmap, position: ImagePosition?): ZoomImageFragment {
            val fragment = ZoomImageFragment()
            fragment.bitmap = bitmap
            fragment.position = position
            return fragment
        }
    }

    private fun loadResource(name: String, defType: ResourceType): Int? {
        val packageName: String? = activity?.application?.packageName
        val resources: Resources? = activity?.application?.resources
        return resources?.getIdentifier(name, defType.key, packageName)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(loadResource("zoom_image_fragment", ResourceType.LAYOUT)!!, container, false)
        zoomImageView = view.findViewById(loadResource("zoomImageView", ResourceType.IDENTIFIER)!!)
        animImageView = view.findViewById(loadResource("animImageView", ResourceType.IDENTIFIER)!!)
        zoomImageFragment = view.findViewById(loadResource("zoom_image_layout", ResourceType.IDENTIFIER)!!)
        onViewInitialised()
        return view
    }

    fun onViewInitialised() {
        bitmap?.let {
            zoomImageView.setImageBitmap(it)
            animImageView.setImageBitmap(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        position?.let {
            position = it
            animate(AnimationDirection.TO_CENTER, object: AnimationListener {
                override fun onAnimationEnd() {
                    zoomImageView.visibility = View.VISIBLE
                    animImageView.visibility = View.INVISIBLE
                }
            })
        } ?: kotlin.run {
            animImageView.visibility = View.INVISIBLE
            zoomImageView.visibility = View.VISIBLE
        }
    }

    private fun canAnimate(position: ImagePosition): Boolean {
        return activity?.resources?.configuration?.orientation == position.orientation
    }

    fun animate(animationDirection: AnimationDirection, listener: AnimationListener?) {

        position?.let { mPosition ->
            // Checks if the ImagePosition fits to the current display orientation;
            if (!canAnimate(mPosition)) {
                listener?.onAnimationEnd()
                return
            }

            animImageView.visibility = View.VISIBLE
            zoomImageView.visibility = View.INVISIBLE

            val animImageViewId = loadResource("animImageView", ResourceType.IDENTIFIER)
            val zoomImageFragmentId = loadResource("zoom_image_fragment", ResourceType.IDENTIFIER)
            if (animImageViewId != null && zoomImageFragmentId != null) {
                when (animationDirection) {
                    AnimationDirection.TO_CENTER -> {
                        toCenterTransition(mPosition, animImageViewId, zoomImageFragmentId, listener)
                    }
                    AnimationDirection.FROM_CENTER -> {
                        fromCenterTransition(mPosition, animImageViewId, zoomImageFragmentId, listener)
                    }
                }
            }
        } ?: kotlin.run {
            listener?.onAnimationEnd()
        }

    }

    private fun toCenterTransition(
        position: ImagePosition,
        animImageViewId: Int,
        zoomImageFragmentId: Int,
        listener: AnimationListener?
    ) {
        val startConstraintSet = ConstraintSet()
        startConstraintSet.clone(zoomImageFragment)
        startConstraintSet.connect(
            animImageViewId,
            ConstraintSet.TOP,
            zoomImageFragmentId,
            ConstraintSet.TOP,
            position.rect.y.toInt()
        )
        startConstraintSet.connect(
            animImageViewId,
            ConstraintSet.START,
            zoomImageFragmentId,
            ConstraintSet.START,
            position.rect.x.toInt()
        )
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        startConstraintSet.connect(
            animImageViewId,
            ConstraintSet.END,
            zoomImageFragmentId,
            ConstraintSet.END,
            displayMetrics.widthPixels - (position.rect.x.toInt() + position.rect.width)
        )
        startConstraintSet.applyTo(zoomImageFragment)

        zoomImageFragment.post {
            val endConstraintSet = ConstraintSet()
            endConstraintSet.clone(zoomImageFragment)
            endConstraintSet.connect(
                animImageViewId,
                ConstraintSet.TOP,
                zoomImageFragmentId,
                ConstraintSet.TOP,
                0
            )
            endConstraintSet.connect(
                animImageViewId,
                ConstraintSet.START,
                zoomImageFragmentId,
                ConstraintSet.START,
                0
            )
            endConstraintSet.connect(
                animImageViewId,
                ConstraintSet.END,
                zoomImageFragmentId,
                ConstraintSet.END,
                0
            )
            endConstraintSet.connect(
                animImageViewId,
                ConstraintSet.BOTTOM,
                zoomImageFragmentId,
                ConstraintSet.BOTTOM,
                0
            )
            TransitionManager.beginDelayedTransition(
                zoomImageFragment,
                getTransition(listener)
            )
            endConstraintSet.applyTo(zoomImageFragment)
        }
    }

    private fun fromCenterTransition(
        position: ImagePosition,
        animImageViewId: Int,
        zoomImageFragmentId: Int,
        listener: AnimationListener?
    ) {
        zoomImageFragment.post {
            val endConstraintSet = ConstraintSet()
            endConstraintSet.clone(zoomImageFragment)
            endConstraintSet.removeFromVerticalChain(animImageViewId)
            endConstraintSet.connect(
                animImageViewId,
                ConstraintSet.TOP,
                zoomImageFragmentId,
                ConstraintSet.TOP,
                position.rect.y.toInt()
            )

            endConstraintSet.connect(
                animImageViewId,
                ConstraintSet.START,
                zoomImageFragmentId,
                ConstraintSet.START,
                position.rect.x.toInt()
            )

            val displayMetrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            endConstraintSet.connect(
                animImageViewId,
                ConstraintSet.END,
                zoomImageFragmentId,
                ConstraintSet.END,
                displayMetrics.widthPixels - (position.rect.x.toInt() + position.rect.width)
            )
            TransitionManager.beginDelayedTransition(
                zoomImageFragment,
                getTransition(listener)
            )
            endConstraintSet.applyTo(zoomImageFragment)
        }
    }

    private fun getTransition(listener: AnimationListener?): Transition {
        val transition = ChangeBounds()
        transition.duration = 250
        transition.addListener(object: Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }
            override fun onTransitionEnd(transition: Transition) {
                listener?.onAnimationEnd()
            }
            override fun onTransitionCancel(transition: Transition) {
            }
            override fun onTransitionPause(transition: Transition) {
            }
            override fun onTransitionResume(transition: Transition) {
            }
        })
        return transition
    }
}