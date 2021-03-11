package com.example.birder.fragments

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.birder.R
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class ARCameraFragment : Fragment(), View.OnClickListener {
    private lateinit var arrayView: Array<View>

    private lateinit var duck: ImageView
    private lateinit var crow: ImageView
    private lateinit var yellow: ImageView


    private lateinit var duckSound: MediaPlayer
    private lateinit var crowSound: MediaPlayer
    private lateinit var yellowSound: MediaPlayer


    private lateinit var arFrag: ArFragment

    private var duckRenderable: ModelRenderable? = null
    private var crowRenderable: ModelRenderable? = null
    private var yellowRenderable: ModelRenderable? = null


    private var selected = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_ar_camera, container, false)

        val supportFragmentManager = childFragmentManager

        arFrag = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment

        duckSound = MediaPlayer.create(activity, R.raw.duck)
        crowSound = MediaPlayer.create(activity, R.raw.crow)
        yellowSound = MediaPlayer.create(activity, R.raw.yellowbird)


        duck = v.findViewById(R.id.duck)
        crow = v.findViewById(R.id.crow)
        yellow = v.findViewById(R.id.yellow)

        setArray()
        setClickListener()
        setModel()



        arFrag.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFrag.arSceneView.scene)
            createModel(anchorNode, selected)
        }

        return v
    }

    private fun createModel(anchorNode: AnchorNode, selected: Int) {
        when (selected) {
            1 -> {
                val duck = TransformableNode(arFrag.transformationSystem)
                duck.setParent(anchorNode)
                duck.renderable = duckRenderable
                duck.setOnTapListener { _, _ ->
                    duckSound.start()
                }
                duck.select()
            }
            2 -> {
                val crow = TransformableNode(arFrag.transformationSystem)
                crow.setParent(anchorNode)
                crow.renderable = crowRenderable
                crow.setOnTapListener { _, _ ->
                    crowSound.start()
                }
                crow.select()
            }
            3 -> {
                val yellow = TransformableNode(arFrag.transformationSystem)
                yellow.setParent(anchorNode)
                yellow.renderable = yellowRenderable
                yellow.setOnTapListener { _, _ ->
                    yellowSound.start()
                }
                yellow.select()
            }
        }
    }

    //Model settings
    private fun setModel() {
        val duckUri = Uri.parse("file:///android_asset/duck/duck.gltf")
        val crowUri = Uri.parse("file:///android_asset/crow/crow.gltf")
        val yellowUri = Uri.parse("file:///android_asset/yellowbird/yellowbird.gltf")

        ModelRenderable.builder()
            .setSource(
                activity,
                RenderableSource.builder()
                    .setSource(activity, duckUri, RenderableSource.SourceType.GLTF2)
                        //Change the size of the model
                    .setScale(0.15f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            ).setRegistryId("duck").build()
            .thenAccept { duckRenderable = it }
            .exceptionally {
                Log.e("YUP", "renderableFuture error: ${it.localizedMessage}")
                null
            }

        ModelRenderable.builder()
            .setSource(
                activity,
                RenderableSource.builder()
                    .setSource(activity, crowUri, RenderableSource.SourceType.GLTF2)
                    .setScale(0.15f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            ).setRegistryId("crow").build()
            .thenAccept { crowRenderable = it }
            .exceptionally {
                Log.e("YUP", "renderableFuture error: ${it.localizedMessage}")
                null
            }

        ModelRenderable.builder()
            .setSource(
                activity,
                RenderableSource.builder()
                    .setSource(activity, yellowUri, RenderableSource.SourceType.GLTF2)
                    .setScale(0.15f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            ).setRegistryId("yellow").build()
            .thenAccept { yellowRenderable = it }
            .exceptionally {
                Log.e("YUP", "renderableFuture error: ${it.localizedMessage}")
                null
            }

    }

    private fun setArray() {
        arrayView = arrayOf(
            duck,
            crow,
            yellow
        )
    }

    private fun setClickListener() {
        for (i in arrayView.indices) {
            arrayView[i].setOnClickListener(this)
        }
    }

    override fun onClick(v: View?) {
        when {
            v!!.id == R.id.duck -> {
                selected = 1
                mySetBackground(v.id)
            }
            v.id == R.id.crow -> {
                selected = 2
                mySetBackground(v.id)
            }
            v.id == R.id.yellow -> {
                selected = 3
                mySetBackground(v.id)
            }
        }
    }

    private fun mySetBackground(id: Int) {
        for (i in arrayView.indices) {
            if (arrayView[i].id == id)
                arrayView[i].setBackgroundColor(Color.parseColor("#80333639"))
            else
                arrayView[i].setBackgroundColor(Color.TRANSPARENT)
        }
    }
}