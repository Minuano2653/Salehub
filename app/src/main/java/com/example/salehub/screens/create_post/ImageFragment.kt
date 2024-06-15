package com.example.salehub.screens.create_post

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.example.salehub.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    private lateinit var binding: FragmentImageBinding
    private lateinit var uri: Uri

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = arguments?.getParcelable<Uri>(ARG_IMAGE_URI)
        Glide
            .with(this)
            .load(uri)
            .into(binding.imageView)
        //binding.imageView.setImageURI(uri)
    }

    companion object {
        private const val ARG_IMAGE_URI = "IMAGE_URI"
        fun newInstance(uri: Uri): ImageFragment {
            val fragment = ImageFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE_URI, uri)
            fragment.arguments = args
            return fragment
        }
    }
}