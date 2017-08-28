package com.example.masterdetailexample.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.masterdetailexample.App
import com.example.masterdetailexample.LOADER_ID_DETAIL
import com.example.masterdetailexample.R
import com.example.masterdetailexample.addTo
import com.example.masterdetailexample.basearchitecture.PresenterFactory
import com.example.masterdetailexample.basearchitecture.PresenterFragment
import com.example.masterdetailexample.detail.models.DetailAction
import com.example.masterdetailexample.detail.models.DetailEvent
import com.example.masterdetailexample.detail.models.DetailResult
import com.example.masterdetailexample.detail.models.DetailState
import com.jakewharton.rxbinding2.view.clicks
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : PresenterFragment<DetailPresenter, DetailEvent, DetailResult, DetailAction, DetailState>() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_detail, container, false)
    }

    override fun loaderId(): Int {
        return LOADER_ID_DETAIL
    }

    override fun presenterFactory(): PresenterFactory<DetailPresenter> {
        return object: PresenterFactory<DetailPresenter>() {
            override fun create(): DetailPresenter {
                return DetailPresenter(App.itemRepo)
            }
        }
    }

    override fun setupViewBindings() {
        btnEditItem
                .clicks()
                .map { DetailEvent.EditItem() }
                .subscribe { events.onNext(it) }
                .addTo(disposables)

        btnSaveItem
                .clicks()
                .map { DetailEvent.SaveItem(txtItemId.text.toString(), edtItemText.text.toString()) }
                .subscribe { events.onNext(it) }
                .addTo(disposables)

        btnDelete
                .clicks()
                .map { DetailEvent.DeleteItem(txtItemId.text.toString()) }
                .subscribe { events.onNext(it) }
                .addTo(disposables)
    }

    override fun renderViewState(state: DetailState) {

        if (state.item != null) {
            txtItemId.text = state.item.id.toString()
            txtItemText.text = state.item.text
        }
        else {
            txtItemId.text = ""
            txtItemText.text = ""
        }

        if (state.buttonsActive) {
            btnEditItem.visibility = if (state.editing) View.INVISIBLE else View.VISIBLE
            btnSaveItem.visibility = if(state.editing) View.VISIBLE else View.INVISIBLE
            btnDelete.visibility = View.VISIBLE
        }
        else {
            btnEditItem.visibility = View.INVISIBLE
            btnSaveItem.visibility = View.INVISIBLE
            btnDelete.visibility = View.INVISIBLE
        }

        txtItemText.visibility = if (state.editing) View.INVISIBLE else View.VISIBLE
        edtItemText.visibility = if (state.editing) View.VISIBLE else View.INVISIBLE
    }
}