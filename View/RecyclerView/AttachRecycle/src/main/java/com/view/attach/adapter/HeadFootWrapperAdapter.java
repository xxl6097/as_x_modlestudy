package com.view.attach.adapter;

import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.yline.base.common.CommonRecycleViewHolder;
import com.yline.base.common.CommonRecyclerAdapter;

/**
 * Bug与方便同在(遇到再调节)
 * @author yline 2017/2/16 --> 18:19
 * @version 1.0.0
 */
public class HeadFootWrapperAdapter<T> extends CommonRecyclerAdapter<T>
{
	// sList的最大数目
	private static final int BASE_ITEM_TYPE_HEADER = 100000;

	// 200000 - 100000 是头部布局的最大个数
	private static final int BASE_ITEM_TYPE_FOOTER = 200000;

	// 头布局
	private SparseArrayCompat<View> headerViewArray = new SparseArrayCompat<>();

	// 底部布局
	private SparseArrayCompat<View> footViewArray = new SparseArrayCompat<>();

	private CommonRecyclerAdapter mInnerAdapter;

	public HeadFootWrapperAdapter(CommonRecyclerAdapter adapter)
	{
		this.mInnerAdapter = adapter;
		this.sList = mInnerAdapter.getDataList();
	}

	/**
	 * 创建时会调用多次,依据viewType类型,创建ViewHolder
	 * @param parent
	 * @param viewType
	 * @return
	 */
	@Override
	public CommonRecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		if (headerViewArray.get(viewType) != null)
		{
			return new CommonRecycleViewHolder(headerViewArray.get(viewType));
		}
		else if (footViewArray.get(viewType) != null)
		{
			return new CommonRecycleViewHolder(footViewArray.get(viewType));
		}
		return mInnerAdapter.onCreateViewHolder(parent, viewType);
	}

	@Override
	public int getItemRes()
	{
		return mInnerAdapter.getItemRes();
	}

	@Override
	public void setViewContent(CommonRecycleViewHolder commonRecycleViewHolder, int i)
	{
		mInnerAdapter.setViewContent(commonRecycleViewHolder, i);
	}

	@Override
	public int getItemViewType(int position)
	{
		if (isHeaderViewPos(position))
		{
			return headerViewArray.keyAt(position);
		}
		else if (isFooterViewPos(position))
		{
			return footViewArray.keyAt(position - getHeadersCount() - sList.size());
		}
		return mInnerAdapter.getItemViewType(position - getHeadersCount());
	}

	@Override
	public void onBindViewHolder(CommonRecycleViewHolder holder, int position)
	{
		if (isHeaderViewPos(position))
		{
			return;
		}
		if (isFooterViewPos(position))
		{
			return;
		}
		mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
	}

	@Override
	public int getItemCount()
	{
		return getHeadersCount() + getFootersCount() + mInnerAdapter.getItemCount();
	}

	/** 适配 GridLayoutManager */
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView)
	{
		mInnerAdapter.onAttachedToRecyclerView(recyclerView);

		RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

		if (layoutManager instanceof GridLayoutManager)
		{
			final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
			final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();

			gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
			{
				@Override
				public int getSpanSize(int position)
				{
					int viewType = getItemViewType(position);

					if (headerViewArray.get(viewType) != null)
					{
						return gridLayoutManager.getSpanCount();
					}
					else if (footViewArray.get(viewType) != null)
					{
						return gridLayoutManager.getSpanCount();
					}

					if (spanSizeLookup != null)
					{
						return spanSizeLookup.getSpanSize(position);
					}

					return 0;
				}
			});
			
			gridLayoutManager.setSpanCount(gridLayoutManager.getSpanCount());
		}
	}

	// 适配 StaggeredGridLayoutManager
	@Override
	public void onViewAttachedToWindow(CommonRecycleViewHolder holder)
	{
		mInnerAdapter.onViewAttachedToWindow(holder);
		int position = holder.getLayoutPosition();
		if (isHeaderViewPos(position) || isFooterViewPos(position))
		{
			ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
			if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams)
			{
				StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) lp;

				params.setFullSpan(true);
			}
		}
	}

	private boolean isHeaderViewPos(int position)
	{
		return position < getHeadersCount();
	}

	private boolean isFooterViewPos(int position)
	{
		return position >= getHeadersCount() + sList.size();
	}

	public void addHeaderView(View view)
	{
		headerViewArray.put(headerViewArray.size() + BASE_ITEM_TYPE_HEADER, view);
	}

	public void addFootView(View view)
	{
		footViewArray.put(footViewArray.size() + BASE_ITEM_TYPE_FOOTER, view);
	}

	public int getHeadersCount()
	{
		return headerViewArray.size();
	}

	public int getFootersCount()
	{
		return footViewArray.size();
	}
}
