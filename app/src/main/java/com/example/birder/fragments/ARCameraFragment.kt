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
    lateinit var arrayView: Array<View>

    private lateinit var duck: ImageView
    private lateinit var eagle: ImageView
    private lateinit var yellow: ImageView


    private lateinit var duckSound: MediaPlayer
    private lateinit var eagleSound: MediaPlayer
    private lateinit var yellowSound: MediaPlayer


    private lateinit var arFrag: ArFragment

    private var duckRenderable: ModelRenderable? = null
    private var eagleRenderable: ModelRenderable? = null
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
        eagleSound = MediaPlayer.create(activity, R.raw.eagle)
        yellowSound = MediaPlayer.create(activity, R.raw.yelllowbird)


        duck = v.findViewById(R.id.duck)
        eagle = v.findViewById(R.id.eagle)
        yellow = v.findViewById(R.id.yellow)

        setArray()
        setClickListener()
        setModel()



        arFrag.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFrag.arSceneView.scene)
            createModel(anchorNode, selected)
        }

        return v
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
        if (selected == 1) {
            val duck = TransformableNode(arFrag.transformationSystem)
            duck.setParent(anchorNode)
            duck.renderable = duckRenderable
            duck.setOnTapListener { _, _ ->
                duckSound.start()
            }
            duck.select()
        } else if (selected == 2) {
            val eagle = TransformableNode(arFrag.transformationSystem)
            eagle.setParent(anchorNode)
            eagle.renderable = eagleRenderable
            eagle.setOnTapListener { _, _ ->
                eagleSound.start()
            }
            eagle.select()
        } else if (selected == 3) {
            val yellow = TransformableNode(arFrag.transformationSystem)
            yellow.setParent(anchorNode)
            yellow.renderable = yellowRenderable
            yellow.setOnTapListener { _, _ ->
                yellowSound.start()
            }
            yellow.select()
        }
    }

    private fun setModel() {
        val duckUri = Uri.parse("file:///android_asset/duck/duck.gltf")
        val eagleUri = Uri.parse("file:///android_asset/eagle/eagle.gltf")
        val yellowUri = Uri.parse("file:///android_asset/yellowbird/yellowbird.gltf")

        //  val duckUri = Uri.parse("https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF/Duck.gltf")

        ModelRenderable.builder()
            .setSource(
                activity,
                RenderableSource.builder()
                    .setSource(activity, duckUri, RenderableSource.SourceType.GLTF2)
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
                    .setSource(activity, eagleUri, RenderableSource.SourceType.GLTF2)
                    .setScale(20f)
                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                    .build()
            ).setRegistryId("eagle").build()
            .thenAccept { eagleRenderable = it }
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
            eagle,
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
            v.id == R.id.eagle -> {
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