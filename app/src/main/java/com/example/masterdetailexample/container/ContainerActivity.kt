package com.example.masterdetailexample.container

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.masterdetailexample.LOADER_ID_CONTAINER
import com.example.masterdetailexample.R
import com.example.masterdetailexample.basearchitecture.PresenterActivity
import com.example.masterdetailexample.basearchitecture.PresenterFactory
import com.example.masterdetailexample.container.models.ContainerAction
import com.example.masterdetailexample.container.models.ContainerEvent
import com.example.masterdetailexample.container.models.ContainerResult
import com.example.masterdetailexample.container.models.ContainerState
import com.example.masterdetailexample.detail.DetailActivity

class ContainerActivity : PresenterActivity<ContainerPresenter, ContainerEvent, ContainerResult, ContainerAction, ContainerState>() {
    
    override var attachAttempted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
    }

    override fun onResume() {
        super.onResume()
        events.onNext(ContainerEvent.DualPaneUpdate(resources.getBoolean(R.bool.dual_pane)))
    }

    override fun getContext(): Context {
        return baseContext
    }

    override fun loaderId(): Int {
        return LOADER_ID_CONTAINER
    }

    override fun presenterFactory(): PresenterFactory<ContainerPresenter> {
        return object: PresenterFactory<ContainerPresenter>() {
            override fun create(): ContainerPresenter {
                return ContainerPresenter()
            }
        }
    }

    override fun renderViewState(state: ContainerState) {
        if (state.showDetail) {
            events.onNext(ContainerEvent.ShowingDetail())
            val detailIntent = Intent(this, DetailActivity::class.java)
            startActivity(detailIntent)
        }
    }

    override fun setupViewBindings() {
        // No bindings necessary for this view.
    }
}
