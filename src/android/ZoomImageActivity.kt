package de.impacgroup.zoomimageview.module

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ZoomImageActivity : AppCompatActivity() {

    lateinit var imageFragment: ZoomImageFragment

    companion object: ZoomImage {

        private const val IMAGE_DATA_KEY = "imageData"
        private const val IMAGE_POSITION_KEY = "imagePosition"
        const val CLOSE_BUTTON_KEY = "showCloseButton"
        const val ZOOM_IMAGE_STATE_ACTION = "zoom-image-view-state"
        const val ZOOM_IMAGE_STATE_KEY = "viewState"


        override fun present(activity: AppCompatActivity, bitmap: Bitmap, point: ImagePosition?) {
            present(activity, bitmap, point, true)
        }

        override fun present(
            activity: AppCompatActivity,
            bitmap: Bitmap,
            point: ImagePosition?,
            closeButton: Boolean
        ) {
            val file = saveImage(activity.applicationContext , bitmap)
            val detailIntent = Intent(activity.applicationContext, ZoomImageActivity::class.java)
            detailIntent.putExtra(IMAGE_DATA_KEY, file.path)
            detailIntent.putExtra(IMAGE_POSITION_KEY, point)
            detailIntent.putExtra(CLOSE_BUTTON_KEY, closeButton)
            activity.startActivity(detailIntent)
            activity.overridePendingTransition(android.R.anim.fade_in, 0)
        }


        private fun saveImage(context: Context, bitmap: Bitmap): File {
            val file = File.createTempFile(String.format("%s.jpeg", UUID.randomUUID().toString()), null, context.cacheDir)
            file.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
            return file
        }
    }

    private fun loadResource(name: String, defType: ResourceType): Int {
        val packageName: String = this.application.packageName
        val resources: Resources = this.application.resources
        return resources.getIdentifier(name, defType.key, packageName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(loadResource("Theme.ZoomView", ResourceType.STYLE))
        super.onCreate(savedInstanceState)
        setContentView(loadResource("activity_zoom_image", ResourceType.LAYOUT))

        intent.extras?.getString(IMAGE_DATA_KEY)?.let {
            val bitmap = BitmapFactory.decodeFile(it)
            present(bitmap, intent.extras?.getParcelable(IMAGE_POSITION_KEY))
        }

        intent.extras?.getBoolean(CLOSE_BUTTON_KEY)?.let {
            val closeBtn: ImageButton = findViewById(loadResource("close_button", ResourceType.IDENTIFIER))
            closeBtn.visibility = if (it){View.VISIBLE} else {View.GONE}
            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun present(bitmap: Bitmap, position: ImagePosition?) {
        imageFragment = ZoomImageFragment.present(bitmap, position)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        val view = findViewById<View>(loadResource("fragment_container_view", ResourceType.IDENTIFIER))
        transaction.add(view.id, imageFragment).commit()
    }

    override fun onBackPressed() {
        dismiss()
    }

    private fun dismiss() {
        send(ViewState.WILL_CLOSE)
        imageFragment.animate(AnimationDirection.FROM_CENTER,  object: AnimationListener {
            override fun onAnimationEnd() {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                transaction.remove(imageFragment)
                transaction.commit()
                finish()
                overridePendingTransition(0, android.R.anim.fade_out)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            send(ViewState.DID_CLOSE)
        }
    }

    private fun send(state: ViewState) {
        val intent = Intent(ZOOM_IMAGE_STATE_ACTION)
        intent.putExtra(ZOOM_IMAGE_STATE_KEY, state.key)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}