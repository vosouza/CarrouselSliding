package com.evosouza.libcarrousel

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

class OverlayableImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?  = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private lateinit var imageView: ImageView
    private lateinit var button: ImageButton

    var image: Image? = null
        set(value) {
            field = value
            value?.let {
                Glide.with(imageView)
                    .load(it.url)
                    .transition(withCrossFade())
                    .transform(
                        FitCenter(),
                        RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_corners_radius))
                    )
                    .into(imageView)
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.carrousel_component, this, true)
        imageView = findViewById(R.id.image_view)
        button = findViewById(R.id.send_button)
        layoutTransition = LayoutTransition()
        isActivated = false

        button.setOnClickListener {
            image?.let {
                Toast.makeText(context, "Botao clicado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setActivated(activated: Boolean) {
        val isChanging = activated != isActivated
        super.setActivated(activated)

        if(isChanging){
            button.isInvisible = !activated
        }
    }
}