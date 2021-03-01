package com.example.birder.fragments

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.birder.R
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ARFragment: AppCompatActivity(), View.OnClickListener {
    lateinit var arrayView: Array<View>

    private lateinit var duck: ImageView

    private lateinit var duckSound: MediaPlayer

    private lateinit var arFrag: ArFragment

    var duckRenderable:ModelRenderable? = null


    internal var selected = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        arFrag = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment
        duckSound = MediaPlayer.create(this, R.raw.duck)

        setArray()
        setClickListener()
        setModel()


        arFrag.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFrag.arSceneView.scene)
            createModel(anchorNode,selected)
        }
    }

    private fun createModel(anchorNode: AnchorNode, selected: Int) {
        /*
        if(selected == 1){
            val toymoped = TransformableNode(arFrag.transformationSystem)
            toymoped.setParent(anchorNode)
            toymoped.renderable = toyMopedRenderable
            toymoped.setOnTapListener { _, _ ->
                mopedSound.start()
            }
            toymoped.select()
        }
        } else if (selected == 3){ */
            val duck = TransformableNode(arFrag.transformationSystem)
            duck.setParent(anchorNode)
            duck.renderable = duckRenderable
            duck.select()
    }

    private fun setModel() {
        val duckUri = Uri.parse("file:///android_asset/duck/duck.gltf")
      //  val duckUri = Uri.parse("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF/Duck.gltf")

        ModelRenderable.builder()
            .setSource(
                this,
                RenderableSource.builder().setSource(this, duckUri, RenderableSource.SourceType.GLTF2)
                    .setScale(0.3f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            ).setRegistryId("duck").build()
            .thenAccept { duckRenderable = it }
            .exceptionally {
                Log.e("YUP", "renderableFuture error: ${it.localizedMessage}")
                null
            }
    }

    private fun setArray(){
        duck = findViewById(R.id.duck)

        arrayView = arrayOf(
            duck,
        )
    }

    private fun setClickListener() {
        for (i in arrayView.indices){
            arrayView[i].setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        /*
        if(v!!.id == R.id.toymoped){
            selected = 1
            mySetBackground(v!!.id)
        }
        }  else*/ if(v!!.id == R.id.duck){
            selected = 3
            mySetBackground(v!!.id)
        }
    }

    private fun mySetBackground(id: Int) {
        for(i in arrayView.indices){
            if(arrayView[i].id == id)
                arrayView[i].setBackgroundColor(Color.parseColor("#80333639"))
            else
                arrayView[i].setBackgroundColor(Color.TRANSPARENT)
        }

    }
}