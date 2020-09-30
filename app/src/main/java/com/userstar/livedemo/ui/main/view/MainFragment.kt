package com.userstar.livedemo.ui.main.view

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.squareup.picasso.Picasso
import com.userstar.livedemo.R
import com.userstar.livedemo.ui.main.viewModel.MainViewModel
import com.userstar.livedemo.ui.main.viewModel.MainViewModelFactory
import com.userstar.livedemo.ui.main.viewModel.Review
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.util.ArrayList


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory()
    }

    private lateinit var youTubePlayerView: YouTubePlayerView
    private var player: YouTubePlayer? = null
    private lateinit var reviewListRecyclerViewAdapter: ReviewListRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)

        youTubePlayerView = view.findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)
        youTubePlayerView.addYouTubePlayerListener(object :AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                player = youTubePlayer
                player?.loadVideo("jrKKZZ2gBHg", 0F)
            }
        })

        reviewListRecyclerViewAdapter = ReviewListRecyclerViewAdapter()
        val reviewListRecyclerView = view.findViewById<RecyclerView>(R.id.review_list_recyclerView)
        reviewListRecyclerView.layoutManager = LinearLayoutManager(context)
        reviewListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        reviewListRecyclerView.adapter = reviewListRecyclerViewAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.reviewList.observe(viewLifecycleOwner) { reviewList ->
            reviewListRecyclerViewAdapter.reviewList = reviewList
            reviewListRecyclerViewAdapter.notifyDataSetChanged()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
            player?.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(review: Review) {
        reviewListRecyclerViewAdapter.reviewList.add(0, review)
        reviewListRecyclerViewAdapter.notifyDataSetChanged()
    }

    inner class ReviewListRecyclerViewAdapter : RecyclerView.Adapter<ReviewListRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.review_list_holder, parent, false))

        var reviewList: ArrayList<Review> = ArrayList()

        override fun getItemCount() = reviewList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Picasso.get()
                .load("https://img.youtube.com/vi/${reviewList[position].id}/mqdefault.jpg")
                .into(holder.thumbnailImageView)
            holder.titleTextView.text = reviewList[position].title
            holder.timeTextView.text = reviewList[position].time
            if (reviewList[position].isNew) {
                holder.isNewHintTextView.visibility = View.VISIBLE
            }
            holder.itemView.setOnClickListener {
                reviewList[position].isNew = false
                holder.isNewHintTextView.visibility = View.INVISIBLE

                val args = Bundle()
                args.putParcelable("review", reviewList[position])

                val fragment = ReviewFragment()
                fragment.arguments = args

                parentFragmentManager.beginTransaction()
                    .add(R.id.container, fragment)
                    .addToBackStack("ReviewFragment")
                    .commit()
            }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val thumbnailImageView: ImageView = view.findViewById(R.id.thumbnail_imageView)
            val titleTextView: TextView = view.findViewById(R.id.title_textView)
            val timeTextView: TextView = view.findViewById(R.id.time_textView)
            val isNewHintTextView: TextView = view.findViewById(R.id.is_new_hint_textView)
        }
    }
}