package com.example.texnostrelka_2025_otbor.presentation.ui.editimagenetwork

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.model.image.request.ImageRequestModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditImageNetworkBinding
import com.example.texnostrelka_2025_otbor.databinding.ActivityEditNetworkBinding
import com.example.texnostrelka_2025_otbor.presentation.factory.EditImageNetworkViewModelFactory
import com.example.texnostrelka_2025_otbor.presentation.ui.editpage.EditPageActivity
import com.example.texnostrelka_2025_otbor.presentation.ui.editpagenetwork.EditPageNetworkActivity
import com.example.texnostrelka_2025_otbor.presentation.utils.toBase64
import com.example.texnostrelka_2025_otbor.presentation.view.PaintView

class EditImageNetworkActivity : AppCompatActivity() {
    private lateinit var paintView: PaintView
    private lateinit var binding: ActivityEditImageNetworkBinding
    private val viewModel : EditImageNetworkViewModel by viewModels {
        EditImageNetworkViewModelFactory(NetworkRepository(), PreferencesManager(this))
    }
    private var pageId = ""
    private var imageId = ""
    private var mode = 0
    private var cellIndex = -1
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            paintView.addImageFromUri(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mode = intent.getIntExtra("MODE", 0)
        when (mode) {
            0 -> throw IllegalArgumentException("Mode is required")
            1 -> {
                pageId = intent.getStringExtra("PAGE-ID")
                    ?: throw IllegalArgumentException("Pageid is required")
                cellIndex = intent.getIntExtra("CELL-INDEX", -1)
                if (cellIndex == -1) throw IllegalArgumentException("Cell Index is required")
            }
            2 -> imageId = intent.getStringExtra("IMAGE-ID") ?: throw IllegalArgumentException("ImageId is required")
        }
        paintView = binding.paintView
        binding.colorBlack.setOnClickListener{paintView.setColor(Color.BLACK)}
        binding.colorRed.setOnClickListener{paintView.setColor(Color.RED)}
        binding.colorBlue.setOnClickListener{paintView.setColor(Color.BLUE)}
        binding.colorGreen.setOnClickListener{paintView.setColor(Color.GREEN)}
        binding.colorYellow.setOnClickListener{paintView.setColor(Color.YELLOW)}
        binding.colorPurple.setOnClickListener{paintView.setColor(Color.rgb(128,0,128))}
        binding.colorCyan.setOnClickListener{paintView.setColor(Color.CYAN)}
        binding.colorOrange.setOnClickListener{paintView.setColor(Color.rgb(255, 165, 0))}
        binding.colorPink.setOnClickListener{paintView.setColor(Color.rgb(255, 192, 203))}
        binding.colorGrey.setOnClickListener{paintView.setColor(Color.rgb(128, 128, 128))}
        binding.colorBrown.setOnClickListener{paintView.setColor(Color.rgb(71, 37, 0))}
        binding.fillButton.setOnClickListener{paintView.setFillMode()}
        binding.eraserButton.setOnClickListener{paintView.setEraserMode()}
        binding.undoButton.setOnClickListener{paintView.undo()}
        binding.redoButton.setOnClickListener{paintView.redo()}
        binding.saveButton.setOnClickListener{savePainting()}
        binding.strokeWidthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                paintView.setStrokeWidth(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.addImageButton.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.moveImageButton.setOnClickListener { paintView.setMoveImageMode(true) }
        binding.zoomInButton.setOnClickListener { paintView.zoomIn() }
        binding.zoomOutButton.setOnClickListener { paintView.zoomOut() }
        binding.panButton.setOnClickListener { paintView.setPanMode() }
        binding.moveTextCloudButton.setOnClickListener { paintView.setMoveTextCloudMode(true) }
        binding.addTextCloudButton.setOnClickListener { paintView.addTextCloud("Новый текст", 100f, 100f, 290f, 100f) }
        binding.editTextCloudButton.setOnClickListener { paintView.setEditTextCloudMode(true) }
        binding.clearCanvasButton.setOnClickListener { paintView.clearCanvas() }
    }

    private fun savePainting() {
        val base64Bitmap = paintView.getBitmap().toBase64() ?: throw IllegalArgumentException("Bitmap is required")
        if (mode == 1) {
            viewModel.addImage(pageId, ImageRequestModel(cellIndex, base64Bitmap))
        }
        else if (mode == 2) {
            viewModel.updateImage(imageId, base64Bitmap)
        }
        else {
            throw IllegalArgumentException("Unknowm mode")
        }
        val intent = Intent(this, EditPageNetworkActivity::class.java).apply {
            putExtra("PAGE-ID", pageId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
        finish()
    }
}