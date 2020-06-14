package com.utkarsh.daggerhiltexample.trendingRepositories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.utkarsh.daggerhiltexample.R
import com.utkarsh.daggerhiltexample.databinding.RepositoryItemBinding
import com.utkarsh.daggerhiltexample.domain.Repository
import kotlinx.android.synthetic.main.repository_item.view.*


class RepositoriesAdapter() : RecyclerView.Adapter<RepositoriesViewHolder>() {

    var repositories: List<Repository> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var expandedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesViewHolder {
        val withDataBinding: RepositoryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            RepositoriesViewHolder.LAYOUT,
            parent,
            false)
        return RepositoriesViewHolder(withDataBinding)
    }

    override fun getItemCount() = repositories.size

    override fun onBindViewHolder(holder: RepositoriesViewHolder, position: Int) {
        holder.viewDataBinding.also {
            it.repository = repositories[position]
        }
        holder.viewDataBinding.executePendingBindings()
        val expandableView = holder.itemView.expandable_layout
        val isExpanded = position==expandedPosition;
        if (position == expandedPosition) {
            if (expandableView.visibility != View.VISIBLE) {
                expandableView.visibility = View.VISIBLE
            }
        } else {
            if (expandableView.visibility != View.GONE) {
                expandableView.visibility = View.GONE
            }
        }
        holder.itemView.setOnClickListener {
            if (isExpanded) {
                expandedPosition = -1
                notifyItemChanged(position)
                return@setOnClickListener
            }
            if (expandedPosition >= 0) {
                val prev = expandedPosition
                notifyItemChanged(prev)
            }
            expandedPosition = position
            notifyItemChanged(expandedPosition)
        }
    }

}

class RepositoriesViewHolder(val viewDataBinding: RepositoryItemBinding) :
    RecyclerView.ViewHolder(viewDataBinding.root) {
    companion object {
        @LayoutRes
        val LAYOUT = R.layout.repository_item
    }
}
