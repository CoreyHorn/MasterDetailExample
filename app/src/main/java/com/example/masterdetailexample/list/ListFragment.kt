package com.example.masterdetailexample.list

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.masterdetailexample.App
import com.example.masterdetailexample.LOADER_ID_MASTER
import com.example.masterdetailexample.R
import com.example.masterdetailexample.addTo
import com.example.masterdetailexample.basearchitecture.PresenterFactory
import com.example.masterdetailexample.basearchitecture.PresenterFragment
import com.example.masterdetailexample.list.models.ListAction
import com.example.masterdetailexample.list.models.ListEvent
import com.example.masterdetailexample.list.models.ListResult
import com.example.masterdetailexample.list.models.ListState
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : PresenterFragment<ListPresenter, ListEvent, ListResult, ListAction, ListState>() {

    private val adapter = ItemAdapter(emptyList())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setHasStableIds(true)
        rcyItems.adapter = adapter
        rcyItems.layoutManager = LinearLayoutManager(context)
    }

    override fun onPause() {
        events.onNext(ListEvent.UpdateListState(rcyItems.layoutManager.onSaveInstanceState()))
        super.onPause()
    }

    override fun loaderId(): Int {
        return LOADER_ID_MASTER
    }

    override fun presenterFactory(): PresenterFactory<ListPresenter> {
        return object: PresenterFactory<ListPresenter>() {
            override fun create(): ListPresenter {
                return ListPresenter(App.itemRepo)
            }
        }
    }

    override fun setupViewBindings() {
        btnSaveItem.clicks()
                .map { edtItemText.text.toString() }
                .map { ListEvent.Create(it, rcyItems.layoutManager.onSaveInstanceState()) }
                .subscribe { events.onNext(it) }
                .addTo(disposables)

        adapter.clicks()
                .map { ListEvent.Select(it, rcyItems.layoutManager.onSaveInstanceState()) }
                .subscribe { events.onNext(it) }
                .addTo(disposables)

        adapter.deletes()
                .map { ListEvent.Delete(it, rcyItems.layoutManager.onSaveInstanceState()) }
                .subscribe { events.onNext(it) }
                .addTo(disposables)
    }

    override fun renderViewState(state: ListState) {
        state.items?.let {
            adapter.updateItems(it)
            state.listState?.let { rcyItems?.layoutManager?.onRestoreInstanceState((it)) }
        }
        if (state.clearText) {
            val currentText = edtItemText.text.toString()
            if (!currentText.isEmpty()) {
                edtItemText?.let { it.setText("") }
                events.onNext(ListEvent.ClearedInput(rcyItems.layoutManager.onSaveInstanceState()))
            }
        }
    }

    companion object {
        fun newInstance(): ListFragment { return ListFragment() }
    }

}